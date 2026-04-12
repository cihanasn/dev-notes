package com.cihan.keycloak.otp;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class OtpAuthenticator implements Authenticator {

    private static final String HARDCODED_OTP = "123456";
    private static final String OTP_FORM_TEMPLATE = "otp-form.ftl";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
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

        if (HARDCODED_OTP.equals(enteredOtp)) {
            context.success();
        } else {
            Response response = context.form()
                .setError("Hatalı OTP kodu. Lütfen tekrar deneyin.")
                .createForm(OTP_FORM_TEMPLATE);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, response);
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