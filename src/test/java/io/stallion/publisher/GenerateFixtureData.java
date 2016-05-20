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

import io.stallion.services.Log;
import io.stallion.tools.SampleDataGenerator;
import io.stallion.users.IUser;
import io.stallion.users.Role;
import io.stallion.users.User;
import io.stallion.users.UserController;
import io.stallion.utils.GeneralUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.stallion.utils.Literals.*;
import static io.stallion.Context.*;


public class GenerateFixtureData extends SampleDataGenerator {

    @Override
    public Long getBaseId() {
        return 1000L;
    }

    @Override
    public void generate() {
        makeAuthors();
        makePosts();
        makePages();
        makeComments();
        makePages();
        makeFormSubmissions();
        makeFiles();
    }


    protected void makeAuthors() {
        IUser[] users = new IUser[]{
                new User()
                        .setEmail("testing1+henry@stallion.io")
                        .setDisplayName("Patrick Henry")
                        .setGivenName("Patrick")
                        .setFamilyName("Henry")
                        .setId(newId(10)),
                new User()
                        .setEmail("testing1+thomas@stallion.io")
                        .setDisplayName("Thomas Paine")
                        .setGivenName("Thomas")
                        .setFamilyName("Paine")
                        .setId(newId(11)),
                new User()
                        .setEmail("testing1+ben@stallion.io")
                        .setDisplayName("Benjamin Franklin")
                        .setGivenName("Benjamin")
                        .setFamilyName("Franklin")
                        .setId(newId(12))
        };
        for (int x = 0; x<users.length; x++) {
            IUser user = users[x];
            user.setApproved(true);
            user.setEmailVerified(true);
            user.setUsername(user.getEmail());
            user.setEncryptionSecret(GeneralUtils.md5Hash(user.getEmail()));
            if (user.getRole() == null) {
                user.setRole(Role.STAFF);
            }
            UserController.instance().save(user);
        }

        AuthorProfile[] profiles = new AuthorProfile[] {
                new AuthorProfile().setBio("The say his tongue is made of silver.").setUserId(getId(10)),
                new AuthorProfile().setBio("The firebrand of the revolution").setUserId(getId(11)),
                new AuthorProfile().setBio("The scientist and elder stateman.").setUserId(getId(12))
        };
        for (int x = 0; x<profiles.length; x++) {
            AuthorProfile profile = profiles[x];
            profile.setId(20L + x);
            AuthorProfileController.instance().save(profile);
        }

    }

    private String[] titleParts = {"Acme", "Build", "Bark", "Cover", "Light", "Shine", "Spear", "Drive", "Flower"};

    private String[] contents = {
            "<p>\"Much this way had it been with the Town-Ho; so when her leak was found gaining once more, there was in truth some small concern manifested by several of her company; especially by Radney the mate. He commanded the upper sails to be well hoisted, sheeted home anew, and every way expanded to the breeze. Now this Radney, I suppose, was as little of a coward, and as little inclined to any sort of nervous apprehensiveness touching his own person as any fearless, unthinking creature on land or on sea that you can conveniently imagine, gentlemen. Therefore when he betrayed this solicitude about the safety of the ship, some of the seamen declared that it was only on account of his being a part owner in her. So when they were working that evening at the pumps, there was on this head no small gamesomeness slily going on among them, as they stood with their feet continually overflowed by the rippling clear water; clear as any mountain spring, gentlemen&mdash;that bubbling from the pumps ran across the deck, and poured itself out in steady spouts at the lee scupper-holes.</p>",
            "<p>\"And I was very happy, and I had beautiful things to eat. And my hands  were soft, because I did no work with them, and my body was clean  all over and dressed in the softest garments&mdash;</p>\n" +
                    "\n" +
                    "<p>\"He surveyed his mangy goat-skin with disgust.</p>",
            "<p>\"Thanks, sir; I was about to ask the favour.\"</p>\n" +
                    "\n" +
                    "<p>\"Very well.  In half an hour we shall go on board.\"</p>\n" +
                    "\n" +
                    "<p>\"But poor Passepartout?\" urged Aouda, who was much disturbed by the servant's disappearance.</p>\n" +
                    "\n" +
                    "<p>\"I shall do all I can to find him,\" replied Phileas Fogg.</p>",
            "<p>\"We are not so unlucky,\" said the new ruler, \"for this Palace and the Emerald City belong to us, and we can do just as we please.  When I remember that a short time ago I was up on a pole in a farmer's cornfield, and that now I am the ruler of this beautiful City, I am quite satisfied with my lot.\"</p>",
            "<p>When he got into the full swing of babbling to himself, it slowly purged  itself into pure English. The sentences grew longer and were enunciated  with a rhythm and ease that was reminiscent of the lecture platform.</p>\n" +
                    "\n" +
                    "<p>\"Tell us about the Red Death, Granser,\" Hare-Lip demanded, when the  teeth affair had been satisfactorily concluded.</p>",

    };

    private String makeTitle(long x) {
        long x2 = new Double(Math.pow(x, 1.7)).longValue();
        long x3 = new Double(Math.pow(x, 1.2)).longValue();
        return titleParts[(int)(x % titleParts.length)] + " " +
                titleParts[(int)(x2 % titleParts.length)]+ " " +
                titleParts[(int)(x3 % titleParts.length)];
    }

    private String makeContent(long x) {
        String content = contents[(int) (x % contents.length)];
        return content;
    }



    protected void makePosts() {
        ZonedDateTime baseTime = ZonedDateTime.of(2015, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime futureTime = ZonedDateTime.of(2099, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));

        for(int x = 100; x<225; x++) {
            String content = makeContent(x);
            String title = makeTitle(x);
            String slug = "/" + GeneralUtils.slugify(title) + "-" + x;
            ZonedDateTime publishDate = baseTime.plusDays(x);
            if (x % 7 == 0) {
                publishDate = futureTime.plusDays(x);
            }
            BlogPost post = new BlogPost()
                    .setUpdatedAt(baseTime.plusDays(x).toInstant().toEpochMilli())
                    .setContent(content)
                    .setOriginalContent(content.replace("<p>", "").replace("</p>", "\n\n"))
                    .setTitle(title)
                    .setSlug(slug)
                    .setDraft(x % 3 == 0)
                    .setPublishDate(publishDate)
                    .setId(newId(x));
            Log.info("update blog post {0} {1}", getId(x), post.getUpdatedAt());
            BlogPostController.instance().save(post);
        }
    }

    protected void makePages() {
        ZonedDateTime baseTime = ZonedDateTime.of(2015, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime futureTime = ZonedDateTime.of(2099, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));

        for(int x = 153; x<170; x++) {
            String content = makeContent(x);
            String title = makeTitle(x);
            String slug = "/" + GeneralUtils.slugify(title) + "-" + x;
            ZonedDateTime publishDate = baseTime.plusDays(x);
            if (x % 7 == 0) {
                publishDate = futureTime.plusDays(x);
            }
            Page page = new Page()
                    .setContent(content)
                    .setOriginalContent(content.replace("<p>", "").replace("</p>", "\n\n"))
                    .setTitle(title)
                    .setSlug(slug)
                    .setDraft(x % 3 == 0)
                    .setPublishDate(publishDate)
                    .setId(newId(x));
            PageController.instance().save(page);
        }
    }

    private String[] commentBodies = {
            "This is really interesting, can you tell me more?",
            "This is the greatest post that I have ever read.",
            "Fine, stuff, my friend",
            "Awesome. Just awesome."
    };

    private String[] commenterNames = {
            "Jamie",
            "Mike",
            "Sam",
            "Ashley",
            "Justin"
    };


    protected void makeComments() {
        ZonedDateTime baseTime = ZonedDateTime.of(2015, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime futureTime = ZonedDateTime.of(2099, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));

        for(int x = 400; x<800; x++) {
            String body = commentBodies[(int)(x % commentBodies.length)];
            String name = commenterNames[(int)(x % commenterNames.length)];
            String email = "testing1+" + name.toLowerCase() + "+" + (x % 20) + "@stallion.io";
            ZonedDateTime publishDate = baseTime.plusDays(x);
            if (x % 7 == 0) {
                publishDate = futureTime.plusDays(x);
            }
            Contact contact = ContactController.instance().forUniqueKey("email", email);
            if (contact == null) {
                Log.info("conact is null {0}", email);
                contact = new Contact()
                        .setEmail(email)
                        .setId(newId(x + 1000));
            }
            contact
                    .setDisplayName(name)
                    .setGivenName(name)
                    .setEverCookie(GeneralUtils.md5Hash(email + x))
                    .setWebSite("https://" + name.toLowerCase() + " stallion.io")
                    .setSecretToken(GeneralUtils.md5Hash(email))
                    ;
            ContactController.instance().save(contact);

            Comment comment = new Comment()
                    .setParentPermalink("http://localhost:8090/some-post")
                    .setAkismetApproved(true)
                    .setAkismetCheckedAt(100000L)
                    .setApproved(true)
                    .setApprovedAt(publishDate.toInstant().toEpochMilli())
                    .setAuthorDisplayName(name)
                    .setThreadId(getId((int)(x % 100 + 100)))
                    .setAuthorEmail(email)
                    .setContactId(getId(x + 1000))
                    .setId(newId(x))
                    ;
            if (x % 13 == 7) {
                comment.setApproved(false).setDeleted(true);
            }
            CommentController.instance().save(comment);
        }
    }


    protected void makeFormSubmissions() {
        ZonedDateTime baseTime = ZonedDateTime.of(2015, 8, 1, 12, 0, 0, 0, ZoneId.of("UTC"));

        for(int x = 800; x<830; x++) {
            String body = commentBodies[(int)(x % commentBodies.length)];
            String name = commenterNames[(int)(x % commenterNames.length)];
            String email = "testing1+" + name.toLowerCase() + "+" + (x % 20) + "@stallion.io";
            ZonedDateTime publishDate = baseTime.plusDays(x);
            Contact contact = ContactController.instance().forUniqueKey("email", email);
            if (contact == null) {
                contact = new Contact()
                        .setEmail(email)
                        .setId(newId(x + 1000));
            }
            contact
                    .setDisplayName(name)
                    .setGivenName(name)
                    .setEverCookie(GeneralUtils.md5Hash(email))
                    .setWebSite("https://" + name.toLowerCase() + " stallion.io")
                    .setSecretToken(GeneralUtils.md5Hash(email))
                    ;
            ContactController.instance().save(contact);

            Map<String, Object> data = new HashMap<String, Object>(map(val("email", email), val("message", body)));
            FormSubmission submission = new FormSubmission()
                    .setContactId(contact.getId())
                    .setEmail(email)
                    .setFormId("contact-us-form")
                    .setFormName("Contact us form")
                    .setPageTitle("Contact us")
                    .setPageUrl("https://stallion.io/contact-us")
                    .setData(data)
                    .setSubmittedAt(publishDate.toInstant().toEpochMilli())
                    .setId(newId(x));

            FormSubmissionController.instance().save(submission);
        }

    }

    protected void makeFiles() {
        ZonedDateTime baseTime = ZonedDateTime.of(2015, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
        UploadedFile[] files = {
                new UploadedFile()
                        .setUrl("https://stallion.io/st-assets/stallion-rearing-red-299.png?ts=1456278540000")
                        .setExtension("png")
                        .setUploadedAt(baseTime.plusDays(7))
                        .setName("Stallion Red Logo")
                        .setType("image")
                        .setId(newId(900)),
                new UploadedFile()
                        .setUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Declaration_independence.jpg/640px-Declaration_independence.jpg")
                        .setExtension("jpg")
                        .setUploadedAt(baseTime.plusDays(18))
                        .setName("Founding Fathers Signing the Declaration")
                        .setType("document")
                        .setId(newId(901)),
                new UploadedFile()
                        .setUrl("http://www.constitution.org/us_doi.pdf")
                        .setExtension("pdf")
                        .setUploadedAt(baseTime.plusDays(29))
                        .setName("The Declaration of Independence Text")
                        .setType("document")
                        .setId(newId(902))
        };
        for (UploadedFile file: files) {
            UploadedFileController.instance().save(file);
        }
    }
}

