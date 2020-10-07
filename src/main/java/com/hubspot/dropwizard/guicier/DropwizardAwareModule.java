package com.hubspot.dropwizard.guicier;

import com.google.inject.Module;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class DropwizardAwareModule<C extends Configuration> implements Module {
    private volatile Bootstrap<C> bootstrap;
    private volatile C configuration;
    private volatile Environment environment;

    protected Bootstrap<C> getBootstrap() {
        return checkNotNull(this.bootstrap, "bootstrap was not set!");
    }

    protected C getConfiguration() {
        return checkNotNull(this.configuration, "configuration was not set!");
    }

    protected Environment getEnvironment() {
        return checkNotNull(this.environment, "environment was not set!");
    }

    public void setBootstrap(Bootstrap<C> bootstrap) {
        checkState(this.bootstrap == null, "bootstrap was already set!");
        this.bootstrap = checkNotNull(bootstrap, "bootstrap is null");
    }

    public void setConfiguration(C configuration) {
        checkState(this.configuration == null, "configuration was already set!");
        this.configuration = checkNotNull(configuration, "configuration is null");
    }

    public void setEnvironment(Environment environment) {
        checkState(this.environment == null, "environment was already set!");
        this.environment = checkNotNull(environment, "environment is null");
    }
}
