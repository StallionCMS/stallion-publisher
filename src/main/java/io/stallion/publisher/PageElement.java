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

package io.stallion.publisher;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.util.List;
import java.util.Map;

import io.stallion.services.Log;


public class PageElement {
    private String content = "";
    private String rawContent = "";
    private String type = "";
    private Map<String, Object> data = map();
    private String name = "";
    private List<Map> widgets = list();

    public String getContent() {
        return content;
    }

    public PageElement setContent(String content) {
        this.content = content;
        return this;
    }

    public String getRawContent() {
        return rawContent;
    }

    public PageElement setRawContent(String rawContent) {
        this.rawContent = rawContent;
        return this;
    }

    public String getType() {
        return type;
    }

    public PageElement setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public PageElement setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public String getName() {
        return name;
    }

    public PageElement setName(String name) {
        this.name = name;
        return this;
    }

    public List<Map> getWidgets() {
        return widgets;
    }

    public PageElement setWidgets(List<Map> widgets) {
        this.widgets = widgets;
        return this;
    }
}
