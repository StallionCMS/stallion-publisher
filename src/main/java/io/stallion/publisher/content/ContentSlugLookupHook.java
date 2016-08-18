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

import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;

import io.stallion.dataAccess.Displayable;
import io.stallion.requests.DisplayableBySlugHook;
import io.stallion.requests.IRequest;
import io.stallion.services.Log;


public class ContentSlugLookupHook extends DisplayableBySlugHook {
    @Override
    public Displayable find(IRequest o) {
        Content content = ContentController.instance().forUniqueKey("slug", o.getPath());
        if (content == null) {
            return null;
        }
        if (content.getPublished()) {
            return null;
        }
        return content;
    }
}
