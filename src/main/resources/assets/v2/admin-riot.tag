/**
* Copyright
*/






//<script>
   (function() {
       var admin = {};

       admin.mainContentTag = null;
       admin.sidebarTag = null;
       admin.$app = $('#riot-app');
       
       admin.onLoad = function() {
           admin.sidebarTag = riot.mount('sidebar-menu');
           admin.configureRouting();
           riot.route.start(true);
       };

       admin.configureRouting = function() {
           makeContentRoute('/posts', function() {
               return mount('view-posts', {});
           });
           makeContentRoute('/new-post', function() {
               return mount('edit-post', {});
           });
           makeContentRoute('/edit-post/*', function(postId) {
               return mount('edit-post', {postId: parseInt(postId, 10)});
           });

           
           makeContentRoute('/pages', function() {
               return mount('view-pages', {});
           });

           makeContentRoute('/files', function() {
               console.log('/files');
               return mount('file-library', {});
           });

           makeContentRoute('/file-upload', function() {
               console.log('/files');
               return mount('file-uploader', {});
           });
           
           
       };

       var mount = function(tag, opts) {
           if (!admin.$app.find(tag).length) {
               admin.$app.append('<' + tag + '></' + tag + '>');
           }
           console.log('Mounting tag ', tag, ' with options ', opts);
           return riot.mount(tag, opts)[0];
       };

       var makeContentRoute = function(route, mountingFunc) {
           riot.route(route, function() {
               console.log('Routing: ', route, arguments);
               if (admin.mainContentTag) {
                   console.log('unmount ', admin.mainContentTag);
                   admin.mainContentTag.unmount(true);
               }
               admin.mainContentTag = mountingFunc.apply(admin, arguments);
           });
       }
       

       window.stPublisherAdmin = admin;

       $(document).ready(admin.onLoad);

    })();
//</script>

<loading-div>
    <div style="margin-top: 1em; margin-bottom: 1em; font-size: 18px;">Loading &hellip;</div>
</loading-div>

//<script>
   function generateUUID(){
       var d = new Date().getTime();
       if(window.performance && typeof window.performance.now === "function"){
           d += performance.now(); //use high-precision timer if available
       }
       var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
           var r = (d + Math.random()*16)%16 | 0;
           d = Math.floor(d/16);
           return (c=='x' ? r : (r&0x3|0x8)).toString(16);
       });
       return uuid;
   }
   
   var showRiotModal = function(opts) {
       opts = opts || {};
       var title = opts.title;
       var outerClass = opts.outerClass;
       var innerClass = opts.innerClass;
       var sizeClass = opts.sizeClass || 'modal-lg';
       var closable = opts.closable === null ? true : opts.closable;
       var riotTag = opts.riotTag || 'widget-modal';
       var elementId = opts.elementId || 'modal-' + riotTag;
       if (!riotTag) {
           throw new Error("A modal must pass in an elementId as an option.");
       }
       var $ele = $('#' + opts.elementId);
       var riotOpts = $.extend(opts.mountOpts, {}) || {};

       if ($ele.length > 0) {
           riotOpts.parentElement = $ele;
           $ele.unbind('show.bs.modal');
           $ele.unbind('hidden.bs.modal');
           riot.mount('widget-modal.modal-riot-content', riotOpts);
           $ele.on('hidden.bs.modal', function (e) {
               riotTagObject.unmount();
           })
           $ele.modal('show');
           return;
       }
       
       $ele = $('<div class="modal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel"><div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button><h4 class="modal-title" id="myModalLabel">Modal title</h4></div></div></div></div>');
       riotOpts.parentElement = $ele;
       $ele.attr('id', elementId);

       
       if (!title) {
           $ele.find('.modal-header').remove();
       }
       if (outerClass) {
           $ele.addClass(outerClass);
       }
       if (innerClass) {
           $ele.find('.modal-dialog').addClass(innerClass);
       }
       if (sizeClass) {
           $ele.find('.modal-dialog').addClass(sizeClass);
       }
       if (closable === false) {
           $ele.find('button.close').remove();
       }
       $ele.find('.modal-content').append('<' + riotTag + ' class="modal-riot-content"></' + riotTag + '>');
       $(document.body).prepend($ele);
       $ele.modal({show: false});

       $ele.on('show.bs.modal', function (e) {
           riot.mount('widget-modal.modal-riot-content', riotOpts);
       });
       //$ele.on('hidden.bs.modal', function (e) {
       //    riotTagObject.unmount();
       //})

       
       $ele.modal('show');

       //setTimeout(function() {  }, 1000);
   };
   

//</script>

<view-posts>
    <h3>Posts</h3>
    <h3 if={loading}>Loading &hellip;</h3>
    <h3 if={!loading && !items.length}>No posts yet</h3>
    <table if={!loading && items.length} class="pure-table comments-table">
        <thead>
            <tr>
                <th>Last updated</th>
                <th>Title</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <tr each={post in items}>
                <td>
                    <a href="#/edit-post/{post.id}">Edit</a>
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
         stallion.request({
             url: '/st-publisher/posts?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o.pager;
                 self.items = o.pager.items;
                 self.loading = false;
                 self.update();
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
