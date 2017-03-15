<style lang="scss">
 .comments-thread-vue {

 }
</style>

<template>
    <div class="comments-thread-vue">
        <div v-for="comment in comments" class="st-new-comment-outer" v-bind:key="comment.id">
            <div :class="{'st-comment': true, 'st-comment-rejected': !comment.approved, 'st-comment-pending': comment.pending}"  :data-comment-id="comment.id" :id="'st-comment-' + comment.id">
                <a :name="'st-comment-' + comment.id"></a>    
                <div class="comment-body-wrap">
                    <div v-if="!comment.approved" class="st-comment-label st-comment-rejected">This comment is not approved.</div>
                    <div class="st-comment-author-avatar st-letter-avatar" :style="'background-color: ' +  comment.avatarColor">
                        {{ comment.avatarLetter }}
                    </div>
                    <div class="st-comment-main">
                        <div class="st-comment-byline">
                            <a v-if="comment.authorWebSite" :href="comment.authorWebSite" rel="nofollow">{{ comment.authorDisplayName }}</a>
                            <span v-if="!comment.authorWebSite">{{comment.authorDisplayName}}</span>
                            commented at {{dateFormat(comment.createdTicks, ST_COMMENT_CREATED_FORMAT)}}
                        </div>
                        <div class="st-comment-body" v-html="comment.bodyHtml"></div>
                        <div v-if="comment.editable && !comment.adminable"  class="moderation-actions">
                            <button class="edit-button" @click="edit">Edit</button>
                        </div>
                        <div v-if="comment.adminable" class="moderation-actions">
                            <button class="edit-button" @click="edit(comment)">Edit</button>
                            <button class="trash-button" v-if="comment.approved" @click="reject(comment)">Trash</button>
                            <button class="approve-button" v-if="!comment.approved" @click="approve(comment)">Approve</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script> 
module.exports = {
     props: {
         onStartEditComment: Function,
         comments: Array
     },
     methods: {
         edit: function(comment) {
             this.onStartEditComment(comment);
         },
         reject: function(comment) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/comments/' + comment.id + '/delete',
                 method: 'POST',
                 success: function() {
                     comment.approved = false;
                     //$(self.root).find('.st-comment-label').addClass('st-comment-rejected').removeClass('st-comment-pending').removeClass('st-comment-approved');
                 }
             });
             
         },
         approve: function(comment) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/comments/' + comment.id + '/restore-and-approve',
                 method: 'POST',
                 success: function() {
                     comment.approved = true;
                     //$(self.root).find('.st-comment-label').addClass('st-comment-approved').removeClass('st-comment-pending').removeClass('st-comment-rejected');
                     //self.update();
                 }
             });
             
         },
         trash: function(comment) {

         },
         addComment: function(comment) {
             this.comments.push(comment);
         },
         dateFormat: dateFormat,
         onSaveEditComment: function(comment) {
             var self = this;
             var i = 0;
             this.comments.forEach(function(cmt) {
                 i++;
                 if (cmt.id === comment.id) {
                     Vue.set(self.comments, i, comment);
                     return false;
                 }
             });
         }
     }
 };
</script>
