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

import io.stallion.Context;
import io.stallion.dataAccess.file.TextItem;
import io.stallion.dataAccess.filtering.FilterChain;
import io.stallion.dataAccess.filtering.FilterOperator;
import io.stallion.dataAccess.filtering.Pager;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.DateUtils;
import io.stallion.utils.GeneralUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.stallion.utils.Literals.*;

public class BlogEndpoints implements EndpointResource {

    private BlogConfig config;

    public BlogEndpoints(BlogConfig config) {
        this.config = config;
    }

    public Map<String, Object> makeContext() throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("blogConfig", config);
        context.put("postsFilter", filterChain());
        if (!empty(config.getTitle())) {
            Context.getResponse().getMeta().setTitle(config.getTitle());
        }
        if (!empty(config.getMetaDescription())) {
            Context.getResponse().getMeta().setDescription(config.getMetaDescription());
        }
        String blogRoot = GeneralUtils.slugify(config.getSlug());
        if (empty(blogRoot) || blogRoot.equals("-")) {
            blogRoot = "root";
        } else if (blogRoot.startsWith("-")) {
            blogRoot = blogRoot.substring(1);
        }
        Context.getResponse().getMeta().setBodyCssId("publisher-" + blogRoot);
        Context.getResponse().getMeta().getCssClasses().add("st-publisher-" + blogRoot);
        return context;
    }

    private ContentController posts() {
        return ContentController.instance();
    }

    private FilterChain<Content> filterChain() {
        return posts().filterChain().filter("blogId", config.getId())
                .filter("draft", false)
                .filterBy("publishDate", DateUtils.utcNow(), FilterOperator.LESS_THAN_OR_EQUAL)
                ;
    }

    @GET
    @Path("/")
    @Produces("text/html")
    public String listHome() throws Exception {
        return listHome(1);
    }

    @GET
    @Path("/page/:page/")
    @Produces("text/html")
    public String listHome(@PathParam("page") Integer page) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .sort("publishDate", "desc")
                .pager(page, or(config.getPostsPerPage(), 10));
        context.put("postsPager", pager);
        if (pager.getItems().size() == 0) {
            Context.getResponse().setStatus(404);
        }
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);
    }

    @GET
    @Path("/feed/")
    @Produces("text/xml")
    public String feed() throws Exception {
        return rss();
    }

    @GET
    @Path("/rss.xml")
    @Produces("text/xml")
    public String rss() throws Exception  {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .sort("publishDate", "desc")
                .pager(0, 20);
        context.put("postsPager", pager);
        context.put("blogUrl", Context.getSettings().getSiteUrl() + config.getSlug());
        ZonedDateTime buildTime = ZonedDateTime.of(2015, 1, 1, 12, 0, 0, 0, GeneralUtils.UTC);
        if (pager.getItems().size() > 0) {
            TextItem item = (TextItem) pager.getItems().get(0);
            buildTime = item.getPublishDate().plusMinutes(1);
        }
        context.put("generator", Settings.instance().getMetaGenerator());
        context.put("lastBuildDate", DateUtils.formatLocalDateFromZonedDate(buildTime, "EEE, dd MMM yyyy HH:mm:ss Z"));
        return TemplateRenderer.instance().renderTemplate(
                getClass().getResource("/templates/rss.jinja").toString(),
                context);
    }



    @GET
    @Path("/archives/:year/:month/")
    public String listByDate(@PathParam("year") String year, @PathParam("month") String month) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .filter("year", year)
                .filter("month", month)
                .sort("publishDate", "desc")
                .pager(0, 5000);
        context.put("postsPager", pager);
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);

    }

    @GET
    @Path("/by-tag/:tag/")
    @Produces("text/html")
    public String listByTag(@PathParam("tag") String tag) throws Exception {
        Map<String, Object> context = makeContext();
        Pager pager = filterChain()
                .filter("tags", tag, "in")
                .sort("publishDate", "desc")
                .pager(0, 5000);
        context.put("postsPager", pager);
        return TemplateRenderer.instance().renderTemplate(config.getListingTemplate(), context);
    }
}
