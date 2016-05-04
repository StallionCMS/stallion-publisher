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

import io.stallion.dal.DalRegistry;
import io.stallion.dal.base.DalRegistration;
import io.stallion.dal.base.StandardDisplayableModel;
import io.stallion.dal.base.StandardModelController;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class UploadedFileController extends StandardModelController<UploadedFile> {
    public static UploadedFileController instance() {
        return (UploadedFileController) DalRegistry.instance().get("uploaded_files");
    }
    public static void register() {
        DalRegistration registration = new DalRegistration()
                .setBucket("uploaded_files")
                .setModelClass(UploadedFile.class)
                .setControllerClass(UploadedFileController.class);
        DalRegistry.instance().registerDal(registration);
    }
}
