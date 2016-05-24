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

import io.stallion.dataAccess.ModelBase;

import javax.persistence.Column;
import javax.persistence.Table;


@Table(name="blog_configs")
public class BlogConfig extends ModelBase {
    private String internalName;
    private String title;
    private String metaDescription;
    private String template;
    private String slug;

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

    @Column
    public String getMetaDescription() {
        return metaDescription;
    }

    public BlogConfig setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
        return this;
    }

    @Column
    public String getTemplate() {
        return template;
    }

    public BlogConfig setTemplate(String template) {
        this.template = template;
        return this;
    }

    @Column
    public String getSlug() {
        return slug;
    }

    public BlogConfig setSlug(String slug) {
        this.slug = slug;
        return this;
    }
}
