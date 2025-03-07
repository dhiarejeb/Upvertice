package com.dhia.listener;

import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeycloakEventListenerFactory implements EventListenerProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakEventListenerFactory.class);
    private static final String PROVIDER_ID = "Upvertice-listener";
    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new KeycloakEventListener(keycloakSession);
    }

    @Override
    public void init(org.keycloak.Config.Scope scope) {
        logger.info("Initializing Custom Keycloak Event Listener");

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        logger.info("Custom Event Listener Post Init");

    }

    @Override
    public void close() {
        logger.info("Closing Custom Event Listener");

    }

    @Override
    public String getId() {
        return PROVIDER_ID ;
    }
}
