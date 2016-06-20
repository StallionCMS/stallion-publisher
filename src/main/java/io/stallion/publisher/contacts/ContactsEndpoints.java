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

import static io.stallion.utils.Literals.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.stallion.Context;
import io.stallion.exceptions.*;
import io.stallion.reflection.PropertyUtils;
import io.stallion.requests.validators.SafeMerger;
import io.stallion.restfulEndpoints.BodyParam;
import io.stallion.restfulEndpoints.EndpointResource;
import io.stallion.restfulEndpoints.MinRole;
import io.stallion.restfulEndpoints.ObjectParam;
import io.stallion.services.Log;
import io.stallion.templating.TemplateRenderer;
import io.stallion.users.Role;
import io.stallion.utils.DateUtils;
import io.stallion.utils.Sanitize;

import javax.servlet.http.Cookie;
import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;

@Produces("application/json")
public class ContactsEndpoints implements EndpointResource {


    @GET
    @Path("/contacts/list")
    @Produces("application/json")
    public Object getContacts(@QueryParam("page") Integer page) {
        page = or(page, 1);
        Map ctx =  map(val("pager", ContactsController.instance().filterChain().exclude("deleted", true).sort("id", "desc").pager(page)));
        return ctx;
    }


    @GET
    @Path("/contacts/my-subscriptions/:token")
    @Produces("text/html")
    @MinRole(Role.ANON)
    public Object mySubscriptions(@PathParam("token") String token) throws Exception {
        URL url = getClass().getClassLoader().getResource("templates/my-subscriptions.jinja");
        Context.getResponse().getMeta().setTitle("My Subscriptions");
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", token);
        if (emptyInstance(contact)) {
            throw new io.stallion.exceptions.NotFoundException("Subscriptions for the contact not found.");
        }
        Map ctx = map();
        List<Subscription> subscriptions = SubscriptionController.instance().filter("contactId", contact.getId()).all();
        ctx.put("subscriptionsSafeJson", Sanitize.htmlSafeJson(subscriptions));
        ctx.put("contact", Sanitize.htmlSafeJson(contact, "owner"));
        ctx.put("secretToken", Sanitize.htmlSafeJson(contact.getSecretToken()));
        Log.info("Subscriptions contactEmail={0} contactId={1} subs-count={2}", contact.getEmail(), contact.getId(), subscriptions.size());
        return TemplateRenderer.instance().renderTemplate(url.toString(), ctx);
    }

    @POST
    @Path("/contacts/subscribe")
    @Produces("text/html")
    @MinRole(Role.ANON)
    public Object resubscribe(@BodyParam("secretToken") String token, @BodyParam("subscriptionId") Long subscriptionId) throws Exception {
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", token);
        Subscription subscription = SubscriptionController.instance().forId(subscriptionId);
        if (contact == null || subscription == null || !subscription.getContactId().equals(contact.getId())) {
            throw new ClientException("Forbidden from updating this resource", 403);
        }
        return SubscriptionController.instance().resubscribe(contact, subscriptionId);
    }

    @POST
    @Path("/contacts/unsubscribe")
    @Produces("text/html")
    @MinRole(Role.ANON)
    public Object unsubscribe(@BodyParam("secretToken") String token, @BodyParam("subscriptionId") Long subscriptionId) throws Exception {
        URL url = getClass().getClassLoader().getResource("templates/my-subscriptions.jinja");
        Context.getResponse().getMeta().setTitle("My Subscriptions");
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", token);
        Subscription subscription = SubscriptionController.instance().forId(subscriptionId);
        if (contact == null || subscription == null || !subscription.getContactId().equals(contact.getId())) {
            throw new ClientException("Forbidden from updating this resource", 403);
        }
        return SubscriptionController.instance().unsubscribe(contact, subscriptionId);
    }

    @POST
    @Path("/contacts/resend-verify/:secretToken")
    public Object resendVerify(@PathParam("secretToken") String secret) throws IOException {
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", secret);
        if (contact == null) {
            throw new io.stallion.exceptions.NotFoundException("Could not find a contact record");
        }
        SubscriptionController.instance().enqueueVerifyOptIn(contact);
        return true;
    }


    @GET
    @Path("/contacts/verify-subscribe/:secretToken")
    @Produces("text/html")
    public String verifySubscribe(@PathParam("secretToken") String secret) throws IOException {
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", secret);
        if (contact == null) {
            throw new io.stallion.exceptions.NotFoundException("Could not find a contact record");
        }
        contact.setOptInAt(DateUtils.mils());
        contact.setVerifiedOptIn(true);
        contact.setVerifiedEmail(true);
        ContactsController.instance().save(contact);

        URL url = getClass().getClassLoader().getResource("templates/my-subscriptions.jinja");
        Map ctx = map(val("contactId", contact.getId().toString()), val("secretToken", contact.getSecretToken()));
        return TemplateRenderer.instance().renderTemplate(url.toString(), ctx);


    }

    @GET
    @Path("/contacts/decline-subscribe/:secretToken")
    @Produces("text/html")
    public String declineSubscribe(@PathParam("secretToken") String secret) throws IOException {
        Contact contact = ContactsController.instance().forUniqueKey("secretToken", secret);
        if (contact == null) {
            throw new io.stallion.exceptions.NotFoundException("Could not find a contact record");
        }
        contact.setVerifyRejectedAt(DateUtils.mils());
        contact.setVerifiedOptIn(false);
        ContactsController.instance().save(contact);
        URL url = getClass().getClassLoader().getResource("templates/my-subscriptions.jinja");
        Map ctx = map(val("contactId", contact.getId().toString()), val("secretToken", contact.getSecretToken()));
        return TemplateRenderer.instance().renderTemplate(url.toString(), ctx);
    }



    @POST
    @Path("/contacts/submit-form")
    public Boolean submitForm(@ObjectParam FormSubmission rawSubmission) {
        FormSubmission submission = SafeMerger
                .with()
                .nonEmpty("data")
                .optional("formName", "pageUrl", "pageTitle", "formId")
                .optionalEmail("email")
                .merge(rawSubmission);
        Cookie everCookie = Context.request().getCookie("st-evercookie");
        Contact contact = null;
        if (!empty(submission.getEmail())) {
            contact = ContactsController.instance().forUniqueKey("email", submission.getEmail());
        } else if (empty(submission.getEmail()) && everCookie != null && !empty(everCookie.getValue())) {
            contact = ContactsController.instance().forUniqueKey("everCookie", everCookie.getValue());
            submission.setEmail(contact.getEmail());
        }
        if (empty(submission.getEmail())) {
            throw new ClientException("A valid email is required to submit a form to the contacts endpoint!", 422);
        }
        if (contact == null) {
            contact = new Contact()
                    .setEmail(submission.getEmail());
        }
        if (everCookie == null || empty(everCookie.getValue()) && empty(contact.getEverCookie())) {
            contact.setEverCookie(UUID.randomUUID().toString());
        }
        Context.response().addCookie("st-evercookie", contact.getEverCookie(), 365 * 20 * 86400);

        for(Map.Entry<String, Object> entry: submission.getData().entrySet()) {
            if (PropertyUtils.isWriteable(contact, entry.getKey())) {
                if (Contact.SETTABLE_FIELDS.contains(entry.getKey())) {
                    PropertyUtils.setProperty(contact, entry.getKey(), entry.getValue());
                }
            } else {
                contact.getExtra().put(entry.getKey(), entry.getValue());
            }

        }

        ContactsController.instance().save(contact);


        submission
                .setEverCookie(contact.getEverCookie())
                .setContactId(contact.getId())
                .setSubmittedAt(mils());
        FormSubmissionController.instance().save(submission);


        FormSubmissionEmailTaskHandler.enqueue(submission);

        return true;
    }

}
