<style lang="scss">

 
 .page-editor-vue {
     .publishing-options-column, .editor-header {
         width: 270px;
         
     }
     .editor-column {
         
     }
     .right-columm {

     }
     .title-wrap {
         margin-top: 1em;
     }
     .title-input {
         font-size: 18px;
         height: 38px;
         background-color: transparent;
         border-color: transparent;
         box-shadow: none;
     }
     .title-input:hover {
         border: 1px dashed #ddd;
     }
     .spacer-bar {
         margin-top: 2em;
         margin-bottom: 1em;
         height: 0px;
         border-top: 1px solid #CCC;
         .preview-new-tab-link, .autosave-label {
             background-color: #f5f5f5;
             display: inline-block;
             margin-top: -12px;

         }
         .preview-new-tab-link {
             float: right;
             padding-left: 10px;
         }
         .autosave-label {
             float: left;
             padding-right: 10px;
         }
         .preview-new-tab-link .material-icons {
             font-size: 16px;
             vertical-align: -18%;

         }
     }
     .accordian-item {
         background-color: #fff;
         border: 1px solid #EEE;
         margin-bottom: 1em;
         .accordian-header, .accordian-body {
             padding: .5em 10px .5em 10px;
         }
         .timezone-input {
             width: 100%;
         }
         .date-input {
             width: 136px;
         }
         
         .accordian-header {
             cursor: pointer;
             border-bottom: 1px solid #EEE;
             color: #666;
             h3 {
                 font-size: 15px;
                 color: #666;
                 margin: 0px;
                 display: inline-block;
                 width: 90%;
             }
         }
         .accordian-header:after {
             content: "▼";
         }
         .accordian-body {

         }
         .accordian-actions {
             padding: 10px;
             background-color: #f9f9f9;
             border-top: 1px solid #DDD;
         }
         .btn-publish {
             width: 100%;
         }
         .btn-revert-to-draft {
             margin-top: .5em;
             width: 100%;
         }
         .accordian-body {
             display: none;
         }
     }
     .accordian.metadata .accordian-item.metadata-item .accordian-body {
         display: block;
     }
     .accordian.publishing .accordian-item.publishing-item .accordian-body {
         display: block;
     }
     .accordian.extra .accordian-item.extra-item .accordian-body {
         display: block;
     }
     .preview-iframe {
         border-width: 0px;
         border-top: 1px solid #ccc;
     }
     .preview-iframe.dirty {
         opacity: .2;
     }

     .preview-iframe.mobile {
         max-width: 500px;
     }
     .menu-link-group {
         margin-top: 0em;
         margin-bottom: 1em;
         text-align: center;
         a {
             display: inline-block;
             width: 50%;
             border-left: 1px solid #CCC;
             border-bottom: 1px solid #CCC;
         }         
         a:first-child {
             border-left: 1px solid transparent;
         }
         a.active {
             border-bottom: 1px solid transparent;
         }

     }
     .menu-link-group a, .menu-link-group a:hover, .menu-link-group a:active {
         text-decoration: none;
         padding-top: 6px;
         padding-bottom: 6px;
     }
     .menu-link-group .active {
         color: #888;
     }
     .right-column-wrap {
         border: 1px solid #CCC;
         padding: 0px;
         background-color: #f5f5f5;
     }
     .collaboration-column {
         padding: 1em 20px 1em 20px;
         h3:first-child {
             margin-top: 0px;
         }
     }
     .versions-column {
         h3 {
             margin-left: 20px;
         }
         table thead {
             border-top: 1px solid #ccc;
             background-color: #F0F0F0;
         }
     }

     .preview-dirty-overlay {
         position: absolute;
         width: 75%;
         padding: 40px 0px 0px 0px;
         text-align: center;
         background-color: transparent;
         height: 200px;
         z-index: 100;
         font-size: 24px;
     }
     
     

 }

 
 @media screen and (max-width: 1180px) {
     .page-editor-vue .col-xs-4.right-column {
         width: 25%;
     }
 }
 
</style>



<template>
    <div class="page-editor page-editor-vue">
        <loading-div v-if="isLoading"></loading-div>
        <div v-if="!isLoading">
            <div class="row">
                <div class="col-xs-3 editor-header">
                    <div class="st-breadcrumbs">
                        <a href="#!/posts">{{labelUpper}}s</a> <span>&#8250;</span> <span>Edit {{labelUpper}}</span>
                    </div>
                </div>
                <div class="col-xs-5">
                    <div class="title-wrap">
                        <input  name="title" type="text" v-model="post.title" placeholder="Untitled Post" class="form-control title-input">
                    </div><!-- end title row -->
                </div>
                <div class="col-xs-4">
                    &nbsp;
                </div>
            </div>
            <div class="row">
                <div class="col-xs-3 publishing-options-column">
                    <div :class="['accordian', openAccordianItem]">
                        <div class="accordian-item publishing-item">
                            <div @click="switchAccordian('publishing')" class="accordian-header"><h3>Publishing</h3></div>
                            <div class="accordian-body">
                                <div v-if="!post.published" class="form-group">
                                    <div class="radio">
                                        <div>
                                            <label><input type="radio" name="scheduled" v-bind:value="false" v-model="post.scheduled"> Publish immediately</label>
                                        </div>
                                        <div>
                                            <label><input type="radio" name="scheduled" v-bind:value="true" v-model="post.scheduled"> Schedule for future</label>
                                        </div>
                                    </div>
                                </div>
                                <div v-if="post.published">
                                    <label>Publication Date</label>
                                </div>
                                <div v-if="post.currentlyPublished || post.scheduled" class="form-group">
                                    <datetime-picker v-model="post.publishDate"></datetime-picker>
                                </div>
                                
                                <div class="form-group">
                                    <label>URL</label>
                                    <!-- <div class="input-group"><div class="input-group-addon">{{siteUrl}}</div></div>-->
                                    <input type="text" class="form-control" v-model="post.slug" @paste="onUrlTouched" @keypress="onUrlTouched">
                                </div>
                                <div class="form-group">
                                    <image-picker-field label="Choose featured image" label-selected="Change featured image" v-model="post.featuredImage"></image-picker-field> 
                                </div>
                            </div>
                            <div class="accordian-actions">
                                <button @click="publishPost" class="btn-publish btn btn-primary btn-xl" :disabled="dirty">
                                    <span v-if="!post.currentlyPublished">Publish</span>
                                    <span v-if="post.currentlyPublished">Update Published {{labelUpper}}</span>
                                </button>
                                <button v-if="post.currentlyPublished" @click="revertToDraft" class="btn btn-default btn-xl btn-revert-to-draft" >
                                    Revert to draft
                                </button>
                            </div>
                        </div><!-- /accordian-item -->
                        <div class="accordian-item metadata-item">
                            <div @click="switchAccordian('metadata')"   class="accordian-header"><h3>Metadata</h3></div>
                            <div class="accordian-body">
                                <div class="form-group">
                                    <label>Meta Description</label>
                                    <autogrow-textarea v-model="post.metaDescription" ></autogrow-textarea>
                                </div>
                                <div class="form-group">
                                    <label>Author</label>
                                    <div class="form-group">
                                        <author-picker  v-model="post.authorId"></author-picker>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="accordian-item extra-item">
                            <div @click="switchAccordian('extra')"   class="accordian-header"><h3>Advanced</h3></div>
                            <div class="accordian-body">
                                <div class="form-group">
                                    <label>Custom Head HTML</label>
                                    <autogrow-textarea v-model="post.headHtml"></autogrow-textarea>
                                </div>
                                <div class="form-group">
                                    <label>Custom Footer HTML</label>
                                    <autogrow-textarea v-model="post.footerHtml"></autogrow-textarea>
                                </div>
                                <div class="form-group">
                                    <button style="width:100%;" class="btn btn-default btn-xl">Change Template</button>
                                    <span v-if="post.template">Current template is {{ post.template }}</span>
                                </div>
                            </div>
                        </div><!-- accordian-item -->
                    </div><!-- /accordian -->
                </div><!-- /accordian column -->
                <div class="col-sm-7 col-md-5 col-xs-12 editor-column">
                    <div v-show="tab==='editor'">
                        <div class="spacer-bar" style="margin-top:0px;">
                            <a class="preview-new-tab-link" :href="'/st-publisher/content/' + post.postId + '/view-latest-version'" target="_blank"">Preview <i class="material-icons">open_in_new</i></a>
                            <div class="autosave-label" style="">{{lastAutosaveAt}}</div>
                        </div>
                        <div v-show="contentEditable">
                            <tinymce-editor :options="{hideInternalPages: true}" v-if="!useMarkdown" ref="maincontent" :html="post.originalContent" :widgets="post.widgets" :change-callback="onMarkdownChange"></tinymce-editor>
                            <markdown-editor v-if="useMarkdown" ref="maincontent" editor-id="main-content-editor" :markdown="post.originalContent" :widgets="post.widgets" :change-callback="onMarkdownChange"></markdown-editor>
                        </div>
                        <div class="editable-page-element-wrapper" v-for="element in post.elements">
                            <label>Edit <em>{{element.name}}</em> section</label>
                            <page-element name="pageElementEditable" :element-name="element.name" :change-callback="onPageElementChange" :element="element" ></page-element>
                        </div>
                    </div>
                    <div v-if="tab==='versions'">
                    </div>
                </div>
                <div v-if="true" class="col-lg-4 col-md-3 col-xs-12 right-columm" id="preview-bootstrap-col" >
                    <div class="right-column-wrap">
                        <div class="menu-link-group"><a v-bind:class="{'active': rightTab==='preview'}" href="javascript:;" @click="rightTab = 'preview'">Live preview</a><a v-show="false" v-bind:class="{'active': rightTab==='collaborate'}" href="javascript:;" @click="rightTab = 'collaborate'">Collaboration</a><a v-bind:class="{'active': rightTab==='versions'}" href="javascript:;" @click="rightTab = 'versions'">Versions</a></div>
                        <div id="above-preview-row"></div>
                        <div v-show="rightTab==='collaborate'" class="collaboration-column">
                            <h3>Collaboration</h3>
                            <!-- <div>No collaborators</div>
                            <button class="btn btn-default btn-xs">Invite Collaborator</button>
                            -->
                            <h3>Comments</h3>
                            <div style="margin-bottom: 1em;">
                                No comments.
                            </div>
                            <form @submit="addComment">
                                <div class="form-group">
                                    <autogrow-textarea class="form-control"></autogrow-textarea>
                                </div>
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary">Add a comment</button>
                                </div>
                            </form>
                        </div>
                        <div v-if="rightTab==='versions'" class="versions-column">
                            <version-history name="versionHistoryTab" :content-id="contentId" :callback="restoreVersionCallback" ></version-history>
                        </div>
                        <div v-show="rightTab==='preview'" id="live-preview-column">
                            <div style="margin-bottom: 1em; margin-top: 1em; text-align: center;">
                                <div style="" class="btn-group" data-toggle="buttons">
                                    <label class="btn btn-default btn-xs active" @click="previewMode='mobile'">
                                        <input type="radio" name="previewModeRadio" v-model="previewMode" value="mobile"> Mobile
                                    </label>
                                    <label class="btn btn-default btn-xs" @click="previewMode='tablet'">
                                        <input type="radio" name="previewModeRadio" v-model="previewMode" value="tablet"> Tablet
                                    </label>
                                    <label class="btn btn-default btn-xs"  @click="previewMode='desktop'">
                                        <input type="radio" name="previewModeRadio" v-model="previewMode" value="desktop"> Desktop
                                    </label>
                                </div>
                            </div>
                            <div v-show="dirty" class="preview-dirty-overlay">Blog post being edited.<br>Waiting to refresh preview.</div>
                            <iframe sandbox="allow-forms allow-scripts" v-bind:class="{'preview-iframe': true, 'dirty': dirty}" style="width: 100%; height: 100%; min-height: 600px; " v-bind:src="'/st-publisher/content/' + contentId +  '/view-latest-version?t=' + now" name="previewIframe"></iframe>
                        </div><!-- -end live preview column -->
                    </div><!-- end right-column-wrap -->
                </div><!-- end col-md-4 -->
            </div><!-- end row -->
        </div><!-- end loaded content -->
    </div><!-- end page-editor -->
</template>

<script>
 module.exports = {
     data: function() {
         return {
             isLoading: false,
             label: 'post',
             labelPlural: 'posts',
             labelUpper: 'Post',
             tab: 'editor',
             lastAutosaveAt: '',
             openAccordianItem: 'publishing',
             contentId: 0,
             versionId: 0,
             originalPost: null,
             postLoaded: false,
             templateElements: [],
             shouldBeScheduled: false,
             previewMode: 'mobile',
             rightTab: 'preview',
             previewZoom: '1',
             contentEditable: false,
             lastWidgetCount: 0,
             now: new Date().getTime(),
             useMarkdown: stPublisherAdminContext.useMarkdown,
             previewOrCollaborate: 'preview',
             post: {},
             dirty: false,
             previewColumnHeight: null,
             previewColumnWidth: null,
             siteUrl: stPublisherAdminContext.siteUrl
         };
     },
     mounted: function() {
         this.onRoute();
     },
     watch: {
         '$route': function() {
             this.onRoute();
         }
     },
     methods: {
         onRoute: function() {
             console.log('page editor on route');
             var self = this;
             var contentId = parseInt(this.$route.params.contentId, 10);
             var data = {};
             var callback = function(o) {
                 self.post = JSON.parse(JSON.stringify(o.post));
                 self.originalPost = o.post;
                 self.versionId = o.post.id;
                 self.contentId = contentId;
                 self.shouldBeScheduled = o.post.scheduled;
                 self.lastWidgetCount = o.post.widgets.length;
                 self.contentEditable = o.contentEditable;
                 if (o.useMarkdown !== null && o.useMarkdown !== undefined) {
                     self.useMarkdown = o.useMarkdown;
                 }
                 
             };
             this.loadContent(contentId, callback);
             
         },
         loadContent: function(contentId, callback) {
             var self = this;
             self.postLoaded = false;
             var url = '/st-publisher/content/' + contentId + "/latest-draft";
             stallion.request({
                 url: url,
                 method: 'GET',
                 success: function (o) {
                     setTimeout(function() { self.postLoaded = true }, 1);
                     callback(o);
                 }
             });
         },
         publishPost: function() {
             var self = this;
             console.log("unsaved pages, can't publish yet.");
             if (self.dirty) {
                 return;
             }
             console.log('publish post ');
             stallion.request({
                 url: '/st-publisher/content/' + self.contentId + '/publish/' + self.versionId,
                 method: 'POST',
                 success: function(post) {
                     stallion.showSuccess("Post " + post.title + " has been published.");
                     window.location.hash = "/posts";
                 }
             });
             
         },
         switchAccordian: function(name) {
             if (this.openAccordianItem === name) {
                 this.openAccordianItem = '';
             } else {
                 this.openAccordianItem = name;
             }
         },
         switchPreviewOrCollaborate: function(mode) {
             this.mode = mode;
         },
         showTab: function(tab) {
             this.tab = tab;
         },
         switchPreviewMode: function(previewMode) {
             this.previewMode = previewMode;
             
         },
         onPageElementChange: function() {
             console.log('page element changed');
             this.dirty = true;
             this.debouncedSaveDraftAndReloadPreview();
         },
         onMarkdownChange: function() {
             console.log('markdown changed');
             this.dirty = true;
             this.debouncedSaveDraftAndReloadPreview();
         },
         onUrlTouched: function() {
             console.log('url touched');
             this.post.slugTouched = true;
             this.optionsChange();
         },
         titleChange: function() {
             if (!this.post.slugTouched) {
                 this.post.slug = this.calculateSlug();
             }
             this.onPageElementChange();
         },
         calculateSlug: function() {
             return "/" + stPublisher.slugify(this.post.title.toLowerCase());
         },
         slugTouched: function() {
             console.log('slug touched');
         },
         optionsChange: function(a, b, c) {
             console.log('options change', a, b, c);
             if (!this.postLoaded) {
                 console.log('post not yet fully loaded, ignoring changes');
                 return;
             }
             this.debouncedSaveDraft();
         },
         saveDraft: function(callback) {
             console.log('save draft');
             var self = this;
             var editorData = self.$refs['maincontent'].getData();
             var post = JSON.parse(JSON.stringify(self.post));
             post.originalContent = editorData.markdown || editorData.html || '';
             post.widgets = editorData.widgets;
             

             var originalJson = JSON.stringify(this.originalPost);
             var currentJson = JSON.stringify(post);
             if (originalJson === currentJson) {
                 console.log('no changes, no need to save');
                 this.dirty = false;
                 return;
             }
             stallion.request({
                 url: '/st-publisher/content/' + self.contentId + '/update-draft',
                 method: 'POST',
                 data: post,
                 success: function(postVersion) {
                     self.versionId = postVersion.id;
                     self.lastAutosaveAt = 'Draft saved at ' + moment().format('hh:mm:ss a');
                     self.optionsDirty = false;
                     if (callback) {
                         callback(postVersion);
                     }
                 }
             });
             
         },
         debouncedSaveDraft: function() {
             if (!this._debouncedSaveDraft) {
                 this._debouncedSaveDraft = stPublisher.debounce(this.saveDraft, 1000, false);
             }
             console.log('debounced save draft');
             this._debouncedSaveDraft();
         },
         saveDraftAndReloadPreview: function() {
             var self = this;
             self.saveDraft(function() {
                 $(self.$el).find('.preview-iframe').attr('src', '/st-publisher/content/' + self.contentId + '/view-latest-version?t=' + new Date().getTime() + '&zoom=' + self.zoom);
                 
                 self.previewNotDirty();
             });
         },
         debouncedSaveDraftAndReloadPreview: function() {
             if (!this._debouncedSaveDraftAndReloadPreview) {
                 this._debouncedSaveDraftAndReloadPreview = stPublisher.debounce(this.saveDraftAndReloadPreview, 1000, false);
             }
             this._debouncedSaveDraftAndReloadPreview();
         },
         previewNotDirty: function() {
             this.dirty = false;
         },
         previewDirty: function() {
             this.dirty = true;
         },
         addComment: function() {

         },
         revertToDraft: function() {
             alert('implement revertToDraft!');
         },
         restoreVersionCallback: function() {
             var self = this;
             self.dirty = true;
             this.loadContent(this.contentId, function(o) {
                 self.tab = 'editor';
                 // TODO: eliminate duplication with initial loading code
                 self.post = JSON.parse(JSON.stringify(o.post));
                 self.$refs.maincontent.setData(self.post.originalContent, self.post.widgets);
                 self.originalPost = o.post;
                 self.versionId = o.post.id;
                 self.templateElements = o.templateElements;
                 self.shouldBeScheduled = o.post.scheduled;
                 self.lastWidgetCount = o.post.widgets.length;
                 self.dirty = false;
                 $(self.$el).find('.preview-iframe').attr('src', '/st-publisher/content/' + self.contentId + '/view-latest-version?t=' + new Date().getTime() + '&zoom=' + self.zoom);
             });
         }
     },
     watch: {
         'post.metaDescription': 'optionsChange',
         'post.headHtml': 'optionsChange',
         'post.footerHtml': 'optionsChange',
         'post.slug': 'optionsChange',
         'post.authorId': 'optionsChange',
         'post.title': 'titleChange',
         'post.publishDate': 'optionsChange',
         dirty: function() {
             
         },
         previewMode: function() {
             var self = this;
             console.log('previewMode Changed ', self.previewMode);
             var $el = $(this.$el);
             var $frame = $el.find('iframe.preview-iframe');
             var $column = $el.find('#preview-bootstrap-col');
             if (self.previewColumnHeight === null) {
                 self.previewColumnHeight = $column.height();
             }
             if (self.previewColumnWidth === null) {
                 self.previewColumnWidth = $column.width();    
             }
             

             var width = self.previewColumnWidth;
             var height = self.previewColumnHeight;
             
             mode = self.previewMode;
             //$frame.removeClass('fifty').removeClass('seventy-five').removeClass('tablet').removeClass('mobile').removeClass('desktop');
             
             //$frame.css({width: $column.width() + 'px', height: self.calculatedFrameHeight + "px"});
             if (mode === 'mobile') {
                 self.zoom = '1';
             } else if (mode === 'tablet') {
                 //$frame.addClass('tablet');             
                 if (width < 700) {
                     //$frame.css({width: width * 1.33 + 'px', height: self.calculatedFrameHeight * 1.33 + 'px'});
                     self.zoom = (width / 700) + '';
                 } else {
                     self.zoom = '1';
                 }
             } else if (mode === 'desktop') {
                 //$frame.addClass('desktop');             

                 if (width < 1080) {
                     self.zoom = (width / 1080) + '';
                 } else {
                     self.zoom = '1';
                 }
                 /*
                 if (width < 570) {
                     $frame.addClass('fifty');
                     $frame.css({width: width * 2 + 'px', height: self.calculatedFrameHeight * 2 + 'px'});
                 } else if (width < 900) {
                     $frame.addClass('seventy-five');
                     $frame.css({width: width * 1.33 + 'px', height: self.calculatedFrameHeight * 1.33 + 'px'});
                 }
                 */
             }
             frameHeight = parseInt(height / parseFloat(self.zoom, 10), 10) + "px";
             frameWidth = parseInt(width / parseFloat(self.zoom, 10), 10) + "px";
             $frame.css({
                 '-webkit-transform': 'scale(' + self.zoom + ')',
                 '-webkit-transform-origin': '0 0',
                 height: frameHeight, 
                 width: frameWidth
             });
             //$(self.$el).find('.preview-iframe').attr('src', '/st-publisher/content/' + self.contentId + '/view-latest-version?t=' + new Date().getTime() + '&zoom=' + self.zoom);
         }
     }
 }
</script>
