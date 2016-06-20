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

import io.stallion.dataAccess.DataAccessRegistry;
import io.stallion.dataAccess.StandardModelController;
import io.stallion.utils.DateUtils;


public class GlobalModuleVersionController extends StandardModelController<GlobalModuleVersion> {
    public static GlobalModuleVersionController instance() {
        return (GlobalModuleVersionController ) DataAccessRegistry.instance().get("global_module_versions");
    }

    public static void register() {
        DataAccessRegistry.instance().registerDbModel(GlobalModuleVersion.class, GlobalModuleVersionController.class, false);
    }
    public GlobalModuleVersion newVersionFromOldVersion(GlobalModuleVersion module) {
        return newVersion(module, module.getGlobalModuleId());
    }

    public GlobalModuleVersion newVersionFromModule(GlobalModule module) {
        return newVersion(module, module.getId());
    }

    protected GlobalModuleVersion newVersion(GlobalModule module, Long moduleId) {
        GlobalModuleVersion version = (GlobalModuleVersion)new GlobalModuleVersion()
                .setName(module.getName())
                .setContent(module.getContent())
                .setRawContent(module.getRawContent())
                .setWidgets(module.getWidgets());
        version.setVersionDate(DateUtils.utcNow());
        version.setGlobalModuleId(moduleId);
        return version;

    }

    public void updateModuleFromVersion(GlobalModule module, GlobalModuleVersion version) {
        module.setContent(version.getContent());
        module.setRawContent(version.getRawContent());
        module.setWidgets(version.getWidgets());
    }
}
