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

import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.BodyParam;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.EndpointsRegistry;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.services.Log;
import io.stallion.settings.Settings;
import io.stallion.templating.TemplateRenderer;
import io.stallion.utils.Markdown;

import javax.ws.rs.*;

@Path("/global-modules")
@Produces("application/json")
public class GlobalModuleEndpoints implements EndpointResource {

    @GET
    @Path("/preview")
    @Produces("text/html")
    public Object preview(@QueryParam("versionId") Long versionId) {
        GlobalModuleVersion module = versions().forIdOrNotFound(versionId);
        String template = or(module.getPreviewTemplate(), Settings.instance().getPageTemplate());
        BlogPost page = new BlogPost();
        page.setTitle("Previewing a page with the module: \"" + module.getName() + "\"");
        page.setContent(loremIpsum);
        Map ctx = map(val("page", page), val("item", page), val("post", page));
        ctx.put("__global_module_version__" + module.getName(), module);
        return BlogPostController.instance().render(page, ctx);
    }

    private static final String loremIpsum = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Bonum negas esse divitias, praeposìtum esse dicis? Sed tamen enitar et, si minus multa mihi occurrent, non fugiam ista popularia. <b>Erat enim res aperta.</b> </p>\n" +
            "\n" +
            "<p>Quis istud possit, inquit, negare? Et harum quidem rerum facilis est et expedita distinctio. Quid, quod res alia tota est? Duo Reges: constructio interrete. </p>\n" +
            "\n" +
            "<p><b>Quid ad utilitatem tantae pecuniae?</b> Quae cum magnifice primo dici viderentur, considerata minus probabantur. Videmusne ut pueri ne verberibus quidem a contemplandis rebus perquirendisque deterreantur? Quid, si etiam iucunda memoria est praeteritorum malorum? </p>\n" +
            "\n" +
            "<p><i>Collatio igitur ista te nihil iuvat.</i> Nihil enim iam habes, quod ad corpus referas; Pugnant Stoici cum Peripateticis. Non est ista, inquam, Piso, magna dissensio. Ergo id est convenienter naturae vivere, a natura discedere. Animum autem reliquis rebus ita perfecit, ut corpus; </p>\n" +
            "\n"
            ;

    @GET
    @Path("/list")
    public Object listGlobalModules() {
        return map(val("modules", GlobalModuleController.instance().all()));
    }

    @GET
    @Path("/get-draft/:name")
    public Object getDraft(@PathParam("name") String name) {
        GlobalModule module = modules().forUniqueKeyOrNotFound("name", name);
        GlobalModuleVersion version = versions().filter("name", name).sort("id", "desc").first();
        if (version == null) {
            version = versions().newVersionFromModule(module);
            versions().save(version);
        }
        return version;
    }

    @POST
    @Path("/update-draft/:name")
    public Object updateDraft(@PathParam("name") String name, @ObjectParam GlobalModuleVersion updatedVersion) {
        GlobalModule module = modules().forUniqueKeyOrNotFound("name", name);
        GlobalModuleVersion version = versions().newVersionFromModule(module);
        version = SafeMerger.with().nonNull("rawContent", "widgets").merge(updatedVersion, version);
        version.setContent(Markdown.instance().process(version.getRawContent()));
        versions().save(version);
        return version;
    }

    @POST
    @Path("/publish/:name")
    public Object publish(@PathParam("name") String moduleName, @BodyParam("versionId") Long versionId) {
        GlobalModule module = modules().forUniqueKeyOrNotFound("name", moduleName);
        GlobalModuleVersion version = versions().forId(versionId);
        versions().updateModuleFromVersion(module, version);

        modules().save(module);

        return module;
    }



    private GlobalModuleVersionController versions() {
        return GlobalModuleVersionController.instance();
    }

    private GlobalModuleController modules() {
        return GlobalModuleController.instance();
    }


}
