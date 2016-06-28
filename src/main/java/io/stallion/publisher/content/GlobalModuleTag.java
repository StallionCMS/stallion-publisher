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

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.TemplateSyntaxException;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.TagNode;
import io.stallion.services.Log;
import org.apache.commons.lang3.StringUtils;


public class GlobalModuleTag implements Tag {
    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        String helpers = tagNode.getHelpers().trim();
        int i = helpers.indexOf(" ");
        if (i == -1) {
            i = helpers.length() -1;
        }
        String moduleName = StringUtils.strip(helpers.substring(0, i + 1), "\"\' ");
        if (empty(moduleName)) {
            throw new TemplateSyntaxException(tagNode.getName(), "global_module tag must have a name, for example: {% global_module some_name %}", tagNode.getLineNumber());
        }
        GlobalModule module = GlobalModuleController.instance().forUniqueKey("name", moduleName);
        if (module == null) {
            throw new TemplateSyntaxException(tagNode.getName(), "No global module found with name \"" + moduleName + "\" for tag  {% global_module " + moduleName + " %}", tagNode.getLineNumber());
        }
        Object version = jinjavaInterpreter.getContext().getOrDefault("__global_module_version__" + module.getName(), null);
        if (version != null) {
            module = (GlobalModule) version;
            Log.info("Loaded module version {0}", module.getId());
        }
        return module.getContent();

    }

    public String getEndTagName() {
        return "endmodule";
    }

    public String getName() {
        return "global_module";
    }
}
