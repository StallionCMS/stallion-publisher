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

package io.stallion.publisher.contacts;

import io.stallion.asyncTasks.AsyncCoordinator;
import io.stallion.asyncTasks.AsyncTaskHandlerBase;
import io.stallion.dataAccess.Model;
import io.stallion.publisher.PublisherSettings;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.users.User;
import io.stallion.users.UserController;

import java.util.List;


public class FormSubmissionEmailTaskHandler extends AsyncTaskHandlerBase {
    private Long submissionId;

    public static void enqueue(FormSubmission submission) {
        FormSubmissionEmailTaskHandler handler = new FormSubmissionEmailTaskHandler();
        handler.setSubmissionId(submission.getId());
        AsyncCoordinator.instance().enqueue(handler, "new-submission-email-" + submission.getId(), 0);
    }


    public void process() {
        FormSubmission submission = FormSubmissionController.instance().forId(submissionId);
        Contact contact = ContactsController.instance().forId(submission.getContactId());
        if (contact == null) {
            Log.info("Contact is null!! for contact id {0}", submission.getContactId());
            contact = new Contact().setEmail(submission.getEmail());
        }
        if (Settings.instance().getEmail() == null) {
            Log.warn("Cannot send form submission email because email is not configured!");
        }
        List<String> notifyEmails = PublisherSettings.getInstance().getNotifyEmails();
        if (notifyEmails.size() == 0) {
            notifyEmails = Settings.instance().getEmail().getAdminEmails();
        }
        Log.info("Mail submission to to moderators submission={0} moderators={1}", submission.getId(), notifyEmails);
        for(String email: notifyEmails) {
            Model m = null;
            if (UserController.instance() != null) {
                m = UserController.instance().forUniqueKey("email", email);
            }
            User user;
            if (m == null) {
                user = new User().setEmail(email);
            } else {
                user = (User)m;
            }
            FormSubmissionEmailer emailer = new FormSubmissionEmailer(user, submission, contact);
            Log.info("Send form submission email. submission={0} moderator={0}", submission.getId(), user.getEmail());
            emailer.sendEmail();
        }
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }
}

