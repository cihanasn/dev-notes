package com.cihan.keycloak.otp;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;



public class OtpAuthenticator implements Authenticator {

    private static final String OTP_FORM_TEMPLATE = "otp-form.ftl";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
    	
        String otp = String.valueOf(
            ThreadLocalRandom.current().nextInt(100000, 999999)
        );
        
        context.getAuthenticationSession()
        	.setAuthNote("emailOtp", otp);
        
        context.getAuthenticationSession()
	        .setAuthNote("emailOtpExpiry",
	            String.valueOf(System.currentTimeMillis() + (5 * 60 * 1000)));

        UserModel user = context.getUser();
        
        try {

            EmailTemplateProvider emailProvider =
                context.getSession().getProvider(EmailTemplateProvider.class);

            Map<String, Object> params = new HashMap<>();
            params.put("otp", otp);
            
            emailProvider
	            .setRealm(context.getRealm())
	            .setUser(user)
	            .send(
	                "OTP Doğrulama Kodunuz",
	                "otp-email.ftl",
	                params
	            );

        } catch (EmailException e) {

        	System.out.println("Email gönderilemedi: " + e.getMessage());
        	
            context.failure(
                AuthenticationFlowError.INTERNAL_ERROR
            );

            return;
        }
        
        // Formu göster
        Response response = context.form()
            .createForm(OTP_FORM_TEMPLATE);
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // Kullanıcının girdiği kodu al
        MultivaluedMap<String, String> formData =
            context.getHttpRequest().getDecodedFormParameters();
        String enteredOtp = formData.getFirst("otp");

        String sessionOtp =
                context.getAuthenticationSession()
                    .getAuthNote("emailOtp");
        
        String expiry =
                context.getAuthenticationSession()
                    .getAuthNote("emailOtpExpiry");
 
        if (expiry == null ||
            System.currentTimeMillis() > Long.parseLong(expiry)) {

            Response response = context.form()
                .setError("OTP süresi doldu")
                .createForm(OTP_FORM_TEMPLATE);

            context.failureChallenge(
                AuthenticationFlowError.EXPIRED_CODE,
                response
            );

            return;
        }
        
        
        if (sessionOtp != null &&
            sessionOtp.equals(enteredOtp)) {

            context.success();

        } else {

            Response response = context.form()
                .setError("Hatalı OTP")
                .createForm(OTP_FORM_TEMPLATE);

            context.failureChallenge(
                AuthenticationFlowError.INVALID_CREDENTIALS,
                response
            );
        }
    }

    @Override
    public boolean requiresUser() { return true; }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {}

    @Override
    public void close() {}
}