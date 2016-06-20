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

import java.util.Map;

import static io.stallion.utils.Literals.*;


public class SiteSettingsStatic {

    private static final SiteSettingRecord EMPTY = new SiteSettingRecord();

    public static SiteSettingsController controller() {
        return SiteSettingsController.instance();
    }

    public static void putSettings(Map<String, String> settings) {
        for(Map.Entry<String, String> entry: settings.entrySet()) {
            updateString(entry.getKey(), entry.getValue());
        }
    }

    public static Map<String, String> getAllSettings() {
        Map<String, String> settings = map();
        for(SiteSettingRecord record: controller().all()) {
            settings.put(record.getName(), record.getValue());
        }
        return settings;
    }


    public static String getHeadHtml() {
        return getString("headHtml");
    }

    public static void setHeadHtml(String value) {
        updateString("headHtml", value);
    }

    public static String getFooterHtml() {
        return getString("footerHtml");
    }

    public static void setFooterHtml(String value) {
        updateString("footerHtml", value);
    }



    public static String getSiteDescription() {
        return getString("siteDescription");
    }

    protected static void setSiteDescription(String siteTitle) {
        updateString("siteDescription", siteTitle);
    }


    public static String getSiteTitle() {
        return getString("siteTitle");
    }

    protected static void setSiteTitle(String siteTitle) {
        updateString("siteTitle", siteTitle);
    }


    protected static String getString(String settingName) {
        return or(controller().forUniqueKey("name", settingName), EMPTY).getValue();
    }

    protected static void updateString(String settingName, String value) {
        SiteSettingRecord record = controller().forUniqueKey("name", settingName);
        if (record == null) {
            record = new SiteSettingRecord().setName(settingName);
        }
        record.setValue(value);
        controller().save(record);

    }






}
