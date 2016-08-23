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


public class FormSubmissionController extends StandardModelController<FormSubmission> {
    public static FormSubmissionController instance() {
        return (FormSubmissionController) DataAccessRegistry.instance().get("form_submissions");
    }
    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setBucket("form_submissions")
                .setTableName("stallion_publisher_form_submissions")
                .setModelClass(FormSubmission.class)
                .setControllerClass(FormSubmissionController.class);
        DataAccessRegistry.instance().register(registration);
    }
}
