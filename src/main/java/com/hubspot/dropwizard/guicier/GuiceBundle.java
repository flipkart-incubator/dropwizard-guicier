package com.hubspot.dropwizard.guicier;

import com.google.common.collect.ImmutableSet;
import com.google.inject.*;
import com.google.inject.servlet.*;
import com.squarespace.jersey2.guice.JerseyGuiceModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author <a href="mailto:henning@schmiedehausen.org">Henning P. Schmiedehausen</a>
 */
public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private static final Logger LOG = LoggerFactory.getLogger(GuiceBundle.class);

    public static <U extends Configuration> Builder<U> defaultBuilder(final Class<U> configClass) {
        return new Builder<>(configClass);
    }

    private final Class<T> configClass;
    private final ImmutableSet<DropwizardAwareModule<T>> dropwizardAwareModules;
    private final ImmutableSet<Module> guiceModules;
    private final Stage guiceStage;
    private final boolean allowUnknownFields;
    private final boolean enableGuiceEnforcer;
    private final InjectorFactory injectorFactory;

    private Bootstrap<T> bootstrap;
    private Injector injector;

    private GuiceBundle(final Class<T> configClass,
                        final ImmutableSet<Module> guiceModules,
                        final ImmutableSet<DropwizardAwareModule<T>> dropwizardAwareModules,
                        final Stage guiceStage,
                        final boolean allowUnknownFields,
                        final boolean enableGuiceEnforcer,
                        final InjectorFactory injectorFactory) {
        this.configClass = configClass;

        this.guiceModules = guiceModules;
        this.dropwizardAwareModules = dropwizardAwareModules;
        this.guiceStage = guiceStage;
        this.allowUnknownFields = allowUnknownFields;
        this.enableGuiceEnforcer = enableGuiceEnforcer;
        this.injectorFactory = injectorFactory;
    }

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        this.bootstrap = (Bootstrap<T>) bootstrap;
        if (allowUnknownFields) {
            AllowUnknownFieldsObjectMapper.applyTo(bootstrap);
        }
    }

    @Override
    public void run(final T configuration, final Environment environment) throws Exception {
        for (DropwizardAwareModule<T> dropwizardAwareModule : dropwizardAwareModules) {
            dropwizardAwareModule.setBootstrap(bootstrap);
            dropwizardAwareModule.setConfiguration(configuration);
            dropwizardAwareModule.setEnvironment(environment);
        }

        final DropwizardModule dropwizardModule = new DropwizardModule(environment);
        // We assume that the next service locator will be the main application one
        final String serviceLocatorName = getNextServiceLocatorName();
        ImmutableSet.Builder<Module> modulesBuilder =
                ImmutableSet.<Module>builder()
                        .addAll(guiceModules)
                        .addAll(dropwizardAwareModules)
                        .add(new ServletModule())
                        .add(dropwizardModule)
                        .add(new JerseyGuiceModule(serviceLocatorName))
                        .add(new JerseyGuicierModule())
                        .add(binder -> {
                            binder.bind(Environment.class).toInstance(environment);
                            binder.bind(configClass).toInstance(configuration);
                        });
        if (enableGuiceEnforcer) {
            modulesBuilder.add(new GuiceEnforcerModule());
        }
        this.injector = injectorFactory.create(guiceStage, modulesBuilder.build());

        JerseyGuiceUtils.install((name, parent) -> {
            if (!name.startsWith("__HK2_")) {
                return null;
            } else if (serviceLocatorName.equals(name)) {
                return injector.getInstance(ServiceLocator.class);
            } else {
                LOG.debug("Returning a new ServiceLocator for name '{}'", name);
                return JerseyGuiceUtils.newServiceLocator(name, parent);
            }
        });

        dropwizardModule.register(injector);

        environment.servlets().addFilter("Guice Filter", GuiceFilter.class).addMappingForUrlPatterns(null, false, "/*");
        environment.servlets().addServletListeners(new GuiceServletContextListener() {

            @Override
            protected Injector getInjector() {
                return injector;
            }
        });
    }

    public Injector getInjector() {
        return checkNotNull(injector, "injector has not been initialized yet");
    }

    public static class GuiceEnforcerModule implements Module {
        @Override
        public void configure(final Binder binder) {
            binder.disableCircularProxies();
            binder.requireExplicitBindings();
            binder.requireExactBindingAnnotations();
            binder.requireAtInjectOnConstructors();
        }
    }

    public static class Builder<U extends Configuration> {
        private final Class<U> configClass;
        private final ImmutableSet.Builder<Module> guiceModules = ImmutableSet.builder();
        private final ImmutableSet.Builder<DropwizardAwareModule<U>> dropwizardAwareModules = ImmutableSet.builder();
        private Stage guiceStage = Stage.PRODUCTION;
        private boolean allowUnknownFields = true;
        private boolean enableGuiceEnforcer = true;
        private InjectorFactory injectorFactory = Guice::createInjector;

        private Builder(final Class<U> configClass) {
            this.configClass = configClass;
        }

        public final Builder<U> stage(final Stage guiceStage) {
            checkNotNull(guiceStage, "guiceStage is null");
            this.guiceStage = guiceStage;
            return this;
        }

        public final Builder<U> allowUnknownFields(final boolean allowUnknownFields) {
            this.allowUnknownFields = allowUnknownFields;
            return this;
        }

        public final Builder<U> enableGuiceEnforcer(final boolean enableGuiceEnforcer) {
            this.enableGuiceEnforcer = enableGuiceEnforcer;
            return this;
        }

        public final Builder<U> modules(final Module... modules) {
            return modules(Arrays.asList(modules));
        }

        @SuppressWarnings("unchecked")
        public final Builder<U> modules(final Iterable<? extends Module> modules) {
            for (Module module : modules) {
                if (module instanceof DropwizardAwareModule<?>) {
                    dropwizardAwareModules.add((DropwizardAwareModule<U>) module);
                } else {
                    guiceModules.add(module);
                }
            }
            return this;
        }

        public final Builder<U> injectorFactory(final InjectorFactory injectorFactory) {
            this.injectorFactory = injectorFactory;
            return this;
        }

        public final GuiceBundle<U> build() {
            return new GuiceBundle<>(configClass,
                    guiceModules.build(),
                    dropwizardAwareModules.build(),
                    guiceStage,
                    allowUnknownFields,
                    enableGuiceEnforcer,
                    injectorFactory);
        }
    }

    private static String getNextServiceLocatorName() {
        Class<ServiceLocatorFactoryImpl> factoryClass = ServiceLocatorFactoryImpl.class;
        try {
            Field nameCountField = factoryClass.getDeclaredField("name_count");
            nameCountField.setAccessible(true);
            int count = (int) nameCountField.get(null);

            Field namePrefixField = factoryClass.getDeclaredField("GENERATED_NAME_PREFIX");
            namePrefixField.setAccessible(true);
            String prefix = (String) namePrefixField.get(null);

            return prefix + count;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
