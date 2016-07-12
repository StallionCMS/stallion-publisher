<style>
 
</style>



<template>
    <div class="page-editor">
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if="!$loadingRouteData">
            <div class="row">
                <div class="col-md-6 editor-header">
                    <h3 style="float:left;">Edit {{labelUpper}}</h3>
                    <button @click="publishPost" style="float:right" class="btn btn-primary btn-xl" disabled={{dirty}}>
                        <span v-if="!post.currentlyPublished">Publish</span>
                        <span v-if="post.currentlyPublished">Update Published {{labelUpper}}</span>
                    </button>
                    <div style="float:right; padding-right: 10px; color: #777; padding-top: 6px;">{{lastAutosaveAt}}</div>                
                </div>
                <div class="col-md-6">
                    &nbsp;
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <label>{{labelUpper}} title</label>
                    <input name="title" type="text" v-model="post.title" placeholder="Untitled Post" class="form-control">
                </div>
            </div><!-- end title row -->
            <div class="row">
                <div class="col-md-6">
                    <div style="margin-top: 1em; margin-bottom: 1em; text-align: center;">
                        <div style="" class="btn-group editor-options-buttons" data-toggle="buttons">
                            <label for="radioTabEditor" class="btn btn-default btn-sm btn-tab-editor" v-bind:class="{active: tab==='editor'}" v-on:click="tab='editor'">
                                <input type="radio" name="options" v-model="tab" value="editor"> Editor
                            </label>
                            <label class="btn btn-default btn-sm btn-tab-versions" v-bind:class="{active: tab==='versions'}"  v-on:click="tab='versions'">
                                <input type="radio" name="options" v-model="tab" value="versions" > Versions
                            </label>                        
                            <label class="btn btn-default btn-sm btn-tab-options" v-bind:class="{active: tab==='options'}"   v-on:click="tab='options'">
                                <input type="radio" name="options" v-model="tab" value="options"> Options
                            </label>
                        </div>
                        &nbsp; &nbsp; <a href="/st-publisher/posts/{{post.postId}}/view-latest-version" target="_blank"">Preview in new tab</a>
                    </div>
                    <div v-show="tab==='editor'">
                        <markdown-editor v-ref:maincontent editor-id="main-content-editor" :markdown="post.originalContent" :widgets="post.widgets" :change-callback="onMarkdownChange"></markdown-editor>
                        <div class="editable-page-element-wrapper" v-for="element in post.elements">
                            <label>Edit <em>{{element.name}}</em> section</label>
                            <page-element name="pageElementEditable" :element-name="element.name" :change-callback="onPageElementChange" :element="element" ></page-element>
                        </div>
                    </div>
                    <div v-if="tab==='versions'">
                        <div if="tab==='versions'">
                            <version-history name="versionHistoryTab" :content-id="contentId" :callback="restoreVersionCallback" ></version-history>
                        </div>
                    </div>
                    <div v-show="tab==='options'" class="options">
                        <div class="form-group">
                            <label>URL</label>
                            <div class="input-group">
                                <div class="input-group-addon">{{siteUrl}}</div>
                                <input type="text" class="form-control" v-model="post.slug" @paste="onUrlTouched" @keypress="onUrlTouched">
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Featured image</label>
                            <image-picker-field :value.sync="post.featuredImage"></image-picker-field> 
                        </div>
                        <div class="form-group">
                            <label>Author</label>
                            <div class="form-group">
                                <author-picker :value.sync="post.authorId"></author-picker>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Meta Description</label>
                            <autogrow-textarea v-model="post.metaDescription" ></autogrow-textarea>
                        </div>
                        <div if="!currentlyPublished" class="form-group">
                            <label>Publishing Options</label>
                            <div class="radio">
                                <div>
                                    <label><input type="radio" name="scheduled" v-bind:value="false" v-model="post.scheduled"> Publish when "Publish" button is clicked.</label>
                                </div>
                                <div>
                                    <label><input type="radio" name="scheduled" v-bind:value="true" v-model="post.scheduled"> Publish on a future scheduled date and time &hellip;</label>
                                </div>
                            </div>
                        </div>
                        <div v-if="currentlyPublished || post.scheduled" class="form-group">
                            <label>Publish Datze</label>
                            <datetime-picker :value.sync="post.publishDate"></datetime-picker>
                        </div>
                        <div class="form-group">
                            <label>Custom Head HTML</label>
                            <autogrow-textarea v-model="post.headHtml"></autogrow-textarea>
                        </div>
                        <div class="form-group">
                            <label>Custom Footer HTML</label>
                            <autogrow-textarea v-model="post.footerHtml"></autogrow-textarea>
                        </div>
                    </div>
                </div>
                <div v-if="true" class="col-md-6" id="preview-bootstrap-col" >
                    <div class="menu-link-group">
                        <a v-bind:class="{'active': previewOrCollaborate==='preview'}" href="javascript:;" @click="previewOrCollaborate = 'preview'">Live preview</a> | 
                        <a v-bind:class="{'active': previewOrCollaborate==='collaborate'}" href="javascript:;" @click="previewOrCollaborate = 'collaborate'">Collaboration</a>
                    </div>
                    <div id="above-preview-row"></div>
                    <div v-show="previewOrCollaborate==='collaborate'">
                        <h3>Collaboration</h3>
                        <div>No collaborators</div>
                        <button class="btn btn-default btn-xs">Invite Collaborator</button>
                        <h3>Comments</h3>
                        <div style="margin-bottom: 1em;">
                            No comments.
                        </div>
                        <form onsubmit="self.addComment">
                            <div class="form-group">
                                <textarea class="form-control"></textarea>
                            </div>
                            <div class="form-group">
                                <button type="submit" class="btn btn-primary">Add a comment</button>
                            </div>
                        </form>
                    </div>
                    <div v-show="previewOrCollaborate==='preview'" id="live-preview-column">
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
                        <iframe sandbox="allow-forms allow-scripts" class="preview-iframe mobile" v-bind:class="{'dirty': dirty}" style="width: 100%; height: 100%; min-height: 600px; " v-bind:src="'/st-publisher/posts/' + contentId +  '/view-latest-version?t=' + now" name="previewIframe"></iframe>
                    </div><!-- -end live preview column -->
                </div><!-- end col-md-6  -->
            </div><!-- end row -->
        </div><!-- end loaded content -->
    </div><!-- end page-editor -->
</template>

<script>
 module.exports = {
     data: function() {
         return {
             label: 'post',
             labelPlural: 'posts',
             labelUpper: 'Post',
             tab: 'editor',
             lastAutosaveAt: '',
             contentId: 0,
             versionId: 0,
             originalPost: null,
             postLoaded: false,
             templateElements: [],
             shouldBeScheduled: false,
             previewMode: 'mobile',
             lastWidgetCount: 0,
             now: new Date().getTime(),
             previewOrCollaborate: 'preview',
             post: {},
             dirty: false,
             siteUrl: 'http://localhost' // stPublisherAdminContext.siteUrl
         };
     },
     route: {
         data: function(transition) {
             var self = this;
             var contentId = parseInt(this.$route.params.contentId, 10);
             var data = {};
             var callback = function(o) {
                 var data = {};
                 data.post = JSON.parse(JSON.stringify(o.post));
                 data.originalPost = o.post;
                 data.versionId = o.post.id;
                 data.contentId = contentId;
                 data.shouldBeScheduled = o.post.scheduled;
                 data.lastWidgetCount = o.post.widgets.length;
                 transition.next(data);
             };
             this.loadContent(contentId, callback);
         }
     },
     methods: {
         loadContent: function(contentId, callback) {
             var self = this;
             self.postLoaded = false;
             var url = '/st-publisher/posts/' + contentId + "/latest-draft";
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
                 url: '/st-publisher/posts/' + self.contentId + '/publish/' + self.versionId,
                 method: 'POST',
                 success: function(post) {
                     stallion.showSuccess("Post " + post.title + " has been published.");
                     window.location.hash = "/posts";
                 }
             });
             
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
             var markdownEditorData = self.$refs['maincontent'].getData();
             var post = JSON.parse(JSON.stringify(self.post));
             post.originalContent = markdownEditorData.markdown;
             post.widgets = markdownEditorData.widgets;

             

             var originalJson = JSON.stringify(this.originalPost);
             var currentJson = JSON.stringify(post);
             if (originalJson === currentJson) {
                 console.log('no changes, no need to save');
                 this.dirty = false;
                 return;
             }
             stallion.request({
                 url: '/st-publisher/posts/' + self.contentId + '/update-draft',
                 method: 'POST',
                 data: post,
                 success: function(postVersion) {
                     self.versionId = postVersion.id;
                     self.lastAutosaveAt = 'Last auto-saved at ' + moment().format('hh:mm:ss a');
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
                 $(self.$el).find('.preview-iframe').attr('src', '/st-publisher/posts/' + self.contentId + '/view-latest-version?t=' + new Date().getTime());
                 
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
                 $(self.$el).find('.preview-iframe').attr('src', '/st-publisher/posts/' + self.contentId + '/view-latest-version?t=' + new Date().getTime());
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
             mode = self.previewMode;
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
         }
     }
 }
</script>
