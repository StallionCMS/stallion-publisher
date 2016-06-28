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

import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.utils.DateUtils;

import java.time.ZonedDateTime;


public class SubscriptionVerifyTaskHandler extends AsyncTaskHandlerBase {
    private Long contactId;


    public void process() {
        Contact contact = ContactsController.instance().forId(contactId);
        ZonedDateTime now = DateUtils.utcNow();
        ZonedDateTime rejectedAt = DateUtils.milsToDateTime(contact.getVerifyRejectedAt());
        ZonedDateTime sentAt = DateUtils.milsToDateTime(contact.getVerifySentAt());
        // We recently rejected a verification attempt, do not resent
        if (rejectedAt.plusDays(7).isAfter(now)) {
            return;
        }
        // We already sent a verification attempt in the last 24 hours, do not resend
        if (sentAt.plusDays(1).isAfter(now)) {
            return;
        }
        contact.setVerifySentAt(now.toInstant().toEpochMilli());
        new SubscriptionVerifyEmailer(contact).sendEmail();
    }

    public Long getContactId() {
        return contactId;
    }

    public SubscriptionVerifyTaskHandler setContactId(Long contactId) {
        this.contactId = contactId;
        return this;
    }
}
