





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
           makeContentRoute('/new-post', function() {
               return mount('edit-post', {});
           });
           makeContentRoute('/edit-post/*', function(postId) {
               return mount('edit-post', {postId: parseInt(postId, 10)});
           });

           
           makeContentRoute('/pages', function() {
               return mount('view-pages', {});
           });
           
       };

       var mount = function(tag, opts) {
           if (!admin.$app.find(tag).length) {
               admin.$app.append('<' + tag + '></' + tag + '>');
           }
           console.log('appended tag ', opts);
           return riot.mount(tag, opts)[0];
       };

       var makeContentRoute = function(route, mountingFunc) {
           riot.route(route, function() {
               console.log('route! ', route, arguments);
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

<edit-post>
    <div if={loading}>
        <loading-div></loading-div>
    </div>
    <div if={!loading}>
        <h3 if={!postId}>New Post</h3>
        <h3 if={postId}>Edit Post</h3>
        <div>
            <label>Post title</label>
            <input name="title" type="text" value={post.title} class="form-control">
        </div>
        <div class="row">
            <div class="col-md-6">
                <label>Post body</label>
                <textarea name="originalContent" class="form-control">{post.originalContent}</textarea>
            </div>
            <div class="col-md-6">
                <label>Live preview</label>
                <div class="preview-dirty-overlay">Blog post being edited.<br>Waiting to refresh preview.</div>
                <iframe class="preview-iframe" style="width: 100%; height: 100%; min-height: 600px; " name="previewIframe"></iframe>
            </div>
        </div><!-- end pure-g -->
    </div>
    <script>
     var self = this;
     self.postId = self.opts.postId;
     self.post = {
         title: 'A new blog post, click to edit this title',
         originalContent: 'This is the post body. It supports **Markdown**.'
     };
     self.dirty = false;
     self.lastContent = self.post.originalContent;
     self.lastTitle = self.post.title;

     function insertWidget () {
         var $node = $('<div class="line-widget"><span class="widget-label">Contact form</span> <span class="line-widget-edit btn btn-default btn-xs">Edit widget</span> <span class="line-widget-delete btn btn-xs btn-default">remove &#xd7;</span></div>').addClass('line-widget');
         var widget = self.simplemde.codemirror.addLineWidget(1, $node.get(0), {});
         $node.find('.line-widget-delete').click(function() {
             widget.clear();
         });
     }
     
     if (!self.postId) {
         self.loading = false;
     } else {
         self.loading = true;
     }

     function onEditorChange () {
         if (self.dirty === false && !self.loading) {
             showPreviewDirty();             
         };
         debouncedReload();

     };

     function reloadPreview() {

         var title = self.title.value;
         var originalContent = self.simplemde.value();
         if (title === self.post.title && originalContent === self.post.originalContent) {
             previewNotDirty();
             console.log('nothing changed, no reload');
             return;
         }
         console.log('save draft, reload the preview!');
         stallion.request({
             url: '/st-publisher/posts/' + self.postId + '/update-draft',
             method: 'POST',
             data: {
                 title: self.title.value,
                 originalContent: self.simplemde.value()
             },
             success: function(post) {
                 console.log('reload preview iframe');
                 self.previewIframe.contentWindow.location.reload();
                 previewNotDirty();
             }
         });
     };
     


     this.on('mount', function(){
         console.log('mount');
         self.simplemde = new SimpleMDE({toolbar: makeToolbar(), element: self.originalContent });
         if (!self.postId) {
             this.simplemde.value(self.post.originalContent);
             return;
         }

         $(self.title).change(function() {
             reloadPreview();
         });

         $(self.title).keypress(function() {
             onEditorChange();
         });

         
         self.simplemde.value('Loading...');
         
         stallion.request({
             url: '/st-publisher/posts/' + self.postId,
             success: function (o) {
                 self.post = o;
                 self.loading = false;
                 self.update();
                 console.log('updafbgefa');
                 self.simplemde.value(self.post.originalContent);
                 self.previewIframe.src = self.post.slug + "?stPreview=yes";
                 previewNotDirty();
             }
         });
         self.simplemde.codemirror.on("change", onEditorChange);         
     });


     // Returns a function, that, as long as it continues to be invoked, will not
     // be triggered. The function will be called after it stops being called for
     // N milliseconds. If `immediate` is passed, trigger the function on the
     // leading edge, instead of the trailing.
     function debounce(func, wait, immediate) {
	 var timeout;
	 return function() {
	     var context = this, args = arguments;
	     var later = function() {
		 timeout = null;
		 if (!immediate) func.apply(context, args);
	     };
	     var callNow = immediate && !timeout;
	     clearTimeout(timeout);
	     timeout = setTimeout(later, wait);
	     if (callNow) func.apply(context, args);
	 };
     };

     var showPreviewDirty = function() {
         $(self.previewIframe).addClass("dirty");
         self.dirty = true;
         $('.preview-dirty-overlay').css({'display': 'block'});
     }

     var previewNotDirty = function() {
         $(self.previewIframe).removeClass("dirty");
         self.dirty = false;
         $('.preview-dirty-overlay').hide();
     };


     var debouncedReload = debounce(reloadPreview, 2000, false);


     function makeToolbar() {
         var toolbar = [
             "bold",
	     "italic",
             "strikethrough",
             "heading",
             "code",
             "quote",
             "unordered-list",
             "ordered-list",
             "link",
             "table",
             "fullscreen",
             "undo",
             "redo",
             {
                 name: "insertWidget",
                 action: function(editor) {
                     // Add your own code
                     insertWidget();
                 },
                 className: "fa fa-star",
                 title: "Custom Button",
             },
             "|", // Separator
         ];
         return toolbar;
     };

    </script>
</edit-post>

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
