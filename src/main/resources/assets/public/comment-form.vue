<style>

</style>

<template>
    <div class="comment-form-vue">
        <a name="st-comments-form"></a>
        <form id="st-comment-form" class="st-comment-form st-form pure-form pure-form-stacked" @submit.prevent="onSubmit" name="theCommentForm">
            <h3 v-if="!editMode" class="st-form-header comments-header write-comment">Write a comment</h3>
            <h3 v-if="editMode" class="st-form-header comments-header write-comment">Edit comment</h3>
            <div class="st-error-wrap"></div>
            <div class="comment-field-wrapper form-group st-field">
                <textarea @focus="onFocus" class="form-control st-control comment-body-ta" v-model="comment.bodyMarkdown" placeholder="Type your comment here. Markdown and basic HTML is supported." required="true"></textarea>
            </div>
            <div class="form-group st-field" v-if="fieldsShown"">
                <label>Your name</label>
                <input class="form-control st-control"  type="text" v-model="comment.authorDisplayName" placeholder="Jamie Doe" required="true">
            </div>
            <div class="form-group st-field" v-if="fieldsShown">
                <label>Your email address (will be kept private)</label>
                <input class="form-control st-control" type="email" v-model="comment.authorEmail" placeholder="me@domain.com" required="true">
            </div>
            <div class="form-group st-field" v-if="fieldsShown">
                <label>Your website (optional)</label>
                <input class="form-control st-control" type="text" v-model="comment.authorWebSite" placeholder="http://myblog.com">
            </div>
            <div v-if="reCaptchaSiteKey && fieldsShown && !editMode">
                <div class="recaptcha-wrapper form-group st-field">
                    <div class="g-recaptcha" name="recaptchaDiv"></div>
                </div>
            </div>
            <div class="" v-if="fieldsShown && !editMode">
                <div class="form-group st-field">
                    <label><input type="checkbox" v-model="comment.mentionSubscribe"> Get a daily email of unread direct replies and mentions?</label>
                </div>
                <div class="form-group st-field">
                    <label><input type="checkbox" v-model="comment.threadSubscribe" > Get a daily email of all unread comments on this thread?</label>
                </div>
            </div>
            <div class="st-actions">
                <button v-if="!editMode" class="btn btn-primary btn-xml pure-button pure-button-primary st-button-submit">Submit Comment</button>
                <button v-if="editMode" class="btn btn-primary btn-xml pure-button pure-button-primary st-button-submit">Update Comment</button>
                <a class="st-cancel-link" style="display:none; margin-left;" href="javascript:stallion_plugin_comments.cancelEditComment('{ commentThreadIdSlug }')">Cancel editing comment</a>
            </div>
            <p>&nbsp;</p>
        </form>
    </div>
</template>

<script>
 module.exports = {
     props: {
         threadId: Number,
         parentTitle: String,
         parentPermalink: String,
         addCommentCallback: Function,
         editCommentCallback: Function
     },
     data: function() {
         var self = this;
         var comment = {
             authorEmail: '',
             authorWebSite: '',
             authorDisplayName: '',
             bodyMarkdown: ''
         };
         

         return {
             fieldsShown: false,
             editMode: false,
             reCaptchaSiteKey: stFlatCommentsContext.reCaptchaKey,
             comment: comment,
             mentionSubcribe: false,
             threadSubscribe: false,
             editingCommentId: 0,
             parentId: null,
             captchaWidgetId: null,
             
         }
     },
     methods: {
         cancelEdit: function() {
             var self = this;
             this.editMode = false;
             this.comment = {
                 authorEmail: '',
                 authorWebSite: '',
                 authorDisplayName: '',
                 bodyMarkdown: ''
             }; 
             this.fieldsShown = false;
             this.editingCommentId = 0;
         },
         getStoreFields: function() {
             return['authorDisplayName', 'authorEmail', 'authorWebSite', 'mentionSubscribe'];
         },
         storeToLocalStorage: function(data) {
             var toStore = {};
             $.each(this.getStoreFields(), function(i, fieldName) {
                 toStore[fieldName] = data[fieldName];
             });
             localStorage.stCommenterInfo = JSON.stringify(toStore);
         },
         startEdit: function(comment) {
             var self = this;
             this.comment = comment;
             this.editMode = true;
             this.fieldsShown = true;
             this.editingCommentId = comment.id;
         },
         onSubmit: function(event, b, c) {
             var self = this;
             var comment = this.comment;
             comment.threadId = self.threadId;
             comment.parentPermalink = self.parentPermalink;
             comment.parentTitle = self.parentTitle;
             var url = '/st-publisher/comments/submit';
             var isEdit = false;
             if (this.editingCommentId) {
                 url = '/st-publisher/comments/' + this.editingCommentId + '/revise';
                 isEdit = true;
             }
             if (self.captchaWidgetId != null) {
                 comment.captchaResponse = grecaptcha.getResponse(self.captchaWidgetId);
             }
             stallion.request({
                 url: url,
                 method: 'post',
                 form: this.$el,
                 success: function(comment) {
                     comment.editable = true;
                     if ($('.no-comments.no-comments').length) {
                         $('.no-comments.no-comments').css('display', "none");
                     }
                     window.location.hash = 'st-comment-' + comment.id;
                     setTimeout(function() {
                         $('#st-comment-' + comment.id).css('backgroundColor', 'transparent');
                     }, 300);
                     if (window.grecaptcha) {
                         window.grecaptcha.reset();
                     }
                     if (!self.editMode) {
                         stFlatCommentsContext.comments.push(comment);
                         stFlatCommentsContext.newComments.push(comment);
                         stFlatCommentsContext.commentById[comment.id] = comment;
                         //window.stallion_comments_context_comments.push(comment);
                         self.storeToLocalStorage(comment);
                         self.addCommentCallback(comment);
                     } else {
                         self.editCommentCallback(comment);
                     }
                     self.editMode = false;
                     self.fieldsShown = false;
                     self.comment = JSON.parse(JSON.stringify(comment));
                     self.comment.bodyMarkdown = '';
                 },
                 data: comment
             });
         },
         onFocus: function() {
             var self = this;
             if (self.fieldsShown) {
                 return;
             }
             self.fieldsShown = true;
             if (!localStorage.stCommenterInfo) {
                 return;
             }
             self.comment = JSON.parse(localStorage.stCommenterInfo);
         },
         renderCaptcha: function() {
             var self = this;
             if (self.reCaptchaSiteKey && st_flat_comments.captchaLoaded && !self.captchaRendered) {
                 self.captchaRendered = true;
                 self.captchaWidgetId = grecaptcha.render(
                     self.recaptchaDiv,
                     {"sitekey": self.reCaptchaSiteKey,
                      "theme": "light"
                     });
             }
         },
         
     },
     ready: function() {
         var self = this;
         self.renderCaptcha();
         stallion.autoGrow({}, $(this.$el).find('textarea'));
         $(this.$el).find('textarea').textcomplete([{
             match: /(^|\s)@(\w*)$/,
             search: function (term, callback) {
                 term = term || "";
                 term = term.toLowerCase();
                 var words = [];
                 stFlatCommentsContext.comments.forEach(function(comment) {
                     words.push(comment.authorDisplayName);
                 });
                 callback($.map(words, function (word) {
                     return word.toLowerCase().indexOf(term) === 0 ? word : null;
                 }));
             },
             replace: function (word) {
                 if (word.indexOf(' ') > -1) {
                     word = '"' + word + '"';
                 }
                 return '$1@' + word + '';
             }
         }]);

     }
 };
</script>
