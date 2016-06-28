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

import io.stallion.email.ContactableEmailer;
import io.stallion.users.User;

import static io.stallion.utils.Literals.or;

public class FormSubmissionEmailer extends ContactableEmailer {
    private FormSubmission submission;
    private Contact contact;

    public FormSubmissionEmailer(User user, FormSubmission submission, Contact contact) {
        super(user);
        this.contact = contact;
        this.submission = submission;
        put("contact", contact);
        put("submission", submission);
    }

    @Override
    public boolean isTransactional() {
        return false;
    }

    @Override
    public String getTemplate() {
        return getClass().getResource("/templates/form-submission-notify.jinja").toString();
    }

    @Override
    public String getSubject() {
        return "New form submission: " + or(contact.getEmail(), "(no email)") + " on page " + or(submission.getPageTitle(), "(no page title)");
    }

}

