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

package io.stallion.publisher.contacts;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.lib.tag.Tag;
import com.hubspot.jinjava.tree.Node;
import com.hubspot.jinjava.tree.TagNode;


public class FormTag implements Tag {

    public String interpret(TagNode tagNode, JinjavaInterpreter jinjavaInterpreter) {
        StringBuilder builder = new StringBuilder();

        builder.append("<form id='stallion-contact-form' class=\"pure-form pure-form-stacked st-contacts-form\">");
        for(Node node:tagNode.getChildren()) {
            builder.append(node.render(jinjavaInterpreter));
        }
        builder.append("</form>");
        return builder.toString();
    }


    public String getEndTagName() {
        return "endform";
    }


    public String getName() {
        return "contact_form";
    }
}
