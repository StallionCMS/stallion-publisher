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
           $("html").on("dragover", function(e) { e.preventDefault();  e.stopPropagation(); });
           $("html").on("drop", function(e) { e.preventDefault(); e.stopPropagation(); });
           $("title").html("Stallion Publisher");
           admin.sidebarTag = riot.mount('sidebar-menu');
           admin.configureRouting();
           riot.route.start(true);
       };

       admin.configureRouting = function() {
           makeContentRoute('/posts', function() {
               return mount('view-posts', {});
           });
           makeContentRoute('/new-post', function() {
               return mount('new-post', {});
           });
           makeContentRoute('/new-page', function() {
               return mount('new-page', {});
           });
           
           makeContentRoute('/edit-content/*', function(postId) {
               return mount('edit-post', {postId: parseInt(postId, 10)});
           });

           
           makeContentRoute('/pages', function() {
               return mount('view-pages', {});
           });

           makeContentRoute('/comments', function() {
               return mount('comments-table', {});
           });


           makeContentRoute('/contacts', function() {
               return mount('contacts-table', {});
           });
           
           makeContentRoute('/files', function() {
               console.log('/files');
               return mount('file-library', {});
           });

           makeContentRoute('/file-upload', function() {
               console.log('/files');
               return mount('file-uploader', {});
           });

           makeContentRoute('/settings/authors', function() {
               return mount('authors-config', {});
           });

           makeContentRoute('/settings/author/*', function(userId) {
               return mount('user-view', {userId: parseInt(userId, 10)});
           });

           makeContentRoute('/settings/new-author', function() {
               return mount('user-view', {userId: null});
           });
           

           makeContentRoute('/settings/blogs', function() {
               return mount('blog-configs', {});
           });

           makeContentRoute('/settings/extra-html', function() {
               return mount('extra-html-config', {});
           });



           makeContentRoute('/settings/widgets', function() {
               return mount('template-widgets-config', {});
           });

           makeContentRoute('/settings/edit-widget/*', function(name) {
               return mount('template-widgets-edit', {name: name});
           });

           
           makeContentRoute('/settings/sitemap', function() {
               return mount('sitemap-config', {});
           });

           makeContentRoute('/settings/site-information', function() {
               return mount('site-information-config', {});
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
       var backdrop = opts.backdrop === undefined ? true : opts.backdrop;
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
           riot.mount(riotTag + '.modal-riot-content', riotOpts);
           $ele.on('hidden.bs.modal', function (e) {
               riotTagObject.unmount();
           })
           $ele.modal('show');
           return;
       }
       
       $ele = $('<div class="modal" role="dialog" aria-labelledby="myLargeModalLabel"><div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button><h4 class="modal-title" id="myModalLabel">Modal title</h4></div></div></div></div>');
       $ele.on("dragover", function(e) { e.preventDefault();  e.stopPropagation(); });
       $ele.on("drop", function(e) { e.preventDefault(); e.stopPropagation(); });
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
       console.log('backdrop ', backdrop);
       $ele.modal({show: false, backdrop: backdrop});

       $ele.on('show.bs.modal', function (e) {
           riot.mount(riotTag + '.modal-riot-content', riotOpts);
       });
       //$ele.on('hidden.bs.modal', function (e) {
       //    riotTagObject.unmount();
       //})

       
       $ele.modal('show');

       //setTimeout(function() {  }, 1000);
   };
   

//</script>

<view-pages>
    <view-posts ispages=true></view-posts>
</view-pages>

<view-posts>
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
    <script>
     var self = this;
     self.loading = true;
     self.items = null;
     self.pager = null;
     self.page = 1;
     self.withDeleted = false;
     self.isPages = self.opts.ispages +'' === 'true';

     smartFormatDate = function(date) {
         var m = moment(date);
         return m.fromNow();
     }
     
     
     this.fetchData = function() {
         var url = '/st-publisher/posts'
         if (self.isPages) {
             url = '/st-publisher/pages'
         }
         url += '?page=' + self.page + '&deleted=' + self.withDeleted;
         
         stallion.request({
             url: url,
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



<sidebar-menu>
    <ul class="nav nav-sidebar">
        <!--<li class="dashboard"><a href="#/"><span class="icon-map2"></span> Overview <span class="sr-only">(current)</span></a></li>-->
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
            <li><a href="#/settings/extra-html">Extra HTML and CSS</a></li>
            <li className="widgets"><a href="#/settings/widgets">Global Modules</a></li>
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

   

<new-post>
    <h2 show={loading}>Loading &hellip;</h2>
    
    <form show={!loading} onsubmit={blogFormSubmit}>
        <h2>Select a blog to post on:</h2>
        <div class="radio" each={blog in blogs}>
            <label><input type="radio" name="blogId" value={blog.id} checked={blog.id === selectedBlogId}> {blog.internalName}</label>
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-primary">Create new post</button>
        </div>
    </form>
    <script>
     var self = this;
     self.mixin('databind');
     self.blogs = [];
     self.loading = true;

     self.blogFormSubmit = function() {
         self.createNewPostAndRedirect();
     }
     
     self.createNewPostAndRedirect = function(blogId) {
         if (!blogId) {
             blogId = self.getFormData().blogId;
         }
         localStorage.stLastSelectedBlogId = blogId;
         stallion.request({
             url:'/st-publisher/posts/new-for-editing',
             method: 'POST',
             data: {
                 type: 'post',
                 blogId: blogId
             },             
             success: function(o) {
                 window.location.hash = '/edit-content/' + o.postId;
             }
         });
     };

     this.on('mount', function(){
         stallion.request({
             url: '/st-publisher/list-blogs',
             method: 'GET',
             success: function(o) {
                 if (o.blogs && o.blogs.length === 1) {
                     self.createNewPostAndRedirect(self.blogs[0].id);
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
                     self.update({loading: false, blogs: o.blogs, selectedBlogId: selectedBlogId});
                 }
             }
         });
     });     

    </script>
</new-post>



<new-page>
    <h2 show={loading}>Loading &hellip;</h2>
    <div show={!loading}>
        <div class="recent-pages">
            <h5>Clone a recent page</h5>
            <div>
                <div class="recent-page-choice" each={page in recentPages} style="overflow: hidden; width: 250px; height: 300px; display: inline-block; margin-right: 20px;">
                    <div>{ page.title }</div>
                    
                    <div style="position: absolute; z-index: 100; height: 250px; width: 300px; opacity: .2; padding-top: 100px; padding-left: 40px; background-color: white;"><button class="btn btn-primary">Clone</button></div>
                    <iframe src="/st-publisher/posts/{page.id}/view-latest-version" class="frame-25" style="width: 1000px; height: 800px;"></iframe>
                </div>
            </div>
        </div>
        <div class="page-templates">
            <h5>Start from a template</h5>
            <div>
                <div onclick={onChooseTemplate.bind(this, template.template)} class="template-choice" each={template in templates}>
                    <img src="{ template.thumbnail }">
                    <br>
                    { template.template }
                </div>
            </div>
        </div>
        <div class="special-templates">
            <h5>Special templates</h5>
            <div>
                <div onclick={onChooseTemplate.bind(this, template.template)} class="template-choice" each={template in specialTemplates}>
                    <img src="{ template.thumbnail }">
                    <br>
                    { template.template }
                </div>
                
            </div>
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     self.blogs = [];
     self.loading = true;
     self.templates = [];
     self.specialTemplates = [];
     self.recentPages = [];
     
     self.onClone = function(cloneId) {
         self.createNewPageAndRedirect(cloneId, '');
     }

     self.onChooseTemplate = function(template) {
         self.createNewPageAndRedirect(0, template);
     }

     
     self.createNewPageAndRedirect = function(cloneId, template) {
         console.log('new page cloneId ', cloneId, 'template ', template);
         stallion.request({
             url:'/st-publisher/posts/new-for-editing',
             method: 'POST',
             data: {
                 type: 'page',
                 cloneId: cloneId,
                 template: template
             },             
             success: function(o) {
                 window.location.hash = '/edit-content/' + o.postId;
             }
         });
     };

     this.on('mount', function(){
         stallion.request({
             url: '/st-publisher/posts/choose-page-template-context',
             method: 'GET',
             success: function(o) {
                 self.update({
                     templates: o.templates,
                     specialTemplates: o.specialTemplates,
                     recentPages: o.recentPages,
                     loading: false
                 });
             }
         });
     });     

    </script>
</new-page>
