
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

        editor.addButton('stinsert', {
            text: 'Insert +',
            icon: false,
            onclick: function() {
                editor.vueTag.activeWidgetType = '';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stlink', {
            text: '',
            icon: 'icon-link icomoon',
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
            onclick: function() {
                editor.vueTag.activeWidgetType = 'image-collection';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stembed', {
            text: 'Insert Embed',
            icon: false,
            onclick: function() {
                editor.vueTag.activeWidgetType = 'embed';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });
    });  
        
};
