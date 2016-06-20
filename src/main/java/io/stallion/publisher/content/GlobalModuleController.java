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

import static io.stallion.utils.Literals.*;

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.StandardModelController;
import io.stallion.utils.Markdown;


public class GlobalModuleController extends StandardModelController<GlobalModule> {
    public static GlobalModuleController instance() {
        return (GlobalModuleController) DataAccessRegistry.instance().get("global_modules");
    }
    public static void register() {
        DataAccessRegistry.instance().registerDbModel(GlobalModule.class, GlobalModuleController.class);
        for(GlobalModule def: TemplateConfig.instance().getGlobalModules()) {
            GlobalModule module = instance().forUniqueKey("name", def.getName());
            if (module == null) {
                module = new GlobalModule()
                        .setName(def.getName())
                        .setContent(def.getContent())
                        .setRawContent(def.getRawContent())
                ;
                if (empty(def.getContent()) && !empty(def.getRawContent())) {
                    def.setContent(Markdown.instance().process(def.getRawContent()));
                }
            }

            module.setType(def.getType());
            module.setDescription(def.getDescription());
            module.setPreviewTemplate(def.getPreviewTemplate());
            instance().save(module);
        }
    }
}
