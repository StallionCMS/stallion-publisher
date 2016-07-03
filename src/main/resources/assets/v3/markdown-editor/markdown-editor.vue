

<template>
    <div class="markdown-editor">
        <textarea class="form-control"></textarea>
        <widget-modal v-if="showWidgetModal" :shown.sync="showWidgetModal"></widget-modal>
        <insert-link-modal v-if="showInsertLinkModal" :shown.sync="showInsertLinkModal" :callback="insertLinkCallback"></insert-link-modal>
        <paste-html-modal v-if="showPasteHtml" :shown.sync="showPasteHtml"></paste-html-modal>
    </div>
</template>

<script>
 module.exports = {
     props: {
         markdown: '',
         widgets: [],
         changeCallback: Function
     },
     data: function() {
         var originalWidgets = this.widgets || [];
         var originalMarkdown = this.markdown || '';
         // Make a deep-copy of widgets         
         this.widgets = JSON.parse(JSON.stringify(originalWidgets));
         this.markdown = this.parseOutWidgetHtmlFromContent(originalMarkdown);
         return {
             cm: null,
             simplemde: null,
             lineWidgets: [],
             lineWidgetByGuid: {},
             editorToolbarTop: 140,
             editorToolbarWidth: 500,
             originalWidgets: originalWidgets,
             originalMarkdown: originalMarkdown,
             showWidgetModal: false,
             showPasteHtmlModal: false,
             showInsertLinkModal: false
         };
     },
     attached: function() {
         console.log('markdown ', this.markdown);
         this.simplemde = new SimpleMDE({
             element: $(this.$el).find('textarea').get(0),
             autoDownloadFontAwesome: false,
             toolbar: this.makeToolbar(),
             initialValue: this.markdown
         });
         this.cm = this.simplemde.codemirror;
         this.attachCodeMirrorEventHandlers();
         this.setupScrollHandling();
     },
     methods: {
         getData: function() {
             return {
                 markdown: this.markdown,
                 widgets: JSON.parse(JSON.stringify(this.widgets))
             }
         },
         setData: function(markdown, widgets) {
             this.originalMarkdown = markdown;
             this.originalWidgets = widgets;
             this.widgets = JSON.parse(JSON.stringify(widgets));
             this.markdown = markdown;
         },
         parseOutWidgetHtmlFromContent: function(content) {
             return content.replace(/(\s\s\n|)<rawHtml><!\-\-widget:[\w\-]*\-\->[\s\S]*?<!\-\-end-widget:[\w\-]*\-\-><\/rawHtml>/g, '')
         },
         insertWidget: function() {
             
         },
         insertLink: function() {
             this.showInsertLinkModal = true;
         },
         insertLinkCallback: function(link) {
             console.log('link insert?', link);
         },
         insertImage: function() {

         },
         pasteRichContent: function() {

         },
         attachCodeMirrorEventHandlers: function() {
             var self = this;
             var cm = self.cm;

             cm.on('beforeChange', function() {

             });

             cm.on('change', function() {

             });

             cm.on('cursorActivity', function() {

             });
         },
         setupScrollHandling: function() {
             
         },
         makeToolbar: function() {
             var self = this;
             var toolbar = [
	         {
		     name: "bold",
		     className: "icon icon-bold",
		     title: "Bold",
                     action: SimpleMDE.toggleBold,
		     default: true
	         },
	         {
		     name: "italic",
		     action: SimpleMDE.toggleItalic,
		     className: "icon icon-italic",
		     title: "Italic",
		     default: true
	         },
	         {
		     name: "strikethrough",
		     action: SimpleMDE.toggleStrikethrough,
		     className: "icon icon-strikethrough",
		     title: "Strikethrough"
	         },
	         {
		     name: "heading",
		     action: SimpleMDE.toggleHeadingSmaller,
		     className: "icon icon-font-size",
		     title: "Heading",
		     default: true
	         },
                 {
		     name: "code",
		     action: SimpleMDE.toggleCodeBlock,
		     className: "icon icon-embed2",
		     title: "Code"
	         },
	         {
		     name: "quote",
		     action: SimpleMDE.toggleBlockquote,
		     className: "icon icon-quotes-left",
		     title: "Quote",
		     default: true
	         },
	         {
		     name: "unordered-list",
		     action: SimpleMDE.toggleUnorderedList,
		     className: "icon icon-list2",
		     title: "Generic List",
		     default: true
	         },
	         {
		     name: "ordered-list",
		     action: SimpleMDE.toggleOrderedList,
		     className: "icon icon-list-numbered",
		     title: "Numbered List",
		     default: true
	         },
                 {
		     name: "link",
		     action: this.insertLink,
		     className: "icon icon-link",
		     title: "Create Link",
		     default: true
	         },
	         {
		     name: "image",
		     action: this.insertImage,
		     className: "icon icon-image2",
		     title: "Insert Image",
		     default: true
	         },
	         {
		     name: "pasteRich",
		     action: this.pasteRichContent,
		     className: "icon icon-paste",
		     title: "Paste Rich Content",
		     default: true
	         },
                 
                 {
		     name: "fullscreen",
		     action: SimpleMDE.toggleFullScreen,
		     className: "icon icon-enlarge2 no-disable no-mobile",
		     title: "Toggle Fullscreen",
		     default: true
                 },
	         {
		     name: "undo",
		     action: SimpleMDE.undo,
		     className: "icon icon-undo no-disable",
		     title: "Undo"
	         },
	         {
		     name: "redo",
		     action: SimpleMDE.redo,
		     className: "icon icon-redo no-disable",
		     title: "Redo"
	         },             
                 {
                     name: "insertWidget",
                     action: function(editor) {
                         // Add your own code
                         self.insertWidget();
                     },
                     className: "editor-insert-button",
                     title: "Insert Widget",
                 },
                 "|", // Separator
             ];
             return toolbar;
             
         }
     }
 }
</script>
