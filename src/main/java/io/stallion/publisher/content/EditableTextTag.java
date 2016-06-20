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
import static io.stallion.Context.*;

import java.util.List;
import java.util.Map;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.TemplateSyntaxException;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.Node;
import com.hubspot.jinjava.tree.TagNode;
import io.stallion.dataAccess.Model;
import io.stallion.services.Log;
import io.stallion.utils.Markdown;
import org.apache.commons.lang3.StringUtils;


public class EditableTextTag  implements Tag {
    public static final String ELEMENTS_CONTEXT_VAR = "__found_template_elements";

    public String getType() {
        return "text";
    }

    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        String helpers = tagNode.getHelpers().trim();
        int i = helpers.indexOf(" ");
        if (i == -1) {
            i = helpers.length() -1;
        }
        String name = StringUtils.strip(helpers.substring(0, i + 1), "\"\' ");
        if (empty(name)) {
            throw new TemplateSyntaxException(tagNode.getName(), "editable_section tag must have a name, for example: {% editable_section features %}", tagNode.getLineNumber());
        }





        List<PageElement> templateElements = (List<PageElement>)jinjavaInterpreter.getContext().get(ELEMENTS_CONTEXT_VAR);
        if (templateElements != null) {
            StringBuilder builder = new StringBuilder();
            for(Node node:tagNode.getChildren()) {
                builder.append(node.render(jinjavaInterpreter));
            }
            String defaultValue = builder.toString().trim();
            PageElement element = new PageElement()
                    .setName(name)
                    .setRawContent(defaultValue)
                    .setType(getType());
            if (getType().equals("markdown")) {
                element.setContent(Markdown.instance().process(element.getRawContent()));
            } else {
                element.setContent(element.getRawContent());
            }
            templateElements.add(
                    element
            );
        }

        Model pageModel = (Model)jinjavaInterpreter.getContext().get("page");
        if (pageModel == null || !(pageModel instanceof Content)) {
            return "";
        }
        Content page = (Content)pageModel;
        PageElement element = page.getElement(name);
        if (element == null) {
            return "";
        }
        return element.getContent();
    }


    public String getEndTagName() {
        return "end_editable";
    }


    public String getName() {
        return "editable_text";
    }
}
