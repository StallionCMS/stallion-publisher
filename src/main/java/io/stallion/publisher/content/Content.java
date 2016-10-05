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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.stallion.dataAccess.ModelController;
import io.stallion.dataAccess.StandardDisplayableModel;
import io.stallion.dataAccess.db.Converter;
import io.stallion.dataAccess.db.converters.JsonListConverter;
import io.stallion.dataAccess.db.converters.JsonObjectConverter;

import javax.persistence.Column;
import javax.persistence.Table;

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

/**
 * A Content object represents a page or blog post.
 */
@Table(name = "stallion_publisher_contents")
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
    private Boolean scheduled = false;
    private Boolean slugTouched = false;
    private UploadedFile featuredImage = null;


    /**
     * The user id of the author of the blog post. (This is the id of the Stallion User record, not the author profile record)
     * @return
     */
    @Column
    public Long getAuthorId() {
        return authorId;
    }

    public Content setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }


    /**
     * A URL at which a draft or future-dated post can be previewed.
     * @return
     */
    public String getPreviewUrl() {
        return getPermalink() + "?stPreview=" + getPreviewKey();
    }


    /**
     * When the content was last updated, in millisecods since the epoch
     *
     * @return
     */
    @Column
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Content setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    /**
     * Raw HTML that gets put in the head section of the web page
     * @return
     */
    @Column(columnDefinition = "longtext")
    public String getHeadHtml() {
        return headHtml;
    }

    public Content setHeadHtml(String headHtml) {
        this.headHtml = headHtml;
        return this;
    }

    /**
     * Raw HTML that gets dumped in the footer section of the web page
     *
     * @return
     */
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

    @Deprecated
    public boolean isCurrentlyPublished() {
        return getPublished();
    }


    /**
     * The ContentController instance
     * @return
     */
    @Override
    @JsonIgnore
    public ModelController getController() {
        return ContentController.instance();
    }

    /**
     * Widgets associated with the main HTML or markdown content
     *
     * @return
     */
    @Column(columnDefinition = "longtext")
    @Converter(cls= JsonListConverter.class)
    public List<Map> getWidgets() {
        return widgets;
    }

    public Content setWidgets(List<Map> widgets) {
        this.widgets = widgets;
        return this;
    }

    /**
     * Was this post edited and saved after its initial creation
     *
     * @return
     */
    @Column
    public Boolean getInitialized() {
        return initialized;
    }

    public Content setInitialized(Boolean initialized) {
        this.initialized = initialized;
        return this;
    }

    /**
     * Either "post", "page", etc.
     *
     * @return
     */
    @Column
    public String getType() {
        return type;
    }


    public Content setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * The primary key id of the blog this content is associated with, if any
     *
     * @return
     */
    @Column
    public Long getBlogId() {
        return blogId;
    }

    public Content setBlogId(Long blogId) {
        this.blogId = blogId;
        return this;
    }

    /**
     * Get a page element by name
     *
     * @param name
     * @return
     */
    public PageElement getElement(String name) {
        for(PageElement el: getElements()) {
            if (name.equals(el.getName())) {
                return el;
            }
        }
        return null;
    }

    /**
     * Get the values of saved PageElements. PageElements are extra elements, such as extra content areas,
     * image areas, editable text lines, etc, that are defined by the template and also edited in the editor.
     *
     * @return
     */
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

    /**
     * True if this is a scheduled post as opposed to a post that goes live as soon as it is a non-draft.
     * @return
     */
    @Column
    public Boolean getScheduled() {
        return scheduled;
    }

    public Content setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
        return this;
    }

    /**
     * True if the end-user changed the slug, false if the slug was auto-generated from the title
     * @return
     */
    @Column
    public Boolean getSlugTouched() {
        return slugTouched;
    }

    public Content setSlugTouched(Boolean slugTouched) {
        this.slugTouched = slugTouched;
        return this;
    }

    /**
     * The featured image information
     * @return
     */
    @Converter(cls= UploadedFileConverter.class)
    @Column(columnDefinition = "longtext")
    public UploadedFile getFeaturedImage() {
        return featuredImage;
    }

    public Content setFeaturedImage(UploadedFile featuredImage) {
        this.featuredImage = featuredImage;
        return this;
    }
}
