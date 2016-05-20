/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.stallion.publisher;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.DataAccessRegistration;
import io.stallion.dataAccess.StandardModelController;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class FormSubmissionController extends StandardModelController<FormSubmission> {
    public static FormSubmissionController instance() {
        return (FormSubmissionController) DataAccessRegistry.instance().get("form_submissions");
    }
    public static void register() {
        DataAccessRegistration registration = new DataAccessRegistration()
                .setBucket("form_submissions")
                .setModelClass(FormSubmission.class)
                .setControllerClass(FormSubmissionController.class);
        DataAccessRegistry.instance().register(registration);
    }
}
