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

package io.stallion.publisher.content;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stallion.dataAccess.db.converters.AttributeConverter;
import io.stallion.services.Log;
import io.stallion.utils.json.JSON;


public class UploadedFileConverter implements AttributeConverter<UploadedFile, String> {

    @Override
    public String convertToDatabaseColumn(UploadedFile dbData) {
        try {
            if (dbData == null) {
                return "";
            }
            return JSON.stringify(dbData);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UploadedFile convertToEntityAttribute(String json) {
        if (empty(json)) {
            return null;
        }
        return JSON.parse(json, UploadedFile.class);
    }
}

