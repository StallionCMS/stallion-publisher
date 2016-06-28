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

package io.stallion.publisher.comments;

import io.stallion.exceptions.ClientException;
import io.stallion.publisher.contacts.Contact;
import io.stallion.publisher.contacts.ContactsController;
import io.stallion.publisher.contacts.ContactsController;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.settings.Settings;
import io.stallion.users.Role;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Map;

import static io.stallion.utils.Literals.map;
import static io.stallion.utils.Literals.val;


public class SeleniumEndpoints implements EndpointResource {

    @Path("/selenium/get-contact-secret")
    @GET
    @Produces("application/json")
    @MinRole(Role.ANON)
    public Map getContactSecret(@QueryParam("secret") String secret, @QueryParam("email") String email) {
        if (!Settings.instance().getHealthCheckSecret().equals(secret)) {
            throw new ClientException("Invalid secret");
        }
        if (!email.startsWith("selenium+") || !email.endsWith("@stallion.io")) {
            throw new ClientException("Invalid email address");
        }
        Contact contact = ContactsController.instance().forUniqueKey("email", email);
        return map(val("secret", contact.getSecretToken()));
    }
}
