/*
 * Copyright (c) 2015-2016 Stallion Software LLC
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International license
 * along with this program. If not, see <https://creativecommons.org/licenses/by-nc-sa/4.0/>.
 */

package io.stallion.publisher;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.util.List;
import java.util.Map;

import io.stallion.Context;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.users.Role;
import io.stallion.utils.Sanitize;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@MinRole(Role.STAFF_LIMITED)
@Produces("application/json")
public class MainEndpoints implements EndpointResource {

    @GET
    @Path("/dashboard")
    @Produces("text/html")
    public String dashboard() {
        //Context.getResponse().getPageFooterLiterals().addDefinedBundle("admin3-js");
        //Context.getResponse().getPageHeadLiterals().addDefinedBundle("admin3-css");
        Map pageContext = map();
        pageContext.put("siteUrl", Settings.instance().getSiteUrl());
        pageContext.put("user", Context.getUser());
        pageContext.put("useMarkdown", PublisherSettings.getInstance().isUseMarkdown());
        Map ctx = map(val("adminContextJson", Sanitize.htmlSafeJson(pageContext)));
        return TemplateRenderer.instance().renderTemplate("publisher:admin3.jinja", ctx);
    }


}
