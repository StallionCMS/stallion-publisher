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

import io.stallion.settings.Settings;

import java.util.List;


public class SubscriptionVerifyEmailer extends ContactEmailer {
    public SubscriptionVerifyEmailer(Contact contact) {
        super(contact);
        List<Subscription> subscriptions = SubscriptionController.instance()
                .filterByKey("contactId", contact.getId().toString())
                .filter("enabled", true)
                .all();
        put("subscriptions", subscriptions);
    }


    @Override
    public boolean isTransactional() {
        return true;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/subscription-verify-email.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "Verify your email subscriptions to " + Settings.instance().getSiteName();
    }


}
