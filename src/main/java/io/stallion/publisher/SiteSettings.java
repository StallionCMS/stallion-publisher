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

import io.stallion.reflection.PropertyUtils;


public class SiteSettings {
    private static SiteSettings _instance;

    public static SiteSettings instance() {
        if (_instance == null) {
            load();
        }

        // TODO if is the first time being accessed in this context space, read new values from the database
        return _instance;
    }

    public static SiteSettings load() {
        _instance = new SiteSettings();
        // load the settings from the database
        return _instance;
    }

    /** Instance fields and methods **/

    private String siteTitle;

    public SiteSettings refreshFromDatabase() {
        return this;
    }


    public SiteSettings updateSetting(String name, Object value) {
        // Save to the database
        PropertyUtils.setProperty(this, name, value);
        return this;
    }


    public String getSiteTitle() {
        return siteTitle;
    }

    protected SiteSettings setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
        return this;
    }




}
