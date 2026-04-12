package com.cihan.keycloak.otp;

import java.util.Collections;
import java.util.List;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class OtpAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "turkce-otp";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new OtpAuthenticator();
    }

    @Override
    public String getId() { return ID; }

    @Override
    public String getDisplayType() { return "Türkçe OTP Doğrulama"; }

    @Override
    public String getHelpText() { return "Email ile OTP doğrulama"; }

    @Override
    public String getReferenceCategory() { return "otp"; }

    @Override
    public boolean isConfigurable() { return false; }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[]{
            Requirement.REQUIRED,
            Requirement.ALTERNATIVE,
            Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() { return false; }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}