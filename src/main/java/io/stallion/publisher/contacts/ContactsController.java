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

package io.stallion.publisher.contacts;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.StandardModelController;
import io.stallion.utils.GeneralUtils;

import static io.stallion.utils.Literals.empty;
import static io.stallion.utils.Literals.emptyInstance;


public class ContactsController extends StandardModelController<Contact> {
    public static ContactsController instance() {
        return (ContactsController) DataAccessRegistry.instance().get("contacts");
    }

    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setBucket("contacts")
                .setTableName("stallion_publisher_contacts")
                .setModelClass(Contact.class)
                .setControllerClass(ContactsController.class);
        DataAccessRegistry.instance().register(registration);
    }

    public Contact getOrCreate(Contact contact) {
        Contact existing = forUniqueKey("email", contact.getEmail());
        if (!emptyInstance(existing)) {
            return existing;
        }
        save(contact);
        return contact;
    }


    @Override
    public void onPreSavePrepare(Contact contact) {
        if (empty(contact.getSecretToken())) {
            contact.setSecretToken(GeneralUtils.randomToken(20));
        }

    }


}
