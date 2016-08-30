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

package io.stallion.publisher.tools;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.stallion.boot.AppContextLoader;
import io.stallion.boot.CommandOptionsBase;
import io.stallion.boot.NewProjectBuilder;
import io.stallion.boot.StallionRunAction;
import io.stallion.services.Log;
import io.stallion.utils.Prompter;


public class NewPublisherSiteAction extends NewProjectBuilder {
    PublisherSettingsBuilder builder;

    @Override
    public String getActionName() {
        return "new-publisher-site";
    }

    @Override
    public String getHelp() {
        return "A new Stallion Publisher Site";
    }

    @Override
    public void loadApp(CommandOptionsBase options) {

    }


    public CommandOptionsBase newCommandOptions() {
        return new CommandOptionsBase();
    }


    @Override
    public boolean requireDatabase() {
        return true;
    }

    @Override
    public void execute(CommandOptionsBase options) throws Exception {
        init(options);
        makePages();
        makeStandardConf();
        makeTemplates();
        makeAssets();
        makePages();
        makeBlogConf();

        makeBlogTemplates();
        if (!builder.getRootUrl().equals("/")) {
            String sidebarLi = "" +
                    "          <li class=\"nav-item\">\n" +
                    "            <a class=\"pure-button\" href=\"" + builder.getRootUrl() + "\">Blog</a>\n" +
                    "          </li>\n" +
                    "          <li class=\"nav-item\">\n" +
                    "            <a class=\"pure-button\" href=\"/contact-us\">Contact Us</a>\n" +
                    "          </li>";
            String footerLi = " <li class=\"pure-menu-item\"><a href=\"" + builder.getRootUrl() + "\" class=\"pure-menu-link\">Blog</a></li>\n" +
                    "<li class=\"pure-menu-item\"><a href=\"/contact-us\" class=\"pure-menu-link\">Contact Us</a></li>\n";
            replaceString("templates/base.jinja", "<!--extra-sidebar-links-->", sidebarLi);
            replaceString("templates/base.jinja", "<!--extra-footer-links-->", footerLi);
        }
        makeBlogPosts();


        boolean shouldMakeUser = new Prompter("Do you want to create an admin user right now? This is needed to use some of the internal tools from the web. You can do this later by running >stallion user-add. (y/n)? ").yesNo();
        if (shouldMakeUser) {
            makeUser();
        }
        System.out.printf("\n\nYour site is now complete! You can test it out by using the 'serve' command.\n\n");
    }

    protected void makeBlogTemplates() throws IOException {
        copyFile("/templates/wizard/blog.listing.jinja", "/templates/blog.listing.jinja");
        copyFile("/templates/wizard/blog.post.jinja", "/templates/blog.post.jinja");
        copyFile("/templates/wizard/contact-page.jinja", "/templates/contact-page.jinja");
    }



    protected void makeBlogConf() {
        builder = new PublisherSettingsBuilder();
        builder.setRootUrl(or(new Prompter("Choose a root URL for your main blog page: (default is '/') ").setAllowEmpty(true).prompt(), "/"));
        builder.setCommentModeration(new Prompter("Enable comment moderation? ").yesNo());
        builder.build(getTargetFolder(), getTemplating());
    }

    protected void makeBlogPosts() throws IOException {
        copyFile("/templates/wizard/contact-us.txt", "/pages/contact-us.txt");
        copyFile("/templates/wizard/first-post.txt", "/posts/first-post.txt");
        copyFile("/templates/wizard/second-post.txt", "/posts/second-post.txt");
        copyFile("/templates/wizard/a-draft-post.txt", "/posts/a-draft-post.txt");
    }
}
