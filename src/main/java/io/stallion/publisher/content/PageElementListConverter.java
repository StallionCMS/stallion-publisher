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

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.stallion.dataAccess.db.converters.JsonAttributeConverter;
import io.stallion.services.Log;
import io.stallion.utils.json.JSON;


public class PageElementListConverter  implements JsonAttributeConverter<List> {

    public String convertToDatabaseColumn(List attr) {
        return JSON.stringify(attr);
    }


    public List convertToEntityAttribute(String dbData) {
        if (empty(dbData)) {
            return list();
        }
        List<PageElement> elements = JSON.parse(dbData, new TypeReference<ArrayList<PageElement>>(){});
        return elements;
    }
}

