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

import java.util.List;
import java.util.Map;

import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.services.Log;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


public class CommentsEndpoints implements EndpointResource {


    @GET
    @Path("/comments/list")
    @Produces("application/json")
    public Object getComments(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", CommentController.instance().filterChain().exclude("deleted", true).sort("id", "desc").pager(page)));
        return ctx;
    }


}
