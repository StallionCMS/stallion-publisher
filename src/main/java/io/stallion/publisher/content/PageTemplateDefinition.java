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


public class PageTemplateDefinition {
    private String template = "";
    private String thumb = "";
    private boolean special = false;
    private boolean contentEditable = true;

    public String getTemplate() {
        return template;
    }

    public PageTemplateDefinition setTemplate(String template) {
        this.template = template;
        return this;
    }

    public String getThumb() {
        return thumb;
    }

    public PageTemplateDefinition setThumb(String thumb) {
        this.thumb = thumb;
        return this;
    }

    public boolean isSpecial() {
        return special;
    }

    public PageTemplateDefinition setSpecial(boolean special) {
        this.special = special;
        return this;
    }

    public boolean isContentEditable() {
        return contentEditable;
    }

    public PageTemplateDefinition setContentEditable(boolean contentEditable) {
        this.contentEditable = contentEditable;
        return this;
    }
}
