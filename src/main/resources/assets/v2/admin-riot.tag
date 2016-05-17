





//<script>
   (function() {
       var admin = {};

       admin.mainContentTag = null;
       admin.sidebarTag = null;
       admin.$app = $('#riot-app');
       
       console.log('defin admin', $);
       admin.onLoad = function() {
           console.log('onLoad');
           admin.sidebarTag = riot.mount('sidebar-menu');
           console.log(admin.sidebarTag);
           admin.configureRouting();
           riot.route.start(true);
       };

       admin.configureRouting = function() {
           makeContentRoute('/posts', function() {
               return mount('view-posts', {});
           });
           makeContentRoute('/pages', function() {
               return mount('view-pages', {});
           });
           
       };

       var mount = function(tag, opts) {
           if (!admin.$app.find(tag).length) {
               admin.$app.append('<' + tag + '></' + tag + '>');
           }
           console.log('appended tag');
           return riot.mount(tag, opts)[0];
       };

       var makeContentRoute = function(route, mountingFunc) {
           riot.route(route, function() {
               console.log('route! ', route);
               if (admin.mainContentTag) {
                   console.log('unmount ', admin.mainContentTag);
                   admin.mainContentTag.unmount(true);
               }
               admin.mainContentTag = mountingFunc.call(arguments);
           });
       }
       

       window.stPublisherAdmin = admin;

       $(document).ready(admin.onLoad);

    })();
//</script>

<view-posts>
    <h3>Posts</h3>
    <h3 if={loading}>Loading &hellip;</h3>
    <h3 if={!loading && !items.length}>No posts yet</h3>
    <table if={!loading && items.length} class="pure-table comments-table">
        <thead>
            <tr>
                <th>Title</th>
                
            </tr>
        </thead>
        <tbody>
            <tr each={post in items}>
                <td>
                    {moment(post.updatedAt).fromNow()}
                </td>
                <td>
                    {post.title}
                </td>
                <td>
                    <span if={post.published}>Published</span>
                    <span if={!post.published && post.draft}>Draft</span>
                    <span if={!post.published && !post.draft}>Scheduled</span>
                </td>
            </tr>
        </tbody>
        
    </table>
    <script>
     var self = this;
     self.loading = true;
     self.items = null;
     self.pager = null;
     self.page = 1;
     self.withDeleted = false;

     smartFormatDate = function(date) {
         var m = moment(date);
         return m.fromNow();
     }
     
     
     this.fetchData = function() {
         console.log("fetching data");
         stallion.request({
             url: '/st-publisher/posts?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o.pager;
                 self.items = o.pager.items;
                 self.loading = false;
                 self.update();
                 console.log(self.items);
             },
             error: function(o, form, xhr) {
                 console.log('error loading dashboard', o, xhr);
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });     
     
    </script>
</view-posts>


<view-pages>
    <h3>Pages</h3>
    <script>
     var self = this;
    </script>
</view-pages>

<sidebar-menu>
    <ul class="nav nav-sidebar">
        <li class="dashboard"><a href="#/"><span class="icon-map2"></span> Overview <span class="sr-only">(current)</span></a></li>
        <li class="newPost posts"><a class="partial-width" href="#/posts"><span class="icon-newspaper"></span> Posts</a> <a class="new-thing-link" href="#/new-post">new post</a></li>
        <li class="pages newPage">
            <a class="partial-width" href="#/pages">
                <span class="icon-file-text"></span> Pages</a>
            <a class="new-thing-link" href="#/new-page">new page</a>
        </li>
        <li class="comments"><a href="#/comments"><span class="icon-bubble2"></span> Comments</a></li>
        <li class="contacts"><a href="#/contacts"><span class="icon-users"></span> Contacts</a></li>
        <li class="files"><a href="#/files"><span class="icon-images"></span> Files/Media</a></li>
    </ul>
    <div>
        <a onclick={toggleConfigShown} if={!configMenuShown} className="show-config-link not-shown" href="javascript:;"  onClick={this.toggleConfigShown}>Configuration &#9656;</a>
        <a onclick={toggleConfigShown} if={configMenuShown} href="javascript:;" className="show-config-link shown" onClick={this.toggleConfigShown}>Configuration 	&#9662;</a>
        <ul if={configMenuShown} className="nav nav-sidebar config-options">
            <li><a href="#/settings/authors">Authors</a></li>
            <li><a href="#/settings/blogs">Blogs</a></li>
            <li><a href="#/settings/extra-html">Extra HTML and CSS</a></li>
            <li className="widgets"><a href="#/settings/widgets">Template Widgets</a></li>
            <li className="sitemap"><a href="#/settings/sitemap">Sitemap</a></li>
            <li><a href="#/settings/site-information">Site Information</a></li>
        </ul>
    </div>
    <script>
     var self = this;
     self.configMenuShown = false;

     toggleConfigShown = function() {
         self.update({configMenuShown: !self.configMenuShown});
     }
     
    </script>
</sidebar-menu>
