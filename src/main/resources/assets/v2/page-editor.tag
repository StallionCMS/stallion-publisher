

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
                <input name="title" type="text" value={post.title} onchange={onTitleChanged} placeholder="Untitled Post" class="form-control">
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
                    &nbsp; &nbsp; <a href="/st-publisher/posts/{post.postId}/view-latest-version" target="_blank"">Preview in new tab</a>
                </div>
                <div show={tab==='editor'}>
                    <markdown-editor id="markdownEditor" onchange={onMarkdownChange} name="markdownEditor" foo="bar"></markdown-editor>
                    <div class="editable-page-element-wrapper" each={element in templateElements}>
                        <label>Edit <em>{element.name}</em> section</label>
                        <page-element name="pageElementEditable" elementname="{element.name}" onchange={onMarkdownChange} element={element} ></page-element>
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
                        <div class="input-group">
                            <div class="input-group-addon">{stPublisherAdminContext.siteUrl}</div>
                            <input type="text" class="form-control" name="slug" onchange={onUrlTouched} value={post.slugTouched ? post.slug : calculateSlug()} >
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Featured image</label>
                        <image-editable name="featuredImage" onchange={optionsChange}></image-editable>
                    </div>
                    <div class="form-group">
                        <label>Author</label>
                        <div class="form-group">
                            <author-picker name="authorId"></author-picker>
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Meta Description</label>
                        <autogrow-textarea name="metaDescription" value={post.metaDescription} onkeypress={optionsChange} onchange={optionsChange}></autogrow-textarea>
                    </div>
                    <div if={!currentlyPublished} class="form-group">
                        <label>Publishing Options</label>
                        <div class="radio">
                            <div>
                                <label><input type="radio" name="scheduled" value={false} onchange={setIsScheduled.bind(this, false)}> Publish when "Publish" button is clicked.</label>
                            </div>
                            <div>
                                <label><input type="radio" name="scheduled" value={true} onchange={setIsScheduled.bind(this, true)}> Schedule for a future date &hellips;</label>
                            </div>
                        </div>
                    </div>
                    <div if={shouldBeScheduled} class="form-group">
                        <label>Publish Date</label>
                        <datetime-picker name="publishDate" onchange={optionsChange}></datetime-picker>
                    </div>
                    <div class="form-group">
                        <label>Custom Head HTML</label>
                        <autogrow-textarea name="headHtml" onkeypress={optionsChange} onchange={optionsChange}></textarea>
                    </div>
                    <div class="form-group">
                        <label>Custom Footer HTML</label>
                        <autogrow-textarea name="footerHtml" onkeypress={optionsChange} onchange={optionsChange}></autogrow-textarea>
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
                    <iframe sandbox="allow-forms allow-scripts" class="preview-iframe mobile" style="width: 100%; height: 100%; min-height: 600px; " name="previewIframe"></iframe>
                </div><!-- -end live preview column -->
            </div><!-- end col-md-6  -->
        </div><!-- end row -->
    </div><!-- end loaded content div -->
    <script>
     var self = this;
     self.mixin('bound-form');
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
         });
         
         $(self.title).change(function() {
             reloadPreview();
         });

         $(self.title).keypress(function() {
             onEditorChange();
         });

         self.loadPost();
     });


     self.setIsScheduled = function(scheduled) {
         self.update({shouldBeScheduled: scheduled});
     };


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
                 self.shouldBeScheduled = o.post.scheduled;
                 self.setFormData(o.post);
                 self.update();
                 self.tags.markdownEditor.setData(self.post.originalContent, self.post.widgets);
                 //self.post.originalContent = parseOutWidgetHtmlFromContent(self.post.originalContent);
                 // Clear all existing line widgets, bookmarks
                 //self.simplemde.codemirror.doc.getAllMarks().forEach(function(tm) {
                 //    tm.clear();
                 //});
                 //self.simplemde.value(self.post.originalContent);
                 self.lastWidgetCount = self.post.widgets.length;
                 self.previewIframe.src = '/st-publisher/posts/' + self.postId + '/view-latest-version';
                 //self.previewIframe.contentWindow.location.reload();
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
         $frame = $('.preview-iframe');
         $column = $('#preview-bootstrap-col');
         $frame.removeClass('fifty').removeClass('seventy-five').removeClass('tablet').removeClass('mobile').removeClass('desktop');
         var width = $column.width();
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

     self.onTitleChanged = function() {
         if (!self.urlTouched) {
             self.slug.value =  "/" + stPublisher.slugify(self.title.value.toLowerCase());
         }
         debouncedReload();
     }


     self.calculateSlug = function() {
         return "/" + stPublisher.slugify(self.post.title.toLowerCase());
     }

     self.optionsChange = function() {
         self.optionsDirty = true;
         debouncedReload();
         return true;
     }



     function reloadPreview(force) {
         var postData = {elements:[]};
         var postData = self.getFormData();
         postData.elements = [];

         //postData.title = self.title.value;
         console.log('save draft, reload the preview!');
         var markdownData = self.tags.markdownEditor.getData();
         postData.originalContent = markdownData.markdown;
         postData.widgets = markdownData.widgets;
         var editorTags = self.tags['pageElementEditable'] || [];
         if (editorTags && editorTags.length === undefined) {
             editorTags = [editorTags];
         }
         
         editorTags.forEach(function(editorTag) {
             data = editorTag.getData();
             postData.elements.push({
                 name: editorTag.opts.elementname,
                 content: data.content,
                 data: data.data,
                 type: data.type,
                 rawContent: data.rawContent,
                 widgets: data.widgets
             });
         });

         postData.slugTouched = self.urlTouched;
         if (self.urlTouched) {
             postData.slug = self.slug.value;
         }
         stallion.request({
             url: '/st-publisher/posts/' + self.postId + '/update-draft',
             method: 'POST',
             data: postData,
             success: function(postVersion) {
                 self.post.widgets = postVersion.widgets;
                 self.update({versionId: postVersion.id, lastAutosaveAt: 'Last auto-saved at ' + moment().format('hh:mm:ss a')});
                 //self.previewIframe.contentWindow.location.href = '/st-publisher/posts/' + self.postId + '/view-latest-version';
                 //self.previewIframe.contentWindow.location.reload();
                 self.previewIframe.src = '/st-publisher/posts/' + self.postId + '/view-latest-version?t=' + new Date().getTime();
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
         self.reloadVersions();
     });
    </script>
</version-history>

<image-collection-editable>
    <div>
        <button style="" class="btn btn-default"  onclick={showModal}>Add/Edit/Remove Images</button>
        <span style="width:70%;" if={fourImages}>
            <span each={img in fourImages}>
                
                <img src="{img.src}" style="max-width: 40px; max-height: 40px;">
            </span>
        </span>
    </div>
    <script>
     var self = this;
     self.src = opts.element.data.src;
     self.data = opts.element.data || {};
     self.fourImages = self.data.images ? self.data.images.slice(0, 4) : [];
     console.log('opts.element' , opts.element);

     function chooseImageCallback(widget) {
         console.log('image chosen ', widget);
         self.src = widget.data.src;
         self.data = widget.data;
         self.content = widget.html;
         self.fourImages = self.data.images ? self.data.images.slice(0, 4) : [];
         self.update();
         self.opts.onchange();
     };

     self.getData = function() {
         return {
             content: self.content,
             rawContent: self.content,
             data: self.data
         }
     };

     self.showModal = function() {
         showRiotModal({
             riotTag: 'image-collection-widget-modal',
             mountOpts: {
                 widgetdata: opts.element,
                 callback: chooseImageCallback
             }
         });
     };
    </script>


</image-collection-editable>

<image-editable>
    <div>
        <button class="btn btn-default"  onclick={showModal}>Choose Image</button>
        <span if={src}>
            <img style="max-width: 40px; max-height: 40px;" src="{src}">
        </span>
        <span if={!src}>
            No image selected.
        </span>

    </div>
    <script>
     var self = this;
     self.data = {};
     self.src = '';

     if (opts.element) {
         self.src = opts.element.data.src;
         self.data = opts.element.data;
     }
     console.log('opts.element' , opts.element);
     function chooseImageCallback(widget) {
         console.log('image chosen ', widget);
         self.src = widget.data.src;
         self.data = widget.data;
         self.content = widget.html;
         self.update();
         self.opts.onchange();
     };

     self.getData = function() {
         return {
             content: self.content,
             rawContent: self.content,
             data: self.data
         }
     };

     self.getFormData = function() {
         return self.data.image;
     };

     self.setFormData = function(data) {
         var data = data || {};
         self.update({data: {image: data}, src: data.thumbUrl});
     };


     self.showModal = function() {
         showRiotModal({
             riotTag: 'image-widget-modal',
             mountOpts: {
                 callback: chooseImageCallback
             }
         });
     };
    </script>
</image-editable>

<text-editable>
    <div class="form-group">
        <input class="form-control" type="text" name="contentValue" value={content}>
    </div>
    <script>
     var self = this;
     self.content = this.opts.content;
     console.log('text editable loaded');

     self.getData = function() {
         return {
             content: self.contentValue.value,
             rawContent: self.contentValue.value
         }
     };


    </script>
</text-editable>



<page-element>
    <mount-if condition={element.type==='image'} tag="image-editable" name="image" onchange={handleChange} element={element}></mount-if>
    <mount-if condition={element.type==='image-collection'} tag="image-collection-editable" name="image-collection" onchange={handleChange} element={element}></mount-if>

    <mount-if condition={element.type==='text'} tag="text-editable"" name="text"  data={element.data} content={element.content} onchange={handleChange}></mount-if>
    <mount-if condition={element.type==='markdown'} tag="markdown-editor" name="markdown"  data={element.data} markdown={element.content} widgets={element.widgets} onchange={handleChange}></mount-if>
    <script>
     var self = this;
     self.element = JSON.parse(JSON.stringify(this.opts.element));
     console.log('conditino?' , self.element.type, self.element.type=='image', self.element.type=='image-collection');
     self.handleChange = function() {
         var data = {};
         if (self.element.type === 'markdown') {
             data = self.tags[self.element.type].inner.getData();
             data.rawContent = data.markdown;
             delete data.markdown;
         } else {
             data = self.tags[self.element.type].inner.getData();
         }
         self.element.content = data.content;
         self.element.rawContent = data.rawContent;
         self.element.data = data.data;
         self.element.widgets = data.widgets;
         console.log('handleChange', self.element.name, self.element.type, self.element);
         self.opts.onchange();
     };
     
     console.log('page-element self.element ', self.element.type, self.element);
     /*
     self.on('mount', function() {
         var tag = typeToTag[self.element.type];
         var tagHtml = "<" + tag + "></" + tag + ">";
         $(self.edit_wrapper).append(tagHtml);
         console.log('mount tag ', tag, tagHtml);
         self.innerTag = riot.mount(tag, {
             elementname: self.element.name,
             widgets: self.element.widgets,
             data: self.element.data,
             rawContent: self.element.rawContent,
             markdown: self.element.rawContent,
             content: self.element.content
         })[0];
     });
     */
     self.getData = function() {
         return self.element;
     }

    </script>
</page-element>
