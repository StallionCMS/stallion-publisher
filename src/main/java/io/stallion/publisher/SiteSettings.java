/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
