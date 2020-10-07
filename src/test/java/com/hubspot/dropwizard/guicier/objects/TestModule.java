package com.hubspot.dropwizard.guicier.objects;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import javax.inject.Singleton;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExplicitDAO.class);
        bindConstant().annotatedWith(Names.named("TestTaskName")).to("injected task");
        bindConstant().annotatedWith(Names.named("ProvidedTaskName")).to("provided task");

        bind(InstanceManaged.class).toInstance(new InstanceManaged());
        bind(ProviderManaged.class).toProvider(ProviderManagedProvider.class).in(Scopes.SINGLETON);

        bind(InjectedManaged.class).asEagerSingleton();
        bind(InjectedTask.class).asEagerSingleton();
        bind(InjectedHealthCheck.class).asEagerSingleton();
        bind(InjectedServerLifecycleListener.class).asEagerSingleton();

        bind(InjectedProvider.class);

        bind(ExplicitResource.class);
        bind(JerseyContextResource.class);
    }

    @Provides
    @Singleton
    public ProvidedManaged provideManaged() {
        return new ProvidedManaged();
    }

    @Provides
    @Singleton
    public ProvidedTask provideTask(@Named("ProvidedTaskName") String name) {
        return new ProvidedTask(name);
    }

    @Provides
    @Singleton
    public ProvidedHealthCheck provideHealthCheck() {
        return new ProvidedHealthCheck();
    }

    @Provides
    @Singleton
    public ProvidedServerLifecycleListener provideServerLifecycleListener() {
        return new ProvidedServerLifecycleListener();
    }

    @Provides
    public ProvidedProvider provideProvider() {
        return new ProvidedProvider();
    }
}

