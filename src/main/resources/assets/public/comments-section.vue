<style lang="scss">

</style>

<template>
    <div class="comments-section-vue">
        <comments-thread :comments="comments" :on-start-edit-comment="onStartEditComment" ref="thread"></comments-thread>
        <comment-form ref="form" :thread-id="threadId" :parent-title="parentTitle" :parent-permalink="parentPermalink" :add-comment-callback="onAddComment" :edit-comment-callback="onSaveEditComment"></comment-form>
        
    </div>
</template>

<script>
 module.exports = {
     props: {
     },
     data: function() {
         return {
             comments: stFlatCommentsContext.comments,
             threadId: stFlatCommentsContext.threadId,
             parentTitle: stFlatCommentsContext.parentTitle,
             parentPermalink: stFlatCommentsContext.parentPermalink
         }
     },
     methods: {
         onAddComment: function(comment) {
             console.log('add comment ', comment);
             this.$refs.thread.addComment(comment);
         },
         onSaveEditComment: function(comment) {
             console.log('comment saved ', comment);
             this.$refs.thread.onSaveEditComment(comment);
         },
         onStartEditComment: function(comment) {
             this.$refs.form.startEdit(comment);
         }
     }
 }
</script>
