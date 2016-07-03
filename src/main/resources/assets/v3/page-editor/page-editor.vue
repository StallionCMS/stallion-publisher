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
                        <span v-if="post.currentlyPublished">Update Published {labelUpper}</span>
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
                    <div v-if="tab==='editor'">
                        <markdown-editor editor-id="main-content-editor" :markdown="post.originalContent" :widgets="post.widgets"></markdown-editor>
                        <div class="editable-page-element-wrapper" v-for="element in templateElements">
                            <label>Edit <em>{{element.name}}</em> section</label>
                            <page-element name="pageElementEditable" elementname="{element.name}}" onchange="onMarkdownChange" element="element" ></page-element>
                        </div>
                    </div>
                    <div v-if="tab==='versions'">
                        <div if="tab==='versions'">
                            <version-history name="versionHistoryTab"></version-history>
                        </div>
                    </div>
                    <div v-if="tab==='options'" class="options">
                        <div class="form-group">
                            <label>URL</label>
                            <div class="input-group">
                                <div class="input-group-addon">{{siteUrl}}</div>
                                <input type="text" class="form-control" v-model="post.slug" >
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
                            <label>Publishing Options {{ post.scheduled }}</label>
                            <div class="radio">
                                <div>
                                    <label><input type="radio" name="scheduled" v-bind:value="false" v-model="post.scheduled"> Publish when "Publish" button is clicked.</label>
                                </div>
                                <div>
                                    <label><input type="radio" name="scheduled" v-bind:value="true" v-model="post.scheduled"> Schedule for a future date &hellip;</label>
                                </div>
                            </div>
                        </div>
                        <div v-if="true" class="form-group">
                            <label>Publish Date</label>
                            <datetime-picker :value="1447512468838"></datetime-picker>
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
                <div v-if="false" class="col-md-6" id="preview-bootstrap-col" >
                    <div class="menu-link-group">
                        <a class="'active': previewOrCollaborate==='preview'" href="javascript:;" onclick="switchPreviewOrCollaborate.bind(this, 'preview')">Live preview</a> | 
                        <a class="'active': previewOrCollaborate==='collaborate'" href="javascript:;" onclick="switchPreviewOrCollaborate.bind(this, 'collaborate')">Collaboration</a>
                    </div>
                    <div id="above-preview-row"></div>
                    <div show="previewOrCollaborate==='collaborate'">
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
                    <div show="previewOrCollaborate==='preview'" id="live-preview-column">
                        <div style="margin-bottom: 1em; margin-top: 1em; text-align: center;">
                            <div style="" class="btn-group" data-toggle="buttons">
                                <label class="btn btn-default btn-xs active">
                                    <input type="radio" name="options" id="option1" autocomplete="off"  onchange="switchPreviewMode.bind(this, 'mobile')" checked> Mobile
                                </label>
                                <label class="btn btn-default btn-xs">
                                    <input type="radio" name="options" id="option2" autocomplete="off" onchange="switchPreviewMode.bind(this, 'tablet')"> Tablet
                                </label>
                                <label class="btn btn-default btn-xs">
                                    <input type="radio" name="options" id="option3" autocomplete="off" onchange="switchPreviewMode.bind(this, 'desktop')"> Desktop
                                </label>
                            </div>
                        </div>
                        <div show="dirty" class="preview-dirty-overlay">Blog post being edited.<br>Waiting to refresh preview.</div>
                        <iframe sandbox="allow-forms allow-scripts" class="preview-iframe mobile" style="width: 100%; height: 100%; min-height: 600px; " name="previewIframe"></iframe>
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
             lastAutoSaveAt: 'A while ago',
             contentId: 0,
             versionId: 0,
             templateElements: [],
             shouldBeScheduled: false,
             lastWidgetCount: 0,
             post: {},
             dirty: false,
             siteUrl: 'http://localhost' // stPublisherAdminContext.siteUrl
         };
     },
     route: {
         data: function(transition) {
             var contentId = parseInt(this.$route.params.contentId, 10);
             var data = {};
             var callback = function(o) {
                 var data = {};
                 data.post = o.post;
                 data.versionId = o.post.id;
                 data.contentId = contentId;
                 data.templateElements = o.templateElements;
                 data.shouldBeScheduled = o.post.scheduled;
                 data.lastWidgetCount = o.post.widgets.length;
                 transition.next(data);
             };
             this.loadContent(contentId, callback);
         }
     },
     methods: {
         loadContent: function(contentId, callback) {
             var url = '/st-publisher/posts/' + contentId + "/latest-draft";
             stallion.request({
                 url: url,
                 method: 'GET',
                 success: function (o) {
                     callback(o);
                 }
             });
         },
         publishPost: function() {

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
         onMarkdownChange: function() {

         },
         onUrlTouched: function() {

         },
         onTitleChanged: function() {

         },
         calculateSlug: function() {
             return "/" + stPublisher.slugify(this.post.title.toLowerCase());
         },
         slugTouched: function() {
             console.log('slug touched');
         },
         optionsChange: function(a, b, c) {
             console.log('options change', a, b, c);
         },
         saveDraft: function() {

         },
         saveDraftAndReloadPreview: function() {

         }
     },
     watch: {
         'post.metaDescription': 'optionsChange',
         'post.headHtml': 'optionsChange',
         'post.footerHtml': 'optionsChange',
         'post.slug': 'slugTouched',
         'post.authorId': 'optionsChange'
     }
 }
</script>
