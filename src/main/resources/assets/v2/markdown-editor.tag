<markdown-editor>
    <textarea name="markdownTextarea" class="form-control"></textarea>
    <script>
     var self= this;
     self.markdown = self.opts.markdown || null;
     self.oldMarkdown = self.markdown;
     self.widgets = self.opts.widgets ? JSON.parse(JSON.stringify(self.opts.widgets)) : [];
     self.lineWidgets = [];
     self.lineWidgetByGuid = {};
     self.editorToolbarTop = 140;
     self.editorToolbarWidth = 500;
     self.simplemde = null;
     self.loaded = false;
     var cm = null;

     var toolbarSelector = '#' + self.opts.id + ' .editor-toolbar';
     
     /**************************/
     /* On load                */
     
     this.on('mount', function(){
         if (self.markdown) {
             self.markdown = parseOutWidgetHtmlFromContent(self.markdown);
         }
         
         self.simplemde = new SimpleMDE({
             toolbar: makeToolbar(), 
             element: self.markdownTextarea
         });
         cm = self.simplemde.codemirror;
         //self.simplemde.codemirror.setOption('gutters', ['commentsGutter']);
         //self.simplemde.codemirror.setOption('lineNumbers', true);
         self.simplemde.codemirror.on('beforeChange', beforeChange);
         self.simplemde.codemirror.on("change", onEditorChange);
         self.simplemde.codemirror.on('cursorActivity', cursorMoves);
         self.calculatedFrameHeight = $(window).height() - 150;
         setTimeout(function() {
             self.editorToolbarTop = $(toolbarSelector).offset().top;
             self.editorToolbarWidth = $(toolbarSelector).width() + 22;
         }, 100);
         function onScrollOrFocus () {
             if ($(window).scrollTop() > self.editorToolbarTop && cm.hasFocus()) {
                 $(toolbarSelector).css({'background-color': 'white', 'opacity': 1, 'border-bottom': '1px solid #999', 'position': 'fixed', 'z-index': 1000, 'top': '0px', 'width': self.editorToolbarWidth + 'px'});
             } else {
                 $(toolbarSelector).css({'position': 'static', 'width': '100%'});
             }

         }
         cm.on('blur', onScrollOrFocus);
         cm.on('focus', onScrollOrFocus);         
         $(window).scroll(onScrollOrFocus);
         
         if (self.markdown === null) {
             self.simplemde.value("Loading ...");
         } else {
             self.update({loaded: true});             
             setTimeout(function() {
                 self.simplemde.value(self.markdown);
                 loadAllLineWidgetsFromPost();
             }, 10);
         }
         
     });

     /*******************/
     /* Public methods */

     self.setData = function(markdown, widgets) {
         self.markdown = parseOutWidgetHtmlFromContent(markdown);
         if (widgets !== undefined) {
             self.widgets = JSON.parse(JSON.stringify(widgets));
         }
         //self.simplemde.value(content);
         self.update();
     }

     self.getData = function() {
         updateWidgetStateFromCodeMirror();
         return {
             markdown: getContentWithWidgetHtml(self.markdown),
             widgets: JSON.parse(JSON.stringify(self.widgets))
         };
     };


     /**************************/
     /* Other Event Handlers */

     self.on('update', function() {
         if (self.simplemde) {
             if (self.markdown === null) {
                 self.simplemde.value('Loading...');
             } else if (self.markdown != self.oldMarkdown) {
                 self.simplemde.value(self.markdown);
                 loadAllLineWidgetsFromPost();
                 self.oldMarkdown = self.markdown;
                 self.loaded = true;
             }
             
         }
     });

    

     function onEditorChange(evt, b, c) {
         if (!self.loaded) {
             return;
         }
         if (self.opts.onchange) {
             self.opts.onchange.call(evt);
         }
     };

     function triggerChange() {
         if (self.opts.onchange) {
             self.opts.onchange.call();
         }
     }
     
     /******************************/
     /* Adding widgets to the page */

     function insertWidget() {
         showRiotModal({
             mountOpts: {
                 callback: function(widgetData) {
                     var line = addLineWidget(widgetData, true);
                     self.widgets.push(widgetData);
                     widgetData.line = line;
                     triggerChange();
                 }
             }
         });         
     }

     function loadAllLineWidgetsFromPost() {
         self.lineWidgets.forEach(function(lineWidget) {
             lineWidget.clear();
         });
         self.lineWidgets = [];
         self.widgets.forEach(function(widgetData) {
             addLineWidget(widgetData, false);
         });
     };


     function addLineWidget(widgetData, atCursor) {
         var cm = self.simplemde.codemirror;
         var $node = $('<span class="line-widget line-widget-' + widgetData.type + '"><span class="widget-label">' + widgetData.label + '</span> <span class="line-widget-edit btn btn-default btn-xs">Edit widget</span> <span class="line-widget-delete btn btn-xs btn-default">remove &#xd7;</span><span class="widget-preview">' + (widgetData.previewHtml || '') + '</span></span>').addClass('line-widget');
         //var widget = self.simplemde.codemirror.addLineWidget(line, $node.get(0), {});
         var cursor = self.simplemde.codemirror.doc.getCursor();
         var line = widgetData.line;
         if (line === undefined || atCursor) {
             line = cursor.line;
         }
         var thisLine = cm.getRange({line: line, ch:0}, {line: line+1, ch: 0}).trim('\n');
         if (thisLine.length > 0) {
             if (cursor.ch > 0) {
                 cm.replaceRange("\n", {line:line, ch:999999});
                 line = line + 1;
                 cm.execCommand('goLineDown');
             } else {
                 cm.replaceRange("\n", {line:line, ch:0});
                 cm.execCommand('goLineUp');
             }
         }
         cm.replaceRange("", {line:line, ch:0});
         cm.doc.addLineClass(line, 'wrap', 'editor-line-with-widget');
         var insertLeft = false;
         if (line < 1 || line <= (cm.doc.lineCount() / 2)) {
             insertLeft = true;
         }
         
         var marker = self.simplemde.codemirror.markText(
             {line: line, ch:0}, {line: line, ch:0},
             {
                 replacedWith: $node.get(0),
                 clearWhenEmpty: false,
                 clearOnEnter: false,
                 inclusiveRight: false,
                 inclusiveLeft: false,
                 insertLeft: insertLeft,
                 addToHistory: false,
                 widgetGuid: widgetData.guid
             });
         marker.widgetGuid = widgetData.guid;
         cm.execCommand('goLineDown');
         marker.deleted = false;
         self.lineWidgets.push(marker);
         self.lineWidgetByGuid[widgetData.guid] = marker;
         $node.find('.line-widget-delete').click(function() {
             cm.doc.removeLineClass(line, 'wrap', 'editor-line-with-widget');
             var newWidgets = [];
             self.widgets.forEach(function(existing) {
                 if (existing.guid && existing.guid !== widgetData.guid) {
                     newWidgets.push(existing);
                 }
             });
             self.widgets = newWidgets;
             marker.deleted = true;
             marker.clear();
             widgetData.deleted = true;
             triggerChange();
         });
         $node.find('.line-widget-edit').click(function() {
             showRiotModal({
                 mountOpts: {
                     widgetData: widgetData,
                     callback: function(widgetData) {
                         self.widgets.forEach(function(existing) {
                             if (existing.guid === widgetData.guid) {
                                 existing.data = widgetData.data;
                                 existing.html = widgetData.html;
                                 existing.isBlock = widgetData.isBlock;
                                 existing.previewHtml = widgetData.previewHtml;
                                 return false;
                             }
                         });
                         $node.find('.widget-preview').html(widgetData.previewHtml);
                         triggerChange();
                     }
                 }
             });         
         });
         return line;
         
     }

     /****************************************/
     /* Widget to and from HTML         */

     function parseOutWidgetHtmlFromContent(content) {
         return content.replace(/(\s\s\n|)<rawHtml><!\-\-widget:[\w\-]*\-\->[\s\S]*?<!\-\-end-widget:[\w\-]*\-\-><\/rawHtml>/g, '');
     }

     function getContentWithWidgetHtml() {
         var content = self.simplemde.value();
         var lines = content.split('\n');
         var widgetByLine = {};
         self.widgets.forEach(function(widgetData) {
             if (widgetByLine[widgetData.line] === undefined) {
                 widgetByLine[widgetData.line] = [];
             }
             widgetByLine[widgetData.line].push(widgetData);
         });
         var newLines = [];
         for(var i = 0; i<lines.length; i++) {
             var line = lines[i];
             var widgets = widgetByLine[i];
             if (!widgets) {
                 newLines.push(line);
                 continue;
             }
             widgets.forEach(function(widgetData) {
                 var html = '<rawHtml><!--widget:' + widgetData.guid + '-->' + widgetData.html + '<!--end-widget:' + widgetData.guid + '--></rawHtml>';
                 if (widgetData.isBlock) {
                     html = "" + html + "";
                 }
                 line += html;
             });
             newLines.push(line);
         };
         var content = newLines.join('\n');
         return content;
     };


          
     /****************************************/
     /* Widget state and consistency         */
 

     /**
      * Removes all widgets that have been deleted in the editor from the self.post object.
      * Syncs the line numbers for each widget in self.widgets with the actual line number in the editor
     */
     function updateWidgetStateFromCodeMirror() {
         var cm = self.simplemde.codemirror;
         var newWidgets = []
         var widgetByGuid = {};
         self.widgets.forEach(function(widget) {
             widgetByGuid[widget.guid] = widget;
         });
         cm.getAllMarks().forEach(function(mark) {
             var widget = widgetByGuid[mark.widgetGuid];
             if (!widget) {
                 return;
             }
             if (!mark.lines) {
                 return;
             }
             widget.line = cm.doc.getLineNumber(mark.lines[0]);
             newWidgets.push(widget);
         });
         self.widgets = newWidgets;
     }

     var lastLine = 0;
     function cursorMoves() {
         return;
         var cm = self.simplemde.codemirror;
         var cursor = cm.doc.getCursor();
         var lineInfo = cm.lineInfo(cursor.line);
         var marks = cm.doc.findMarks({line: cursor.line, ch: 0}, {line: cursor.line, ch: 1000});
         if (marks && marks.length) {
             if (cursor.line + 1 === cm.doc.lineCount()) {
                 cm.replaceRange("\n", {line:cursor.line+1, ch:999999});
             } else if (cursor.line === 0) {
                 cm.replaceRange("\n", {line:0, ch:0});
             }
             if (cursor.line > lastLine) {
                 //cm.doc.setCursor({line: cursor.line + 1, ch: cursor.ch});
                 cm.execCommand('goLineDown');
                 lastLine = cursor.line + 1;
             } else {
                 var newLine = cursor.line -1;
                 cm.execCommand('goLineUp');
                 lastLine = cursor.line - 1;
             }
         } else {
             lastLine = cursor.line;
         }
     };

     self.forceAllowChange = false;
     function beforeChange(cm, change) {
         if (self.forceAllowChange) {
             self.forceAllowChange = false;
             return;
         }
         //if (change.from.line === change.to.line) {
         //    return;
         //}

         var marks = cm.doc.findMarks({line: change.from.line, ch: 0}, {line: change.to.line, ch: 999999});
         if (marks && marks.length > 0) {
             //debugger;
         }
         if (marks && marks.length > 0) {
             change.cancel();
             if (change.origin === '+input' && change.text.length == 2 && change.text[0] === '' && change.text[1] === '') {
                 //cm.replaceRange("\n\n", {line:change.to.line-1, ch:99999});
                 if (change.to.line === 0) {
                     //cm.replaceRange("\n", {line:change.to.line, ch:0});
                     //cm.doc.setValue("\n" + cm.doc.getValue());
                     //loadAllLineWidgetsFromPost();
                     insertLine(cm, 0);
                 } else {
                     insertLine(cm, change.to.line + 1);
                     //self.forceAllowChange = true;
                     //cm.doc.setValue(cm.doc.getValue() + "\n");
                     //loadAllLineWidgetsFromPost();
                     //m.doc.setCursor({line:cm.doc.lineCount(), ch:0});
                     //cm.replaceRange("\n\n", {line:change.to.line, ch:99999});
                     
                 }
             }
         }
     };

     function insertLine(cm, index) {
         
         var lines = cm.doc.getValue().split('\n');
         if (index >= lines.length) {
             lines.push('');
             index = lines.length -1;
         } else {
             lines.splice(index, 0, '');
         }

         var newValue = lines.join('\n');
         self.forceAllowChange = true;
         
         self.widgets.forEach(function(widgetData) {
             if (widgetData.line >= index) {
                 widgetData.line++;
             }
         });

         cm.doc.setValue(newValue);
         loadAllLineWidgetsFromPost();
         //var cursor = cm.doc.getCursor();
         
         cm.doc.setCursor({line: index, ch: 0});
         
         

     }

     /*****************************/
     /* Editor Helpers            */

     function makeToolbar() {
         var toolbar = [
             "bold",
	     "italic",
             "strikethrough",
             "heading",
             "code",
             "quote",
             "unordered-list",
             "ordered-list",
             "link",
             "table",
             "fullscreen",
             "undo",
             "redo",
             {
                 name: "insertWidget",
                 action: function(editor) {
                     // Add your own code
                     insertWidget();
                 },
                 className: "editor-insert-button",
                 title: "Insert Widget",
             },
             "|", // Separator
         ];
         return toolbar;
     };

     
     
     
     
    </script>
</markdown-editor>



<widget-modal>
    <div if={!widgetType} class="widget-chooser">
        <div class="modal-header"><h4>Choose a widget to insert</h4></div>
        <div class="modal-body">
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'embed')}>
                    HTML Embed (Youtube Video, Slideshare, etc.)
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'image')}>
                    Image
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'image-collection')}>
                    Image Collection/Gallery
                </a>
            </div>
        </div>
        <div class="modal-footer">
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <div if={widgetType}>
        <div class="modal-header"><h4>Configure widget</h4></div>
        <div class="modal-body" id="widgetConfigureTarget"">
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Update</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     self.widgetType = '';
     self.activeTag = null;

     self.selectWidget = function(widgetType, widgetData) {
         var widgetTag = self.tags[widgetType + 'Widget'];
         if (!widgetData || !Object.keys(widgetData).length) {
             widgetData = {data: {}, type: widgetType, label: '', html: ''};
         }
         self.update({widgetType: widgetType});
         self.activeTag = appendRiotTag(self.widgetConfigureTarget, widgetType + '-widget-configure', {widgetData: widgetData});
     };

     self.saveWidget = function() {
         var widgetTag = self.activeTag;
         var guid = '';
         if (self.widgetData && self.widgetData.guid) {
             guid = self.widgetData.guid;
         }
         if (widgetTag && widgetTag.buildData) {
             var widgetData = widgetTag.buildData();
             widgetData.type = self.widgetType;
             if (!guid) {
                 widgetData.guid = generateUUID();
             } else {
                 widgetData.guid = guid;
             }
             self.opts.callback(widgetData);
             self.close();
         } else {

         }
     };
     
     self.close = function() {
         self.unmount();
         self.opts.parentElement.modal('hide');
     };

     self.on('mount', function() {
         if (self.widgetData && self.widgetData.length > 0 && self.widgetData.type) {
             self.selectWidget(self.widgetData.type, self.widgetData);
         }
     });

     if (self.opts.widgetData) {
         self.widgetData = JSON.parse(JSON.stringify(self.opts.widgetData));
     };
     
    </script>
</widget-modal>

<image-widget-modal>
    <div>
        <div class="modal-header"><h4>Choose Image</h4></div>
        <div class="modal-body">
            <image-widget-configure name="imageWidget"></image-widget-configure>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Update</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     
     self.close = function() {
         self.unmount();
         self.opts.parentElement.modal('hide');
     };
     
     self.saveWidget = function() {
         var widgetTag = self.tags['imageWidget'];
         var widgetData = widgetTag.buildData();
         self.opts.callback(widgetData);
         self.close();
     };

     
    </script>
</image-widget-modal>

<embed-widget-configure>
    <div>
        <ul class="nav nav-tabs" role="tablist">        
            <li role="presentation" class="{'active': tab==='embedLink'}"><a class="" href="javascript:;" onclick={showTab.bind(this, 'embedLink')}>Embed from a URL</a></li>
            <li role="presentation" class="{'active': tab==='embedHtml'}"><a href="javascript:;"  onclick={showTab.bind(this, 'embedHtml')}>Embed HTML</a></li>
        </ul>
    </div>
    <div if={tab==='embedLink'}>
        <div class="form-group">
            <label>Paste in a link to the content you want to embed. Can be a Youtube video, Twitter message, Facebook post, or anything else, and we'll try to guess the embed code. If you have an exact embed code, click "Embed HTML" and paste it in there.</label>
            <input  class="form-control" autofocus="autofocus" name="embedLink" value={widgetData.data.embedLink}>
            <div class="form-group">
                <button onclick={doEmbed} class="btn btn-primary">Embed</button>
            </div>
        </div>
    </div>
    <div if={tab==='embedHtml'}>
        <div class="form-group">
            <label>The HTML Embed code</label>
            <textarea class="form-control" autofocus="autofocus" name="embedCode" value={widgetData.data.embedCode}></textarea>
        </div>
    </div>
    <script>
     var self = this;
     self.tab = 'embedLink';
     if (self.widgetData && self.widgetData.html) {
         self.tab = 'embedHtml';
     }
     self.code = '';
     self.link = ''
     //self.widgetData = self.opts.widgetData;

     self.showTab = function(tab) {
         self.update({tab: tab});
     }

     self.doEmbed = function() {
         var $ele = $('<div></div>');
         self.link = self.embedLink.value;
         $ele.oembed(self.embedLink.value, {
             afterEmbed: function(o) {
                 if (typeof(o.code) === 'string') {
                     self.code = o.code;
                 } else {
                     self.code = o.code[0].outerHTML;
                 }
                 //debugger;
                 console.log('afterEmbed', self.code);
                 //debugger;
                 self.parent.saveWidget();
             }
         });
     };
     
     self.on('mount', function() {

     });
     self.buildData = function() {
         var code = self.code || self.embedCode.value;
         var previewHtml = '<span style="font-size: 11px; margin-left: 5px;display:inline-block;">' + (self.link || code.substr(0, 120).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')) + '...</span>';
         var widgetData = {
             label: 'Embed Code',
             data: {
                 embedCode: code,
                 embedLink: self.link
             },
             previewHtml: previewHtml,
             html: code
         };
         console.log('widgetData ', widgetData);
         return widgetData;
     };
    </script>
</embed-widget-configure>

//<script>
   function appendRiotTag(target, tagName, opts) {
       var $ele = $('<' + tagName + '></' + tagName + '>');
       $(target).append($ele);
       var id = 'widget-' + generateUUID();
       $ele.attr('id', id);
       return riot.mount('#' + id, opts)[0];
   }
//</script>

<image-collection-widget-configure>
    <div if={showAddImage}>
        <image-widget-configure></image-widget-configure>
    </div>
    <div show={!showAddImage}>
        <h1>Image Collection</h1>
        <div each={img in images}>
            
        </div>
        <button onclick={update.bind(this, {showAddImage: true})} class="btn btn-primary">Add image</button>
        <div show={!showAdvanced}>
            <a href="javascript:;" onclick={update.bind(this, {showAdvanced: true})}>Show advanced</a>
        </div>
        <div show={showAdvanced} class="advanced-options">
            <div>
                <a href="javascript:;" onclick={update.bind(this, {showAdvanced: false})}>Hide advanced options.</a>
            </div>
            <div class="form-group">
                <label><input type="checkbox" name="useDefaultStylingInput" checked={useDefaultStylingInput}> Use default image collection style</label>
            </div>
            <div class="form-group">
                <label>Custom CSS class</label>
                <input type="input" class="form-control" name="customCssClassInput" value={customCssClass} />
            </div>
            
        </div>
    </div>
    <script>
     self.images = [];
     self.useDefaultStyling = true;
     self.customCssClass = '';
     self.showAdvanced = false;

     if (self.widgetData && self.widgetData.data && self.widgetData.data) {
         self.images = self.widgetData.data.images || [];
         self.useDefaultStyling = self.widgetData.data.useDefaultStyling !== false;
         self.customCssClass = self.widgetData.data.customCssClass;
         if (self.customCssClass || !self.useDefaultStyling) {
             self.showAdvanced = true;
         }
     }

     function buildHtml() {
         var $ul = $('<ul class="st-image-collection"></ul>');
         if (self.useDefaultStyling) {
             $ul.addClass('st-image-collection-default');
         }
         if (self.customCssClass) {
             $ul.addClass(self.customCssClass);
         }
         self.images.forEach(function(img) {
             $img = $ul.append('<li></li>').append('<img>');
             $img.src = img.src;
         });
         return $ul.get(0).outerHTML;
     }

     self.buildData = function() {
         self.customCssClass = self.customCssClassInput.value;
         self.useDefaultStyling = $(self.useDefaultStylingInput).is(':checked');
             
         var data = {
             images: self.images,
             useDefaultStyling: self.useDefaultStyling,
             customCssClass: self.customCssClass
         }
         var html = buildHtml();
         var widgetData = {
             isBlock: true,
             label: 'Image Collection',
             previewHtml: '<img src="' + newData.src + '">',
             data: newData,
             html: html
         };
         return widgetData;
     };

     
    </script>
</image-collection-widget-configure>

<image-widget-configure>
    <div if={tab!=='formatting'}>
        <ul class="nav nav-tabs" role="tablist">        
            <li role="presentation" class="active"><a class="" href="javascript:;" onclick={showTab.bind(this, 'library')}>Image Library</a></li>
            <li role="presentation"><a href="javascript:;"  onclick={showTab.bind(this, 'upload')}>Upload</a></li>
            <li role="presentation"><a href="javascript:;" onclick={showTab.bind(this, 'url')}>Web Address (URL)</a></li>
        </ul>
    </div>
    <div if={tab==="library"}>
        <image-library></image-library>
    </div>
    <div if={tab==="upload"}>
        <image-uploader></image-uploader>
    </div>
    <div if={tab==="url"}>
        <div class="form-group"></div>
        <div class="form-group">
            <label>Insert the URL of the image here:</label>
            <input type="text" class="form-control" name="src" >
        </div>
        <div class="form-group">
            <button class="btn btn-primary" onclick={urlChange}>Use this image</button>
        </div>
    </div>
    
    <div if={tab==='formatting'}>
        <div style="border-bottom: 1px solid #F4F4F4; padding-bottom: 1em; margin-bottom: 1em;">
            <img src="{imageUrl}"" style="max-width: 50px; max-width: 50px; display:inline-block; margin-right: 20px; border: 1px solid #CCC;">
            <a target="_blank" href="{imageUrl}">{imageUrl}</a>
            <a class="btn btn-default btn-sm" href="javascript;" onclick={showTab.bind(this, 'library')}>Change Image</a>
        </div>
        <h3>Formatting</h3>

        <div class="row">
            <div class="col-sm-6">
                <h5>Align</h5>                
                <div class="form-group">
                    <label for="alignInline">Inline<br> <input type="radio"  name="alignment" value="inline"></label>
                    <label for="alignCenter">Center <br><input type="radio" name="alignment" value="center"></label>        
                    <label for="alignLeft">Left <br><input type="radio"  name="alignment" value="left"></label>
                    <label for="alignRight">Right <br><input type="radio" name="alignment" value="right"></label>
                        
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label>Alternative text/Hover text (appears for screen readers and mouse hover)</label>
                    <input type="text" class="form-control" name="altText"></textarea>
                </div>
                <div class="form-group">
                    <label>Link URL (optional) -- link to follow when image is clicked on</label>
                    <input type="text" class="form-control" name="linkUrl"></textarea>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <a show={!advancedVisible} href="javascript:;" onclick={toggleAdvancedOptions}>Show advanced options &#171;</a>
                <a show={advancedVisible} href="javascript:;" onclick={toggleAdvancedOptions}>&#187; Hide advanced options</a>
            </div>
        </div>
        <div class="row" show={advancedVisible}>
            <div class="col-sm-6">
                <div class="form-group">
                    <label>Title/Header (appears above image)</label>
                    <textarea class="form-control" style="height: 2.5em;" name="title"></textarea>
                </div>
                <div class="form-group">
                    <label>Caption (appears below image)</label>
                    <textarea class="form-control" style="height: 2.5em;" name="caption"></textarea>
                </div>
            </div>
            <div class="col-sm-6">
                <div>
                    <h5>Margin</h5>
                    <div style="margin-left: 60px;">Top: <input type="number" name="marginTop" >px</div>
                    <div><span>Left: <input type="number" name="marginLeft" >px</span><span style="margin-left: 20px">Right: <input type="number" name="marginRight">px</span></div>
                    <div style="margin-left: 60px;">Bottom: <input type="number" name="marginBottom" >px</div>
                </div>
                <div class="checkbox">
                    <label><input type="checkbox" name="constrain100" value="true"> Constrain to column width?</label>
                </div>
                <div class="form-group">
                    <label>Max-width</label>
                    <input type="number" name="maxWidth" >px
                </div>
                <div class="form-group">
                    <label>Max-height</label>
                    <input type="number" name="maxHeight" >px
                </div>
                <div class="form-group">
                    <label>Min-width</label>
                    <input type="number" name="minWidth" >px
                </div>
                <div class="form-group">
                    <label>Min-height</label>
                    <input type="number" name="minHeight" >px
                </div>
                <div class="form-group">
                    <label>Border width</label>
                    <input type="number" name="borderWidth">
                </div>
                <div class="form-group">
                    <label>Border color</label>
                    <input type="text" name="borderColor">
                </div>
            </div>
        </div>

    </div>
    <script>
     var self = this;
     self.mixin('databind');
     self.tab = 'library';
     self.imageUrl = '';
     self.widgetData = self.widgetData || {data: {}};
     var data = self.widgetData.data;
     data.alignment = data.alignment || 'center';
     data.borderColor = data.borderColor || '#777';
     data.borderWidth = data.borderWidth === undefined ? 1 : data.borderWidth;
     data.marginLeft = data.marginLeft || 0;
     data.marginTop = data.marginTop || 0;
     data.marginRight = data.marginRight || 0;
     data.marginBottom = data.marginBottom || 0;
     data.title = data.title || '';
     data.caption = data.caption || '';
     data.altText = data.altText || '';
     data.maxWidth = data.maxWidth || 1000;
     data.minWidth = data.minWidth || 0;
     data.minHeight = data.minHeight || 0;
     data.maxHeight = data.maxHeight || 1000;     
     data.constrain100 = data.constrain100 === undefined ? true : data.constrain100;
     data.link = data.link;
     self.opts.formData = data;

     self.urlChange = function() {
         var url = self.src.value;
         if (url.indexOf('://') > -1) {
             self.selectImageCallback(url);
         }
     };

     self.selectImageCallback = function(url) {
         self.update({tab: 'formatting', imageUrl: url});
     }

     self.showTab = function(tabName) {
         self.tab = tabName;
     }

     self.advancedVisible = false;
     self.toggleAdvancedOptions = function() {
         self.update({advancedVisible: !self.advancedVisible});
     }

     self.buildData = function() {
         var newData = self.getFormData();
         newData.src = self.imageUrl;
         var isBlock = (newData.title || newData.caption || newData.alignment !== 'inline') ? true : false;
         var html = buildHtml(newData, isBlock);
         var widgetData = {
             isBlock: isBlock,
             label: 'Image',
             previewHtml: '<img src="' + newData.src + '">',
             data: newData,
             html: html
         };
         return widgetData;
     };

     function buildHtml(data, isBlock) {
         var $wrap;
         if (isBlock) { 
             $wrap = $('<div></div>')
         } else {
             $wrap = $('<span></span>')
         }
         $wrap.addClass('st-image-wrapper');
         var $img = $('<img>');
         $wrap.append($img);         
         $img.attr('src', data.src).attr('alt', data.altText).attr('title', data.altText);

         if (data.alignment === 'left') {
             $wrap.css({'float': 'left'});
         }

         if (data.alignment === 'right') {
             $wrap.css({'float': 'right'});
         }


         if (data.alignment === 'center') {
             $wrap.css({'display': 'block', 'text-align': 'center'});
         }

         if (data.alignment == 'inline') {
             $wrap.css({'display': 'inline-block'});
         }
         
         
         if (data.constrain100) {
             $img.css({'max-width': '100%'})
         }
         if (data.maxWidth) {
             $wrap.css({'max-width': data.maxWidth + 'px'});
         }
         if (data.maxHeight) {
             $img.css({'max-height': data.maxHeight + 'px'});
         }
         if (data.minWidth) {
             $img.css({'min-width': data.minWidth + 'px'});
         }
         if (data.minHeight) {
             $img.css({'min-height': data.minHeight + 'px'});
         }
         
         $img.css({'border-width': data.borderWidth + 'px', 'border-style': 'solid', 'border-color': data.borderColor});
         $img.css({'margin-left': data.marginLeft + 'px', 'margin-top': data.marginTop + 'px', 'margin-right': data.marginRight + 'px', 'margin-bottom': data.marginBottom + 'px'});
         
         if (data.title) {
             var $title = $('<h5 class="st-image-title"></h5>').html(data.title);
             $wrap.prepend($title);
         }

         if (data.caption) {
             var $caption = $('<div class="st-image-caption"></div>').html(data.caption);
             $wrap.append($caption);
         }

         if (data.caption || data.title) {
             $wrap.css({'display': 'block'});
         }



         if (data.linkUrl) {
             var $a = $('<a></a>');
             $a.attr('href', data.linkUrl);
             $img.wrap($a);
         }
         
         return $wrap.get(0).outerHTML;
     };

     if (self.markdown) {
         self.markdown = parseOutWidgetHtmlFromContent(markdown);
     }

     
    </script>
</image-widget-configure>



<image-uploader>
    <div>
        <h3>Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="image-dropzone dropzone"
              id="my-image-dropzone">
            
        </form>        
    </div>
    <script>
     var self = this;

     self.on('mount', function() {
         self.dropzone = new Dropzone(".image-dropzone", {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker.",
             uploadMultiple: false,
//             parallelUploads: true,
             maxFiles: 1,
             acceptedFiles: 'image/*,.jpg,.png,.svg,.gif',
             init: function() {
                 this.on("success", function(file, response) { 
                     //var o = JSON.parse(response);
                     self.parent.selectImageCallback(response.fullUrl);
                     this.removeFile(file);
                 });
             },
         });
     });
   
    </script>

</image-uploader>


<image-library>
    <div>
        <h3>File Library</h3>
        <h3 if={loading}>Loading &hellip;</h3>
        <h3 if={!loading && !items.length}>No posts yet</h3>
        <table if={!loading && items.length} class="pure-table comments-table">
            <thead>
                <tr>
                    <th></th>
                    <th></th>                    
                    <th>Name</th>
                    <th></th>                    
                    <th>Uploaded</th>     
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr each={item in items}>
                    <td>
                        <a class="btn btn-primary" href="javascript:;" onclick={selectImage.bind(this, item)}>Choose</a>
                    </td>
                    <td>
                        <img src="{item.fullUrl}" style="max-width: 100px; max-height: 100px;">
                    </td>
                    <td>
                        {item.name}
                    </td>
                    <td>
                        {item.extension}
                    </td>
                    <td>
                        {moment(item.uploadedAt * 1000).fromNow()}
                    </td>
                    <td>
                        <a href="{item.fullUrl}" target="_blank">open</a>
                    </td>

                </tr>
            </tbody>
        </table>
    </div>
    <script>
     var self = this;
     self.loading = true;
     self.items = null;
     self.pager = null;
     self.page = 1;
     self.withDeleted = false;

     self.selectImage = function(item) {
         self.parent.selectImageCallback(item.fullUrl);
     };

     smartFormatDate = function(date) {
         var m = moment(date);
         return m.fromNow();
     }
     
     
     this.fetchData = function() {
         stallion.request({
             url: '/st-publisher/images?page=' + self.page + '&deleted=' + self.withDeleted,
             success: function (o) {
                 self.pager = o.pager;
                 self.items = o.pager.items;
                 self.loading = false;
                 self.update();
             },
             error: function(o, form, xhr) {
                 console.log('error loading dashboard', o, xhr);
             }
         });

     };
     
     this.on('mount', function(){
         self.fetchData();
     });     

    </script>
    
</image-library>
