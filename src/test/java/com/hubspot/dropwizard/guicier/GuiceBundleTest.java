package com.hubspot.dropwizard.guicier;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guicier.objects.*;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuiceBundleTest {

    private Environment environment;
    private GuiceBundle<Configuration> guiceBundle;

    @After
    public void tearDown() {
        JerseyGuiceUtils.reset();
    }

    @Before
    public void setUp() throws Exception {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        environment = new Environment("test env", objectMapper, null, new MetricRegistry(), null);
        guiceBundle = GuiceBundle.defaultBuilder(Configuration.class)
                .modules(new TestModule())
                .build();
        Bootstrap bootstrap = mock(Bootstrap.class);
        when(bootstrap.getObjectMapper()).thenReturn(objectMapper);
        guiceBundle.initialize(bootstrap);
        guiceBundle.run(new Configuration(), environment);
    }

    @Test
    public void createsInjectorWhenInit() throws ServletException {
        Injector injector = guiceBundle.getInjector();
        assertThat(injector).isNotNull();
    }

    @Test
    public void serviceLocatorIsAvaliable() throws ServletException {
        ServiceLocator serviceLocator = guiceBundle.getInjector().getInstance(ServiceLocator.class);
        assertThat(serviceLocator).isNotNull();
    }

    @Test
    public void itAddsBoundManaged() {
        InjectedManaged injectedManaged = guiceBundle.getInjector().getInstance(InjectedManaged.class);
        assertThat(environment.lifecycle().getManagedObjects())
                .extracting("managed")
                .containsOnlyOnce(injectedManaged);
    }

    @Test
    public void itAddsInstanceManaged() {
        InstanceManaged instanceManaged = guiceBundle.getInjector().getInstance(InstanceManaged.class);
        assertThat(environment.lifecycle().getManagedObjects())
                .extracting("managed")
                .containsOnlyOnce(instanceManaged);
    }

    @Test
    public void itAddsProviderManagedSingleton() {
        ProviderManaged providerManaged = guiceBundle.getInjector().getInstance(ProviderManaged.class);
        assertThat(environment.lifecycle().getManagedObjects())
                .extracting("managed")
                .containsOnlyOnce(providerManaged);
    }

    @Test
    public void itAddsBoundTask() {
        InjectedTask injectedTask = guiceBundle.getInjector().getInstance(InjectedTask.class);
        assertThat(environment.admin())
                .extracting("tasks")
                .flatExtracting("tasks")
                .containsOnlyOnce(injectedTask);
    }

    @Test
    public void itAddsBoundHealthCheck() {
        assertThat(environment.healthChecks().getNames())
                .containsOnlyOnce(InjectedHealthCheck.class.getSimpleName());
    }

    @Test
    public void itAddsBoundServerLifecycleListener() {
        InjectedServerLifecycleListener injectedServerLifecycleListener =
                guiceBundle.getInjector().getInstance(InjectedServerLifecycleListener.class);
        assertThat(environment.lifecycle())
                .extracting(Function.identity())
                .flatExtracting("lifecycleListeners")
                .extracting("listener")
                .containsOnlyOnce(injectedServerLifecycleListener);
    }

    @Test
    public void itAddsBoundProvider() {
        Set<Class<?>> components = environment.jersey().getResourceConfig().getClasses();
        assertThat(components).containsOnlyOnce(InjectedProvider.class);
    }

    @Test
    public void itAddsBoundResource() {
        Set<Class<?>> resourceClasses = environment.jersey().getResourceConfig().getClasses();
        assertThat(resourceClasses).containsOnlyOnce(ExplicitResource.class);
    }

    @Test
    public void itAddsProvidedManaged() {
        ProvidedManaged providedManaged = guiceBundle.getInjector().getInstance(ProvidedManaged.class);
        assertThat(environment.lifecycle().getManagedObjects())
                .extracting("managed")
                .containsOnlyOnce(providedManaged);
    }

    @Test
    public void itAddsProvidedTask() {
        ProvidedTask providedTask = guiceBundle.getInjector().getInstance(ProvidedTask.class);
        assertThat(environment.admin())
                .extracting("tasks")
                .flatExtracting("tasks")
                .containsOnlyOnce(providedTask);
    }

    @Test
    public void itAddsProvidedHealthCheck() {
        assertThat(environment.healthChecks().getNames())
                .containsOnlyOnce(ProvidedHealthCheck.class.getSimpleName());
    }

    @Test
    public void itAddsProvidedServerLifecycleListener() {
        ProvidedServerLifecycleListener providedServerLifecycleListener =
                guiceBundle.getInjector().getInstance(ProvidedServerLifecycleListener.class);
        assertThat(environment.lifecycle())
                .extracting(Function.identity())
                .flatExtracting("lifecycleListeners")
                .extracting("listener")
                .containsOnlyOnce(providedServerLifecycleListener);
    }

    @Test
    public void itAddsProvidedProvider() {
        Set<Class<?>> components = environment.jersey().getResourceConfig().getClasses();
        assertThat(components).containsOnlyOnce(ProvidedProvider.class);
    }
}
