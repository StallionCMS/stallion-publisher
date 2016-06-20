/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
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

package io.stallion.publisher.content;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.StandardModelController;


public class AuthorProfileController extends StandardModelController<AuthorProfile> {
    public static AuthorProfileController instance() {
        return (AuthorProfileController) DataAccessRegistry.instance().get("authors");
    }

    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setBucket("authors")
                .setModelClass(AuthorProfile.class)
                .setControllerClass(AuthorProfileController.class);
        DataAccessRegistry.instance().register(registration);
    }
}
