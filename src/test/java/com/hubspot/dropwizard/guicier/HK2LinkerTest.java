package com.hubspot.dropwizard.guicier;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;
import com.hubspot.dropwizard.guicier.objects.*;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.*;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HK2LinkerTest {

    private Injector injector;
    private ServiceLocator serviceLocator;

    @Before
    public void setup() throws Exception {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        Environment environment = new Environment("test env", objectMapper, null, new MetricRegistry(), null);
        GuiceBundle<Configuration> guiceBundle = GuiceBundle.defaultBuilder(Configuration.class)
                .modules(new TestModule())
                .build();
        Bootstrap bootstrap = mock(Bootstrap.class);
        when(bootstrap.getObjectMapper()).thenReturn(objectMapper);
        guiceBundle.initialize(bootstrap);
        guiceBundle.run(new Configuration(), environment);

        injector = guiceBundle.getInjector();
        serviceLocator = injector.getInstance(ServiceLocator.class);
    }

    @AfterClass
    public static void tearDown() {
        JerseyGuiceUtils.reset();
    }

    @Test
    public void explicitGuiceBindingsAreBridgedToHk2() throws ServletException {
        ExplicitResource resource = serviceLocator.createAndInitialize(ExplicitResource.class);

        assertThat(resource).isNotNull();
        assertThat(resource.getDAO()).isNotNull();
    }

    @Test
    public void contextBindingsAreBridgedToGuice() {
        for (Class<?> clazz : HK2ContextBindings.SET) {
            Binding<?> binding = injector.getExistingBinding(Key.get(clazz));
            assertThat(binding)
                    .as("%s has a Guice binding", clazz.getName())
                    .isNotNull();
        }
    }
}
