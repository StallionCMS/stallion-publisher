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

import io.stallion.dataAccess.filtering.Or;
import io.stallion.publisher.content.SiteSettingsStatic;
import io.stallion.publisher.content.TemplateConfig;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MapParam;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.services.Log;
import io.stallion.users.Role;
import io.stallion.users.UserController;

import javax.ws.rs.*;


@MinRole(Role.STAFF_LIMITED)
@Produces("application/json")
@Path("/config")
public class ConfigEndpoints implements EndpointResource {


    @GET
    @Path("/site-settings")
    public Map getSiteSettings() {
        return SiteSettingsStatic.getAllSettings();
    }


    @POST
    @Path("/update-site-settings")
    public Object updateSiteSettings(@MapParam Map<String, String> siteSettings) {
        SiteSettingsStatic.putSettings(siteSettings);
        return true;
    }

}
