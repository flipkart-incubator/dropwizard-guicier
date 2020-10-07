/*
 * Copyright 2014-2016 Squarespace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squarespace.jersey2.guice;

import com.google.inject.Binding;
import com.google.inject.Guice;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * An {@link ActiveDescriptor} that is backed by a {@link Guice} {@link Binding}.
 */
class GuiceBindingDescriptor<T> extends AbstractActiveDescriptor<T> {

    private final Class<?> clazz;

    private final Binding<T> binding;

    public GuiceBindingDescriptor(Type type, Class<?> clazz,
                                  Set<Annotation> qualifiers, Binding<T> binding) {
        super(Collections.singleton(type), GuiceScope.class,
                BindingUtils.getNameFromAllQualifiers(qualifiers, clazz),
                qualifiers, DescriptorType.CLASS, DescriptorVisibility.NORMAL,
                0, false, (Boolean) null, (String) null,
                Collections.<String, List<String>>emptyMap());

        this.clazz = clazz;
        this.binding = binding;

        setImplementation(clazz.getName());
    }

    @Override
    public Class<?> getImplementationClass() {
        return clazz;
    }

    @Override
    public Type getImplementationType() {
        return clazz;
    }

    @Override
    public T create(ServiceHandle<?> root) {
        return binding.getProvider().get();
    }

    @Override
    public boolean isReified() {
        return true;
    }

    @Override
    public Set<Annotation> getQualifierAnnotations() {
        Set<Annotation> qualifierAnnotations = super.getQualifierAnnotations();
        return qualifierAnnotations.isEmpty() ? qualifierAnnotations : lenientSet(qualifierAnnotations);
    }

    private static <E> Set<E> lenientSet(Collection<E> entries) {
        LenientHashSet<E> lenientHashSet = new LenientHashSet<>();
        lenientHashSet.addAll(entries);
        return lenientHashSet;
    }

    /**
     * Because sun's runtime version of Annotation doesn't know anything about GuiceQualifier we
     * switch the equality check around so that equals in GuiceQualifier is used instead.
     * Ugly workaround until hk2 supports something similar to guice's AnnotationTypeStrategy.
     */
    @SuppressWarnings("serial")
    private static class LenientHashSet<E> extends HashSet<E> {
        @Override
        public boolean containsAll(Collection<?> foreignEntries) {
            for (Object foreignEntry : foreignEntries) {
                boolean matched = false;
                for (E containedEntry : this) {
                    if (containedEntry.equals(foreignEntry)) {
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    return false;
                }
            }
            return true;
        }
    }
}