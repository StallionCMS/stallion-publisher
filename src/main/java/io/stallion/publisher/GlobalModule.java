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

import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.UniqueKey;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonListConverter;
import io.stallion.services.Log;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name="global_modules")
public class GlobalModule extends ModelBase {
    private String name = "";
    private String rawContent = "";
    private String content = "";
    private List<Map> widgets = list();
    private String type = "";
    private String description = "";
    private String previewTemplate = "";

    @Column
    @UniqueKey
    public String getName() {
        return name;
    }

    public GlobalModule setName(String name) {
        this.name = name;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getRawContent() {
        return rawContent;
    }

    public GlobalModule setRawContent(String rawContent) {
        this.rawContent = rawContent;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getContent() {
        return content;
    }

    public GlobalModule setContent(String content) {
        this.content = content;
        return this;
    }

    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonListConverter.class)
    public List<Map> getWidgets() {
        return widgets;
    }

    public GlobalModule setWidgets(List<Map> widgets) {
        this.widgets = widgets;
        return this;
    }

    @Column
    public String getType() {
        return type;
    }

    public GlobalModule setType(String type) {
        this.type = type;
        return this;
    }

    @Column
    public String getDescription() {
        return description;
    }

    public GlobalModule setDescription(String description) {
        this.description = description;
        return this;
    }

    @Column
    public String getPreviewTemplate() {
        return previewTemplate;
    }

    public GlobalModule setPreviewTemplate(String previewTemplate) {
        this.previewTemplate = previewTemplate;
        return this;
    }
}
