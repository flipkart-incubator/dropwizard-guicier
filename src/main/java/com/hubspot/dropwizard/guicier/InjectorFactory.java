package com.hubspot.dropwizard.guicier;

import com.google.inject.*;
import com.google.inject.util.Modules;

public interface InjectorFactory {
    Injector create(Stage stage, Module module);

    default Injector create(Stage stage, Iterable<? extends Module> modules) {
        return create(stage, Modules.combine(modules));
    }
}
