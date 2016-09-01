<style lang="scss">
 .tinymce-editor-vue {
     .tiny-target {
         width: 100%;
         height: 300px;
         border: 1px solid #DDD;
         visibility: hidden;
     }
     .loading-overlay {
         position: absolute;
         z-index: 100;
         font-size: 20px;
         padding-top: 20px;
         padding-left: 20px;
     }
 }
</style>
<template>
    <div class="tinymce-editor-vue">
        <div class="loading-overlay">Loading editor &hellip;</div>
        <textarea :name="name" style="" class="tiny-target" v-model="html" ></textarea>
        <widget-modal v-if="showWidgetModal" :shown.sync="showWidgetModal" :widget-type="activeWidgetType" :widget-data="activeWidgetData" :callback="insertWidgetCallback"></widget-modal>
        <insert-link-modal v-if="showInsertLinkModal" :shown.sync="showInsertLinkModal" :callback="insertLinkCallback" :hide-internal-pages="options.hideInternalPages" :link="activeLinkUrl" :text="activeLinkText"></insert-link-modal>
    </div>
</template>
<script>
 module.exports = {
     props: {
         html: String,
         widgets: Array,
         tinyOptions: Object,
         options: Object,
         name: String,
         changeCallback: Function,
         onKeyPress: Function,
         onSetup: Function,
         onInit: Function
     },
     data: function() {
         return {
             showWidgetModal: false,
             activeWidgetType: '',
             activeWidgetData: {},
             activeLinkText: '',
             activeLinkUrl: '',
             tinymce: null,
             editor: null,
             showInsertLinkModal: false,
             ticks: new Date().getTime()
         }
     },
     ready: function() {
         var self = this;
         var id = 'tiny-' + stPublisher.generateUUID();
         $(this.$el).find('textarea').attr('id', id);

         var customOptions = this.tinyOptions || {};
         
         requirejs([
             'tinyMCE'
         ], function (tinymce) {
             //var ele = $(this.$el).find('textarea').get(0);
             self.tinymce = tinymce;
             stPublisher.initStallionButtonsPlugin(tinymce);
             stPublisher.initHeadersPlugin(tinymce);

             var options = {
                 selector: '#' + id,
                 statusbar: false,
                 plugins: 'autoresize textcolor colorpicker textpattern imagetools paste charmap example headers link',
                 toolbar1: 'bold italic | styleselect | bullist numlist outdent indent blockquote removeformat | undo redo | stlink stimage stinsert ',
                 menubar: false,
                 content_css: stPublisherAdminContext.siteUrl + '/st-resource/publisher/tinymce/tinymce-content.css?ts=' + self.ticks + ',' + stPublisherAdminContext.siteUrl + '/st-resource/publisher/public/contacts-always.css?vstring=' + self.ticks,
                 init_instance_callback : function(editor) {

                     self.editor = editor;
                     editor.vueTag = self;
                     $(self.$el).find('.loading-overlay').hide();
                     self.addWidgetToolbarToHtml();

                     if (self.onInit) {
                         self.onInit(editor, self);
                     }
                     
                 },
                 setup: function(editor) {

                     editor.on('change', function(e) {
                         if (self.changeCallback) {
                             self.changeCallback();
                         }
                     });

                     if (self.onKeyPress) {
                         editor.on('keypress', function(e) {
                             if (self.onKeyPress) {
                                 self.onKeyPress(e);
                             }
                         });
                     }

                     if (self.onSetup) {
                         self.onSetup(editor, self);
                     }
                 }                 
             };
             Object.keys(customOptions).forEach(function(key) {
                 options[key] = customOptions[key];
             });
             
             tinymce.init(options);

         });         
     },
     methods: {
         getData: function() {
             var self = this;
             if (!this.editor) {
                 return {
                     html: this.html,
                     widgets: this.widgets
                 }
             }
             return {
                 widgets: this.getEditorCleanedWidgets(),
                 html: this.getEditorCleanHtml()
             };
         },
         getEditorCleanHtml: function() {
             var self = this;
             var html = this.editor.getContent({format : 'raw'});
             var $html = $('<div>' + html + '</div>');
             $html.find('.widget-toolbar').remove();
             this.widgets.forEach(function(w) {
                 var eleId = 'wrap-widget-' + w.guid;
                 var $existing = $(self.editor.getBody()).find('#' + eleId);
                 if ($existing.length < 1) {
                     return;
                 }
                 // Reset the HTML
                 $existing.find('.widget-html').html(w.html);
             });
             return $html.html();
         },
         getEditorCleanedWidgets: function() {
             var self = this;
             var cleanedWidgets = [];
             this.widgets.forEach(function(w) {
                 var eleId = 'wrap-widget-' + w.guid;
                 var $existing = $(self.editor.getBody()).find('#' + eleId);
                 if ($existing.length > 0) {
                     cleanedWidgets.push(w);
                 }
             });
             return cleanedWidgets;
         },
         addWidgetToolbarToHtml: function() {
             var self = this;
             this.widgets.forEach(function(widget) {
                 var eleId = 'wrap-widget-' + widget.guid;
                 var $wrapper = $(self.editor.getBody()).find('#' + eleId);
                 if ($wrapper.length < 1) {
                     return;
                 }
                 self.addToolbarForWidgetWrapper(widget, $wrapper);
             });
         },
         insertLinkCallback: function(link, text) {
             var self = this;
             if (!text) {
                 text = this.editor.selection.getContent();
             }
             if (!text) {
                 text = link;
             }
             console.log('insert link ', link, text);
             var $ele = $('<a></a>').attr('href', link).html(text);
             this.editor.insertContent($ele.get(0).outerHTML);
         },
         insertWidgetCallback: function(widget) {
             var self = this;
             console.log('insertWidgetCallback');
             var eleId = 'wrap-widget-' + widget.guid;
             var existing = $(this.editor.getBody()).find('#' + eleId);
             if (existing.length) {
                 self.editor.undoManager.transact(function() {
                     existing.find('.widget-html').html(widget.html);
                 });
                 this.widgets.forEach(function(w) {
                     if (w.guid === widget.guid) {
                         w.data = widget.data;
                         w.html = widget.html;
                         return false;
                     }
                 });
             } else {
                 self.editor.undoManager.transact(function() {
                     self.editor.insertContent('<div contenteditable="false" id="' + eleId + '" class="st-widget-wrapper st-widget-' + widget.type + '"  ><div class="widget-html">' + widget.html + '</div></div>');
                     self.widgets.push(widget);
                     var $wrapper = $(self.editor.getBody()).find('#' + eleId);
                     self.addToolbarForWidgetWrapper(widget, $wrapper);
                 });
             }
         },
         addToolbarForWidgetWrapper: function(widget, $wrapper) {
             var self = this;
             var eleId = $wrapper.attr('id');
             $wrapper.prepend($('<div class="widget-toolbar"><a class="widget-edit-link" href="javascript:;">Edit widget</a> | <a class="delete-widget-link" href="javascript:;">Delete</a></div>'));
             $wrapper.find('.widget-edit-link').bind('click', function() {
                 self.activeWidgetType = widget.type;
                 self.activeWidgetData = widget;
                 self.showWidgetModal = true;
             });
             $wrapper.find('.delete-widget-link').bind('click', function() {
                 //debugger;
                 self.editor.undoManager.transact(function() {
                     $wrapper.remove();
                 });
             });
             
         }
     }
 }
</script>
