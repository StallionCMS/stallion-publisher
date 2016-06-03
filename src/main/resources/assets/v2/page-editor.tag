

<edit-post>
    <div show={loading}>
        <loading-div></loading-div>
    </div>
    <div show={!loading}>
        <div class="row">
            <div class="col-md-6 editor-header">
                <h3 style="float:left;" if={postId}>Edit {labelUpper}</h3>
                <button onclick={publishPost} style="float:right" class="btn btn-primary btn-xl" disabled={dirty}>
                    <span if={!post.currentlyPublished}>Publish</span>
                    <span if={post.currentlyPublished}>Update Published {labelUpper}</span>
                </button>
                <div style="float:right; padding-right: 10px; color: #777; padding-top: 6px;">{lastAutosaveAt}</div>                
            </div>
            <div class="col-md-6">
                &nbsp;
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <label>{labelUpper} title</label>
                <input name="title" type="text" value={post.title} placeholder="Untitled Post" class="form-control">
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                <div style="margin-top: 1em; margin-bottom: 1em; text-align: center;">
                    <div style="" class="btn-group editor-options-buttons" data-toggle="buttons">
                        <label class="btn btn-default btn-sm active btn-tab-editor">
                            <input type="radio" name="options" id="option1" autocomplete="off"  onchange={showTab.bind(this, 'editor')} checked> Editor
                        </label>
                        <label class="btn btn-default btn-sm btn-tab-versions">
                            <input type="radio" name="options" id="option1" autocomplete="off"  onchange={showTab.bind(this, 'versions')} > Versions
                        </label>                        
                        <label class="btn btn-default btn-sm btn-tab-options">
                            <input type="radio" name="options" id="option2" autocomplete="off" onchange={showTab.bind(this, 'options')}> Options
                        </label>                    
                    </div>
                    &nbsp; &nbsp; <a href="{post.slug}" target="_blank"">Preview in new tab</a>
                </div>
                <div show={tab==='editor'}>
                    <markdown-editor onchange={onMarkdownChange} name="markdownEditor" foo="bar"></markdown-editor>
                    <div each={element in templateElements}>
                        <label>Edit {element.name} section</label>
                        <markdown-editor onchange={onMarkdownChange} markdown={element.rawContent} ></markdown-editor>
                    </div>
                </div>
                <div show={tab==="versions"}>
                    <div if={tab==='versions'}>
                        <version-history name="versionHistoryTab"></version-history>
                    </div>
                </div>
                <div show={tab==='options'} class="options">
                    <div class="form-group">
                        <label>URL</label>
                        <input type="text" class="form-control" name="slug" onkeypress={onUrlTouched} onclick={onUrlTouched} onchange={onUrlTouched} value={post.slug || calculateSlug()} >
                    </div>
                    <div class="form-group">
                        <label>Meta Description</label>
                        <textarea name="metaDescription" class="form-control" value={post.metaDescription} onkeypress={optionsChange} onchange={optionsChange}></textarea>
                    </div>
                    <div class="form-group">
                        <label>Publish At</label>
                        <input type="date" class="form-control" name="publishDate" value={post.publishDate} onchange={optionsChange}>
                    </div>
                    <div class="form-group">
                        <label>Custom Head HTML</label>
                        <textarea name="headHtml" class="form-control" onkeypress={optionsChange} onchange={optionsChange}></textarea>
                    </div>
                    <div class="form-group">
                        <label>Custom Footer HTML</label>
                        <textarea name="footerHtml" class="form-control"  onkeypress={optionsChange} onchange={optionsChange}></textarea>
                    </div>
                </div>
            </div>
            <div class="col-md-6" id="preview-bootstrap-col" >
                <div class="menu-link-group">
                    <a class={'active': previewOrCollaborate==='preview'} href="javascript:;" onclick={switchPreviewOrCollaborate.bind(this, 'preview')}>Live preview</a> | 
                    <a class={'active': previewOrCollaborate==='collaborate'} href="javascript:;" onclick={switchPreviewOrCollaborate.bind(this, 'collaborate')}>Collaboration</a>
                </div>
                <div id="above-preview-row"></div>
                <div show={previewOrCollaborate==='collaborate'}>
                    <h3>Collaboration</h3>
                    <div>No collaborators</div>
                    <button class="btn btn-default btn-xs">Invite Collaborator</button>
                    <h3>Comments</h3>
                    <div style="margin-bottom: 1em;">
                        No comments.
                    </div>
                    <form onsubmit={self.addComment}>
                        <div class="form-group">
                            <textarea class="form-control"></textarea>
                        </div>
                        <div class="form-group">
                            <button type="submit" class="btn btn-primary">Add a comment</button>
                        </div>
                    </form>
                </div>
                <div show={previewOrCollaborate==='preview'} id="live-preview-column">
                    <div style="margin-bottom: 1em; margin-top: 1em; text-align: center;">
                        <div style="" class="btn-group" data-toggle="buttons">
                            <label class="btn btn-default btn-xs active">
                                <input type="radio" name="options" id="option1" autocomplete="off"  onchange={switchPreviewMode.bind(this, 'mobile')} checked> Mobile
                            </label>
                            <label class="btn btn-default btn-xs">
                                <input type="radio" name="options" id="option2" autocomplete="off" onchange={switchPreviewMode.bind(this, 'tablet')}> Tablet
                            </label>
                            <label class="btn btn-default btn-xs">
                                <input type="radio" name="options" id="option3" autocomplete="off" onchange={switchPreviewMode.bind(this, 'desktop')}> Desktop
                            </label>
                        </div>
                    </div>
                    <div show={dirty} class="preview-dirty-overlay">Blog post being edited.<br>Waiting to refresh preview.</div>
                    <iframe class="preview-iframe mobile" style="width: 100%; height: 100%; min-height: 600px; " name="previewIframe"></iframe>
                </div>
            </div>
        </div><!-- end pure-g -->
    </div>
    <script>
     var self = this;
     self.postId = self.opts.postId;
     self.post = {
         title: 'A new blog post, click to edit this title',
         originalContent: 'This is the post body. It supports **Markdown**.',
         widgets: []
     };
     self.label = 'post';
     self.labelPlural = 'posts';
     self.labelUpper = 'Post'

     self.dirty = false;
     self.lastContent = self.post.originalContent;
     self.lastTitle = self.post.title;
     self.lastWidgetCount = 0;
     self.widgetDatas = [];
     self.previewMode = 'mobile';
     self.tab = 'editor';
     self.versionId = null;
     self.previewOrCollaborate = 'preview'
     self.lastAutosaveAt = '';
     self.loading = true;
     self.optionsDirty = false;


     this.on('mount', function(){
         self.previewTop = 140;
         self.previewWidth = 500;
         self.calculatedFrameHeight = $(window).height() - 150;
         setTimeout(function() {
             self.previewTop = $('#live-preview-column').offset().top;
             self.previewWidth = $('#live-preview-column').width();
             var viewPortHeight = $(window).height();
             var newHeight = $(window).height() - self.previewTop - 30;
             self.calculatedFrameHeight = newHeight;
             $('.preview-iframe').css({'height': newHeight + 'px'});
         }, 100);
         $(window).scroll(function() {
             if ($(window).scrollTop() > self.previewTop) {
                 $('#live-preview-column').css({'position': 'fixed', 'width': self.previewWidth + 'px', 'top': '0px'});
             } else {
                 $('#live-preview-column').css({'position': 'static', 'width': '100%'});
             }
             console.log('scrolling');
         });
         
         $(self.title).change(function() {
             reloadPreview();
         });

         $(self.title).keypress(function() {
             onEditorChange();
         });

         self.loadPost();
     });

     self.onRestoreVersion = function(version) {
         self.tab = 'editor';       
         $('.editor-options-buttons label.btn-tab-editor').click();
         //$('.btn-tab-editor').addClass('active');
         self.loadPost();
     };

     self.loadPost = function() {
         self.update({loading: true});
         var url = '/st-publisher/posts/new-for-editing';
         var method = 'POST';
         if (self.postId) {
             url = '/st-publisher/posts/' + self.postId + "/latest-draft"
             method = 'GET';
         }
         stallion.request({
             url: url,
             method: method,
             success: function (o) {
                 self.post = o.post;
                 self.postId = self.postId;
                 self.versionId = self.post.id;
                 self.loading = false;
                 self.templateElements = o.templateElements;
                 if (self.post.type == 'page') {
                     self.label = 'page';
                     self.labelPlural = 'pages';
                     self.labelUpper = 'Page';
                 }
                 self.update();
                 self.tags.markdownEditor.setData(self.post.originalContent, self.post.widgets);
                 //self.post.originalContent = parseOutWidgetHtmlFromContent(self.post.originalContent);
                 // Clear all existing line widgets, bookmarks
                 //self.simplemde.codemirror.doc.getAllMarks().forEach(function(tm) {
                 //    tm.clear();
                 //});
                 //self.simplemde.value(self.post.originalContent);
                 self.lastWidgetCount = self.post.widgets.length;
                 self.previewIframe.src = '/st-publisher/posts/' + self.postId + '/view-version/' + self.versionId;
                 previewNotDirty();
                 //loadAllLineWidgetsFromPost();
             }
         });

     };





     self.switchPreviewOrCollaborate = function(mode) {
         self.update({previewOrCollaborate: mode});
     };

     self.showTab = function(tab) {
         if (tab === 'versions' && self.tags.versionHistoryTab &&  self.tags.versionHistoryTab.reloadVersions) {
             self.tags.versionHistoryTab.reloadVersions();
         }
         self.update({tab: tab});
     }

     self.addComment = function(evt) {
         evt.stopPropagation();
         evt.preventDefault();

         //

         return false;
     }
     
     
     self.switchPreviewMode = function(mode) {
         self.previewMode = mode;
         console.log('new frame mode is ', mode);
         $frame = $('.preview-iframe');
         $column = $('#preview-bootstrap-col');
         $frame.removeClass('fifty').removeClass('seventy-five').removeClass('tablet').removeClass('mobile').removeClass('desktop');
         var width = $column.width();
         console.log('column width ', width);
         $frame.css({width: $column.width() + 'px', height: self.calculatedFrameHeight + "px"});
         if (mode === 'mobile') {
             $frame.addClass('mobile');
         } else if (mode === 'tablet') {
             $frame.addClass('tablet');             
             if (width < 700) {
                 $frame.css({width: width * 1.33 + 'px', height: self.calculatedFrameHeight * 1.33 + 'px'});
                 $frame.addClass('seventy-five');
             }
         } else if (mode === 'desktop') {
             $frame.addClass('desktop');             
             if (width < 570) {
                 $frame.addClass('fifty');
                 $frame.css({width: width * 2 + 'px', height: self.calculatedFrameHeight * 2 + 'px'});
             } else if (width < 900) {
                 $frame.addClass('seventy-five');
                 $frame.css({width: width * 1.33 + 'px', height: self.calculatedFrameHeight * 1.33 + 'px'});
             }
         }
     };

     self.onMarkdownChange = function() {
         console.log('onMarkdownChange markdown changed!');
         if (self.dirty === false && !self.loading) {
             showPreviewDirty();             
         };
         debouncedReload();
     };

     function onEditorChange () {
         if (self.dirty === false && !self.loading) {
             showPreviewDirty();             
         };
         debouncedReload();

     };

     self.publishPost = function() {
         console.log("unsaved pages, can't publish yet.");
         if (self.dirty) {
             return;
         }
         console.log('publish post ');
         stallion.request({
             url: '/st-publisher/posts/' + self.postId + '/publish/' + self.versionId,
             method: 'POST',
             success: function(post) {
                 stallion.showSuccess("Post " + post.title + " has been published.");
                 window.location.hash = "/posts";
             }
         });
     };

     self.onUrlTouched = function() {
         self.urlTouched = true;
         debouncedReload();
     }

     self.calculateSlug = function() {
         return self.post.title.toLowerCase();
     }

     self.optionsChange = function() {
         self.optionsDirty = true;
         debouncedReload();
         return true;
     }



     function reloadPreview(force) {
         var postData = {};
         postData.title = self.title.value;
         console.log('save draft, reload the preview!');
         var markdownData = self.tags.markdownEditor.getData();
         postData.originalContent = markdownData.markdown;
         postData.widgets = markdownData.widgets;
         if (self.urlTouched) {
             postData.slug = self.slug.value;
         }
         postData.metaDescription = self.metaDescription.value;
         if (self.publishDate.value) {
             postData.publishDate = self.publishDate.value;
         }
         postData.headHtml = self.headHtml.value;
         postData.footerHtml = self.footerHtml.value;
         stallion.request({
             url: '/st-publisher/posts/' + self.postId + '/update-draft',
             method: 'POST',
             data: postData,
             success: function(postVersion) {
                 self.post.widgets = postVersion.widgets;
                 self.update({versionId: postVersion.id, lastAutosaveAt: 'Last auto-saved at ' + moment().format('hh:mm:ss a')});
                 self.previewIframe.contentWindow.location.href = '/st-publisher/posts/' + self.postId + '/view-version/' + self.versionId;
                 self.optionsDirty = false;
                 previewNotDirty();
             }
         });
     };



     var showPreviewDirty = function() {
         $(self.previewIframe).addClass("dirty");
         self.update({dirty: true});
         //$('.preview-dirty-overlay').css({'display': 'block'});
     }

     var previewNotDirty = function() {
         $(self.previewIframe).removeClass("dirty");
         self.update({dirty: false});
         //$('.preview-dirty-overlay').hide();
     };


     var debouncedReload = stPublisher.debounce(reloadPreview, 1000, false);


    </script>
</edit-post>

<version-history>
    <div show={loading}>
        <h3>Loading old versions</h3>
    </div>
    <div show={!loading}>
        <h3>Version history</h3>
        <table class="table table-striped" >
            <thead>
                <th></th>
                <th>Date</th>
                <th>Author</th>
                <th>Word Count</th>
                <th>Diff</th>
            </thead>
            <tbody>
                <tr each={item in pager.items}>
                    <td>
                        <a style="width:100px;margin-bottom:.5em;" class="btn btn-default btn-xs" href="/st-publisher/posts/{item.postId}/view-version/{item.id}" target="_blank">view</a><br>
                        <a style="width:100px;margin-bottom:.5em;" class="btn btn-default btn-xs" onclick={restoreVersion.bind(this, item)}>restore as draft</a><br>
                    </td>
                    <td>{ moment(item.versionDate * 1000).fromNow() }</td>
                    <td>{ item.versionAuthorName }</td>
                    <td>{ item.wordCount }</td>
                    <td>{ item.diff }</td>
                </tr>
            </tbody>
        </table>
    </div>
    <script>
     var self = this;
     self.loading = true;
     self.loadAll = 'false';
     self.pager = null;
     console.log('init version history');

     self.reopen = function() {
         
     };

     self.restoreVersion = function(version) {
         stallion.request({
             method: 'POST',
             url: '/st-publisher/posts/make-version-most-recent',
             data: {
                 postId: version.postId,
                 versionId: version.id
             },
             success: function(version) {
                 self.parent.onRestoreVersion(version);
             }
         });
     };

     self.reloadVersions = function() {
         if (!self.parent.postId) {
             return;
         }
         self.update({loading: true});
         stallion.request({
             url: '/st-publisher/posts/' + self.parent.postId + '/load-versions?loadAll=' + self.loadAll,
             success: function(result) {
                 self.update({pager: result.pager, loading: false});
             }
         });
     }

     self.on('mount', function() {
         console.log('mounted version history');
         self.reloadVersions();
     });
    </script>
</version-history>
