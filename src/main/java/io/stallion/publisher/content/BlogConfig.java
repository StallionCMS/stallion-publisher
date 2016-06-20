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

import io.stallion.dataAccess.ModelBase;
import io.stallion.dataAccess.UniqueKey;

import javax.persistence.Column;
import javax.persistence.Table;


@Table(name="blog_configs")
public class BlogConfig extends ModelBase {
    private String internalName;
    private String title;
    private String metaDescription;
    private String listingTemplate;
    private String postTemplate;
    private String slug;
    private boolean editable = false;
    private Integer postsPerPage = 10;

    @Column
    public String getInternalName() {
        return internalName;
    }

    public BlogConfig setInternalName(String internalName) {
        this.internalName = internalName;
        return this;
    }

    @Column
    public String getTitle() {
        return title;
    }

    public BlogConfig setTitle(String title) {
        this.title = title;
        return this;
    }

    @Column(columnDefinition = "longtext")
    public String getMetaDescription() {
        return metaDescription;
    }

    public BlogConfig setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
        return this;
    }

    @Column
    public String getListingTemplate() {
        return listingTemplate;
    }

    public BlogConfig setListingTemplate(String listingTemplate) {
        this.listingTemplate = listingTemplate;
        return this;
    }

    @Column
    public String getPostTemplate() {
        return postTemplate;
    }

    public BlogConfig setPostTemplate(String postTemplate) {
        this.postTemplate = postTemplate;
        return this;
    }

    @Column
    @UniqueKey
    public String getSlug() {
        return slug;
    }

    public BlogConfig setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    @Column(nullable = false)
    public boolean isEditable() {
        return editable;
    }

    public BlogConfig setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }

    @Column
    public Integer getPostsPerPage() {
        return postsPerPage;
    }

    public BlogConfig setPostsPerPage(Integer postsPerPage) {
        this.postsPerPage = postsPerPage;
        return this;
    }
}
