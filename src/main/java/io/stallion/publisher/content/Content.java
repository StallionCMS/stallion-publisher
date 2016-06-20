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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.stallion.dataAccess.ModelController;
import io.stallion.dataAccess.StandardDisplayableModel;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonListConverter;

import javax.persistence.Column;
import javax.persistence.Table;

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

@Table(name = "contents")
public class Content extends StandardDisplayableModel {
    private Long authorId = 0L;
    private Long updatedAt = 0L;
    private List<Map> widgets = list();
    private Boolean initialized = true;
    private String type = "post";
    private Long blogId = 0L;
    private String headHtml = "";
    private String footerHtml = "";
    private List<PageElement> elements = list();


    @Column
    public Long getAuthorId() {
        return authorId;
    }

    public Content setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }


    public String getPreviewUrl() {
        return getPermalink() + "?stPreview=" + getPreviewKey();
    }

    public Content setPreviewUrl(String noop) {
        return this;
    }


    @Column
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Content setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getHeadHtml() {
        return headHtml;
    }

    public Content setHeadHtml(String headHtml) {
        this.headHtml = headHtml;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getFooterHtml() {
        return footerHtml;
    }

    public Content setFooterHtml(String footerHtml) {
        this.footerHtml = footerHtml;
        return this;
    }


    //@JsonGetter("currentlyPublished")
    //private boolean currentlyPublished = false;


    public boolean isCurrentlyPublished() {
        return getPublished();
    }

    public Content setCurrentlyPublished(boolean currentlyPublished) {
        //this.currentlyPublished = currentlyPublished;
        return this;
    }

    @Override
    @JsonIgnore
    public ModelController getController() {
        return ContentController.instance();
    }

    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonListConverter.class)
    public List<Map> getWidgets() {
        return widgets;
    }

    public Content setWidgets(List<Map> widgets) {
        this.widgets = widgets;
        return this;
    }

    @Column
    public Boolean getInitialized() {
        return initialized;
    }

    public Content setInitialized(Boolean initialized) {
        this.initialized = initialized;
        return this;
    }

    @Column
    public String getType() {
        return type;
    }


    public Content setType(String type) {
        this.type = type;
        return this;
    }

    @Column
    public Long getBlogId() {
        return blogId;
    }

    public Content setBlogId(Long blogId) {
        this.blogId = blogId;
        return this;
    }

    public PageElement getElement(String name) {
        for(PageElement el: getElements()) {
            if (name.equals(el.getName())) {
                return el;
            }
        }
        return null;
    }

    @Column(columnDefinition = "longtext")
    @Converter(cls=PageElementListConverter.class)
    @JsonDeserialize(contentAs=PageElement.class)
    public List<PageElement> getElements() {
        return elements;
    }

    @JsonDeserialize(contentAs=PageElement.class)
    public Content setElements(List<PageElement> elements) {
        this.elements = elements;
        return this;
    }
}
