package com.example.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.UserModel;

import jakarta.ws.rs.core.MultivaluedMap;

import org.keycloak.authentication.AuthenticationFlowError;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

public class MpinAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
    	
    	 if (context.getUser() == null) {
    	        context.attempted();  // Skips this step if no user is set yet
    	        return;
    	    }
    	
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        System.out.println("formData Map");
     // Print Map contents
        for (Entry<String, List<String>> entry : formData.entrySet()) {
        	
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        
		//var formData = decodedFormParameters;
        if (formData.containsKey("mpin")) {
            String enteredMpin = formData.getFirst("mpin");
            UserModel user = context.getUser();
            String storedMpin = user.getFirstAttribute("mpin");

            if (storedMpin != null && storedMpin.equals(enteredMpin)) {
                context.success();
            } else {
                context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
            }
        } else {
            jakarta.ws.rs.core.Response challenge = context.form()
                .setAttribute("username", context.getUser().getUsername())
                .createForm("mpin.ftl");
            context.challenge(challenge);
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        // No action required
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(org.keycloak.models.KeycloakSession session,
                                 org.keycloak.models.RealmModel realm,
                                 UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(org.keycloak.models.KeycloakSession session,
                                   org.keycloak.models.RealmModel realm,
                                   UserModel user) {
        // No required actions
    }

    @Override
    public void close() {
        // No resources to close
    }
}
