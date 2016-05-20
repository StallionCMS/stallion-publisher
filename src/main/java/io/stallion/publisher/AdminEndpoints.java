/*
 * Copyright (c) 2015-2016 Patrick Fitzsimmons
 *
 * This file is part of Stallion Publisher.
 *
 * Stallion Publisher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.stallion.publisher;

import io.stallion.Context;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.Markdown;

import javax.ws.rs.*;

import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class AdminEndpoints implements EndpointResource {
    @GET
    @Path("/admin")
    @Produces("text/html")
    public String adminPage() {
        Context.getResponse().getPageFooterLiterals().addDefinedBundle("publisher:admin2.js");
        Context.getResponse().getPageHeadLiterals().addDefinedBundle("publisher:admin2.css");
        return TemplateRenderer.instance().renderTemplate("publisher:admin2.jinja");
    }

    @GET
    @Path("/posts")
    @Produces("application/json")
    public Object getPosts(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", BlogPostController.instance().filterChain().pager(page)));
        return ctx;
    }

    @GET
    @Path("/posts/:postId")
    @Produces("application/json")
    public Object getPost(@PathParam("postId") Long postId) {
        return BlogPostController.instance().forId(postId);
    }


    @POST
    @Path("/posts/:postId/update-draft")
    public Object updateDraft(@PathParam("postId") Long postId, @ObjectParam BlogPost updatedPost) {
        BlogPost post = BlogPostController.instance().forId(postId);
        SafeMerger.with().optional("title", "originalContent").merge(updatedPost, post);
        post.setContent(Markdown.instance().process(post.getOriginalContent()));
        BlogPostController.instance().save(post);
        return post;
    }
}


