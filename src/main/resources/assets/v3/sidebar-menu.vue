
<template>
    <div id="sidebar-menu">
        <ul class="nav nav-sidebar">
            <!--<li class="dashboard"><a href="#/"><span class="icon-map2"></span> Overview <span class="sr-only">(current)</span></a></li>-->
            <li class="newPost posts"><a class="partial-width" href="#/posts"><span class="icon-newspaper"></span> Posts</a> <a class="new-thing-link" v-link="{ path: '/new-post' }">new post</a></li>
            <li class="pages newPage">
                <a class="partial-width" href="#/pages">
                    <span class="icon-file-text"></span> Pages</a>
                <a class="new-thing-link" v-link="{ path: '/new-page' }">new page</a>
            </li>
            <li class="comments"><a href="#/comments"><span class="icon-bubble2"></span> Comments</a></li>
            <li class="contacts"><a href="#/contacts"><span class="icon-users"></span> Contacts</a></li>
            <li class="files"><a href="#/files"><span class="icon-images"></span> Files/Media</a></li>
        </ul>
        <div>
            <a v-if='!configMenuShown' className="show-config-link not-shown" href="javascript:;"  @click='configMenuShown=true'>Configuration &#9656;</a>
            <a v-if='configMenuShown' href="javascript:;" className="show-config-link shown" @click='configMenuShown=false'>Configuration 	&#9662;</a>
            <ul v-if='configMenuShown' className="nav nav-sidebar config-options">
                <li><a href="#/settings/authors">Authors</a></li>
                <li><a href="#/settings/extra-html">Extra HTML and CSS</a></li>
                <li className="widgets"><a href="#/settings/widgets">Global Modules</a></li>
                <li><a href="#/settings/site-information">Site Information</a></li>
            </ul>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             configMenuShown: false
         };
     }
 };
</script>
