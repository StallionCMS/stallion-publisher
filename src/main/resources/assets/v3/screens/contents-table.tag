<style>
 
</style>



<template>
    <div>
    <h3 if={!isPages}>Posts</h3>
    <h3 if={isPages}>Pages</h3>    
    <h3 if={loading}>Loading &hellip;</h3>
    <h3 if={!loading && !items.length}>No posts yet</h3>
    <table if={!loading && items.length} class="pure-table comments-table table table-striped">
        <thead>
            <tr>
                <th></th>
                <th>Last updated</th>
                <th>Title</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <tr each={post in items}>
                <td class="page-actions">
                    <a class="btn btn-xs btn-default" href="#/edit-content/{post.id}">Edit</a>
                    <a if={!post.currentlyPublished} class="btn-open btn btn-xs btn-default" target="_blank" href="/st-publisher/posts/{ post.id }/preview">Preview</a>
                    <a if={post.currentlyPublished} class="btn-open btn btn-xs btn-default" target="_blank" href="{ post.previewUrl }">View</a>
                </td>
                <td>
                    {moment(post.updatedAt).fromNow()}
                </td>
                <td>
                    {post.title}
                </td>
                <td>
                    <span if={post.currentlyPublished}>Published</span>
                    <span if={!post.currentlyPublished && post.draft}>Draft</span>
                    <span if={!post.currentlyPublished && !post.draft}>Scheduled</span>
                </td>
            </tr>
        </tbody>
        
    </table>
        
    </div>
</template>

<script>
 module.export = {
     data: function() {
         return {
             isPages: false,
             isPosts: false
         }
     },
     route: {
         data: function(transition) {
             stallion.request
         }
     }
 }
</script>
