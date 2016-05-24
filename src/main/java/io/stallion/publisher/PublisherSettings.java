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

import io.stallion.plugins.BasePluginSettings;
import io.stallion.settings.SettingMeta;


public class PublisherSettings extends BasePluginSettings {
    @SettingMeta(valBoolean = false, help="If true, will display a gravtar image next to users images.")
    private boolean commentsUseGravatar = false;

    public static PublisherSettings getInstance() {
        return getInstance(PublisherSettings.class, "publisher");
    }

    public void assignDefaults() {

    }

    public boolean isCommentsUseGravatar() {
        return commentsUseGravatar;
    }

    public PublisherSettings setCommentsUseGravatar(boolean commentsUseGravatar) {
        this.commentsUseGravatar = commentsUseGravatar;
        return this;
    }
}

