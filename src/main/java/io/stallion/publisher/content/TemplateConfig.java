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

import static io.stallion.utils.Literals.*;

import java.io.File;
import java.util.List;

import com.moandjiezana.toml.Toml;
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



    public PageTemplateDefinition getDefinition(String template) {
        for (PageTemplateDefinition def: getPageTemplates()) {
            if (template.equals(def.getTemplate())) {
                return def;
            }
        }
        return null;
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
