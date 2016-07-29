
stPublisher.initTinyImagePlugin = function(tinymce) {
    tinymce.PluginManager.add('example', function(editor, url) {
        // Add a button that opens a window
        editor.addButton('stimage', {
            text: '',
            icon: 'icon-image2 icomoon',            
            onclick: function() {
                editor.vueTag.activeWidgetType = 'image';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stimagesimple', {
            text: '',
            icon: 'icon-image2 icomoon',
            tooltip: 'Insert Image',
            onclick: function() {
                editor.vueTag.activeWidgetType = 'image-simple';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });
 
        editor.addButton('stembed', {
            text: '',
            icon: 'icon-embed2 icomoon',
            tooltip: 'Embed Tweet, Video, etc.',
            onclick: function() {
                editor.vueTag.activeWidgetType = 'embed';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });
        
        
        editor.addButton('stinsert', {
            text: 'Insert +',
            icon: false,
            tooltip: 'Insert Image, Gallery, Embed, etc.',
            onclick: function() {
                editor.vueTag.activeWidgetType = '';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stlink', {
            text: '',
            icon: 'icon-link icomoon',
            tooltip: 'Insert Link',
            onclick: function() {
                var node = editor.selection.getNode();
                if (node.tagName.toUpperCase() === 'A') {
                    editor.vueTag.activeLinkUrl = node.getAttribute('href');
                    editor.vueTag.activeLinkText = node.innerHTML;
                    editor.selection.select(node);
                } else {
                    editor.vueTag.activeLinkText = editor.selection.getContent();
                }
                editor.vueTag.showInsertLinkModal = true;
            }
        });
        

        editor.addButton('stgallery', {
            text: 'Insert Gallery',
            icon: false,
            tooltip: 'Insert Gallery',
            onclick: function() {
                editor.vueTag.activeWidgetType = 'image-collection';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

    });  
        
};
