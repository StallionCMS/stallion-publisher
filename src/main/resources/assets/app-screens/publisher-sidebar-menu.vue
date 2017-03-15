<style lang="scss">
 .sidebar-menu-vue {
     .material-icons {
         font-size: 18px;
         vertical-align: -20%;
     }
 }
</style>
<template>
    <div id="sidebar-menu" class="sidebar-menu-vue">
        <ul class="nav nav-sidebar">
            <!--<li class="dashboard"><a href="#/"><span class="icon-map2"></span> Overview <span class="sr-only">(current)</span></a></li>-->
            <li :class="{'active': active.posts}" class="newPost posts"><a class="partial-width" href="#/posts" title="Posts Dashboard"><i class="material-icons">note</i> <span class="item-label">Posts</span></a> <a class="new-thing-link" href="#/new-post">new post</a></li>
            <li :class="{'active': active.pages}"  class="pages newPage">
                <a class="partial-width" href="#/pages" title="Pages Dashboard">
                    <i class="material-icons">description</i> <span class="item-label">Pages</span></a>
                <a class="new-thing-link" href="#/new-page">new page</a>
            </li>
            <li :class="{'active': active.comments}" class="comments"><a href="#/comments"><i class="material-icons">forum</i> <span class="item-label">Comments</span></a></li>
            <li :class="{'active': active.contacts}" class="contacts"><a href="#/contacts"><span class="icon-users"></span> <span class="item-label">Contacts</span></a></li>
            <li :class="{'active': active.files}" class="files"><a href="#/files"><span class="icon-images"></span> <span class="item-label">Files/Media</span></a></li>
            <li :class="{'active': active.files}" class="settings-nav-item"><a href="#/settings/site-information"><i class="material-icons">settings</i> <span class="item-label">Settings</span></a></li>
        </ul>
        <div class="config-menu-block">
            <a v-if='!configMenuShown' className="show-config-link not-shown" href="javascript:;"  @click='configMenuShown=true'>Configuration &#9656;</a>
            <a v-if='configMenuShown' href="javascript:;" className="show-config-link shown" @click='configMenuShown=false'>Configuration 	&#9662;</a>
            <ul v-if='configMenuShown' className="nav nav-sidebar config-options">
                <li><a href="#/settings/authors">Authors</a></li>
                <li><a href="#/settings/extra-html">Extra HTML and CSS</a></li>
                <li><a href="#/settings/global-modules">Global Modules</a></li>
                <li><a href="#/settings/site-information">Site Information</a></li>
            </ul>
        </div>
        <div class="p show-website-block">
            <br><br>
            <a :href="siteUrl" target="_blank">Open your web site <span class="icon-new-tab"></span></a>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             configMenuShown: false,
             siteUrl: stPublisherAdminContext.siteUrl,
             active: this.resetActive()
         };
     },
     methods: {
         resetActive: function() {
             return {
                 posts: false,
                 pages: false,
                 comments: false,
                 contacts: false,
                 files: false
             }
         },
         updatePath: function(path) {
             console.log('sidebar path', path);
             var active = this.resetActive();

             if (path.indexOf('/posts') > -1) {
                 active.posts = true;
             } else if (path.indexOf('/pages') > -1) {
                 active.pages = true;
             } else if (path.indexOf('/comments') > -1) {
                 active.comments = true;
             } else if (path.indexOf('/contacts') > -1) {
                 active.contacts = true;
             } else if (path.indexOf('/files') > -1) {
                 active.files = true;
             }
             if (path.indexOf('/settings') > -1) {
                 this.configMenuShown = true;
             }
             this.active = active;
         }
     }
 };
</script>
