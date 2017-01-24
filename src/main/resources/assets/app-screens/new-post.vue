
<template>
    <div>
        <loading-div v-if="isLoading"></loading-div>
        <div v-if="isLoading">
            <form @submit="blogFormSubmit">
                <h2>Select a blog to post on:</h2>
                <div class="radio" v-for="blog in blogs">
                    <label><input type="radio" name="blogId" v-bind:value="blog.id" v-model="selectedBlogId"> {{blog.internalName}}</label>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Create new post</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             selectedBlogId: 0,
             blogs: [],
             isLoading: true
         }
     },
     created: function() {
         this.onRoute();
     },
     watch: {
         '$route': function() {
             this.onRoute();
         }
     },
     methods: {
         onRoute: function() {
             this.fetchData();
         },
         fetchData: function() {
             var self = this;
             stallion.request({
                 url: '/st-publisher/content/list-blogs',
                 method: 'GET',
                 success: function(o) {
                     self.isLoading = true;
                     if (o.blogs && o.blogs.length === 1) {
                         self.createNewPostAndRedirect(o.blogs[0].id);
                     } else if (!o.blogs) {
                         self.createNewPostAndRedirect(null);
                     } else {
                         var selectedBlogId = localStorage.stLastSelectedBlogId;
                         if (selectedBlogId) {
                             selectedBlogId = parseInt(selectedBlogId, 10);
                         }
                         if (!selectedBlogId) {
                             selectedBlogId = o.blogs[0].id;
                         }
                         self.blogs = o.blogs;
                         self.selectedBlogId = selectedBlogId;
                     }
                 }
             });
         },
         blogFormSubmit: function() {
             this.createNewPostAndRedirect();
         },
         createNewPostAndRedirect: function(blogId) {
             var self = this;
             if (!blogId) {
                 blogId = self.selectedBlogId;
             }
             localStorage.stLastSelectedBlogId = blogId;
             stallion.request({
                 url:'/st-publisher/content/new-for-editing',
                 method: 'POST',
                 data: {
                     type: 'post',
                     blogId: blogId
                 },             
                 success: function(o) {
                     window.location.hash = '/edit-content/' + o.postId;
                 }
             });
         }
     }
 };
</script>
