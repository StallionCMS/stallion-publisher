/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.stallion.publisher;

import io.stallion.Context;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.templating.TemplateRenderer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class AdminEndpoints implements EndpointResource {
    @GET
    @Path("/admin")
    @Produces("text/html")
    public String adminPage() {
        Context.getResponse().getPageFooterLiterals().addDefinedBundle("publisher:admin.js");
        Context.getResponse().getPageHeadLiterals().addDefinedBundle("publisher:admin.css");
        return TemplateRenderer.instance().renderTemplate("publisher:admin.jinja");
    }
}
