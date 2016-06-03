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

import java.io.File;
import java.util.List;
import java.util.Map;

import com.moandjiezana.toml.Toml;
import io.stallion.services.Log;
import io.stallion.settings.Settings;


public class TemplateConfig {
    private static TemplateConfig _instance = null;

    public static TemplateConfig instance() {
        return _instance;
    }

    public static void load() {
        File file = new File(Settings.instance().getTargetFolder() + "/templates/templates.toml");
        if (!file.exists()) {
            _instance = new TemplateConfig();
        } else {
            _instance = new Toml().read(file).to(TemplateConfig.class);
        }
    }




    private List<GlobalModule> globalModules = list();
    private List<PageTemplateDefinition> pageTemplates = list();


    public List<GlobalModule> getGlobalModules() {
        return globalModules;
    }

    public TemplateConfig setGlobalModules(List<GlobalModule> globalModules) {
        this.globalModules = globalModules;
        return this;
    }

    public List<PageTemplateDefinition> getPageTemplates() {
        return pageTemplates;
    }

    public TemplateConfig setPageTemplates(List<PageTemplateDefinition> pageTemplates) {
        this.pageTemplates = pageTemplates;
        return this;
    }
}
