
stPublisher.initTinyImagePlugin = function(tinymce, vueTag) {
    tinymce.PluginManager.add('example', function(editor, url) {
        // Add a button that opens a window
        editor.addButton('stimage', {
            text: 'Insert Image',
            icon: false,
            onclick: function() {
                editor.vueTag.activeWidgetType = 'image';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stinsert', {
            text: 'Insert [+]',
            icon: false,
            onclick: function() {
                editor.vueTag.activeWidgetType = '';
                editor.vueTag.activeWidgetData = {};
                editor.vueTag.showWidgetModal = true;
            }
        });

        editor.addButton('stlink', {
            text: 'Link [+]',
            icon: false,
            onclick: function() {
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