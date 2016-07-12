
<template>
    <div>
        <textarea style="" id="my-tiny-editor"></textarea>
        <widget-modal v-if="showWidgetModal" :shown.sync="showWidgetModal" :widget-type="activeWidgetType" :widget-data="activeWidgetData" :callback="insertWidgetCallback"></widget-modal>
        <insert-link-modal v-if="showInsertLinkModal" :shown.sync="showInsertLinkModal" :callback="insertLinkCallback"></insert-link-modal>
    </div>
</template>
<script>
 module.exports = {
     data: function() {
         return {
             showWidgetModal: false,
             activeWidgetType: '',
             activeWidgetData: {},
             showInsertLinkModal: false
         }
     },
     ready: function() {
         var self = this;
         requirejs([
             'tinyMCE'
         ], function (tinymce) {
             console.log(tinymce);
             //var ele = $(this.$el).find('textarea').get(0);
             self.tinymce = tinymce;
             stPublisher.initTinyImagePlugin(tinymce, self);
             
             tinymce.init({
                 selector: '#my-tiny-editor',
                 statusbar: false,
                 plugins: 'autoresize textcolor colorpicker textpattern imagetools paste charmap example',
                 toolbar1: '| bold italic | alignleft aligncenter alignright | bullist numlist outdent indent blockquote | link image | stlink stimage stinsert ',
                 menubar: false,
                 //toolbar2: 'bold forecolor backcolor ',
                 init_instance_callback : function(editor) {
                     self.editor = editor;
                     editor.vueTag = self;
                 }                 
             });

             // your code here
         });         
     },
     methods: {
         insertLinkCallback: function(link) {
             console.log('data ', link);
             var $ele = $('<a></a>').attr('href', link).html(link);
             //debugger;
             this.editor.insertContent($ele.get(0).outerHTML);
         },
         insertWidgetCallback: function(widget) {
             console.log('insertWidgetCallback');
             this.editor.insertContent(widget.html);
         }
     }
 }
</script>
