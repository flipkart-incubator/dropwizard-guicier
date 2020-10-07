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

import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Providers;

class Items {

    @Inject
    public ServiceLocator locator;

    @Inject
    public Application application;

    @Inject
    public Providers providers;

    @Inject
    public UriInfo uriInfo;

    @Inject
    public HttpHeaders headers;

    @Inject
    public SecurityContext securityContext;

    @Inject
    public Request request;
}
