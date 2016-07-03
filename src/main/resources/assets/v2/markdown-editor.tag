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
             autoDownloadFontAwesome: false,
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

     function insertWidget(type) {
         var widgetData = null;
         if (type) {
             widgetData = {type: type};
         }
         showRiotModal({
             backdrop: 'static',
             mountOpts: {
                 widgetData: widgetData,
                 callback: function(widgetData) {
                     var line = addLineWidget(widgetData, true);
                     self.widgets.push(widgetData);
                     widgetData.line = line;
                     triggerChange();
                 }
             }
         });         
     }

     function insertImage(editor) {
         insertWidget('image');
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
                 backdrop: 'static',
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
             change.cancel();
             if (change.origin === '+input' && change.text.length == 2 && change.text[0] === '' && change.text[1] === '') {
                 if (change.to.line === 0) {
                     insertLine(cm, 0);
                 } else {
                     insertLine(cm, change.to.line + 1);
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

     function pasteRichContent() {
         showRiotModal({
             riotTag: 'paste-html-modal',
             mountOpts: {
                 callback: function(content) {
                     var cursor = self.simplemde.codemirror.doc.getCursor();
                     console.log('insert content ' , content,  ' at ', cursor);
                     cm.replaceRange(content, {line: cursor.line, ch: cursor.ch});
                 }
             }
         });
     }


     function _replaceSelection(cm, active, startEnd, url) {
	 if(/editor-preview-active/.test(cm.getWrapperElement().lastChild.className))
	     return;
         
	 var text;
	 var start = startEnd[0];
	 var end = startEnd[1];
	 var startPoint = cm.getCursor("start");
	 var endPoint = cm.getCursor("end");
	 if(url) {
	     end = end.replace("#url#", url);
	 }
	 if(active) {
	     text = cm.getLine(startPoint.line);
	     start = text.slice(0, startPoint.ch);
	     end = text.slice(startPoint.ch);
	     cm.replaceRange(start + end, {
		 line: startPoint.line,
		 ch: 0
	     });
	 } else {
	     text = cm.getSelection();
	     cm.replaceSelection(start + text + end);

	     startPoint.ch += start.length;
	     if(startPoint !== endPoint) {
		 endPoint.ch += start.length;
	     }
	 }
	 cm.setSelection(startPoint, endPoint);
	 cm.focus();
     }
     
     function insertLink() {
         
         showRiotModal({
             riotTag: 'insert-link-modal',
             mountOpts: {
                 callback: function(url) {
                     console.log('insert link ', url);
	             var cm = self.simplemde.codemirror;
	             var stat = self.simplemde.getState();
	             var options = self.simplemde.options;
                     console.log('stat.link ', stat.link, 'options.insertTexts ', options.insertTexts.link);
                     var startEnd = ["[", "](" + url + ")"]
	             _replaceSelection(cm, stat.link, startEnd, '');
                     // TODO refocus on the editor
                 }
             }
         });
     }

     
     
     /*****************************/
     /* Editor Helpers            */

     function makeToolbar() {
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
		 action: insertLink,
		 className: "icon icon-link",
		 title: "Create Link",
		 default: true
	     },
	     {
		 name: "image",
		 action: insertImage,
		 className: "icon icon-image2",
		 title: "Insert Image",
		 default: true
	     },
	     {
		 name: "pasteRich",
		 action: pasteRichContent,
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
                    <span class="icon icon-embed"></span> HTML Embed (Youtube Video, Slideshare, etc.)
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'image')}>
                    <span class="icon icon-image2"></span> Image
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'image-collection')}>
                    <span class="icon icon-images3"></span> Image Collection/Gallery
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'html-form')}>
                    <span class="icon icon-insert-template"></span> HTML Form
                </a>
            </div>
            <div class="widget-option">
                <a href="javascript:;" onclick={selectWidget.bind(this, 'html')}>
                    <span class="icon icon-file-xml"></span> HTML Code
                </a>
            </div>
        </div>
        <div class="modal-footer">
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <div if={widgetType}>
        <div class="modal-header"><h4>{header}</h4></div>
        <div class="modal-body" id="widgetConfigureTarget"">
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget} disabled={updateDisabled}>Update</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     self.widgetType = '';
     self.activeTag = null;
     self.header = 'Configure widget'
     self.updateDisabled = true;

     self.selectWidget = function(widgetType, widgetData) {
         var widgetTag = self.tags[widgetType + 'Widget'];
         if (!widgetData || !Object.keys(widgetData).length) {
             widgetData = {data: {}, type: widgetType, label: '', html: ''};
         }
         self.update({widgetType: widgetType});
         self.activeTag = appendRiotTag(self.widgetConfigureTarget, widgetType + '-widget-configure', {widgetData: widgetData, parent: self});
         self.activeTag.parent = self;
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
         if (self.widgetData && self.widgetData.type) {
             self.selectWidget(self.widgetData.type, self.widgetData);
         }
     });

     if (self.opts.widgetData) {
         self.widgetData = JSON.parse(JSON.stringify(self.opts.widgetData));
     };
     
    </script>
</widget-modal>

<paste-html-modal>
    <div>
        <div class="modal-header"><h4>Paste Rich Content</h4></div>
        <div class="modal-body">
            <div class="row">
                <div class="col-sm-6">
                    <label>Paste rich text, HTML, or from Google Docs or Word into this text box.</label>
                    <div contenteditable="true" onPaste={htmlUpdated} class="paste-target" name="pasteTarget"></div>
                </div>
                <div class="col-sm-6">
                    <label>Preview the resulting markdown here:</label>
                    <textarea class="markdown-preview" class="form-control" name="markdownPreview"></textarea>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Insert</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     
     self.close = function() {
         self.unmount();
         self.opts.parentElement.modal('hide');
     };


     self.htmlUpdated = function(evt) {
         //var html = evt.clipboardData.getData('text/html');
         setTimeout(function() {
             var markdown = toMarkdown($(self.pasteTarget).html());
             markdown = markdown.replace(/<[^>]+>/g, '');
             self.markdownPreview.value = markdown;
         }, 20);
         return true;
     }
     
     self.saveWidget = function() {
         var content = self.markdownPreview.value;
         self.opts.callback(content);
         self.close();
     };
    </script>
</paste-html-modal>

<insert-link-modal>
    <div>
        <div class="modal-header"><h4>Insert Link</h4></div>
        <div class="modal-body">
            <div class="row">
                <div class="col-sm-4">
                    <a href="javascript:;" onclick={switchTab.bind(this, 'external-link')} class={'vertical-tab': true, 'active-tab': tab==="external-link"}>
                        External Link
                    </a>
                    <a href="javascript:;" onclick={switchTab.bind(this, 'internal-link')} class={'vertical-tab': true, 'active-tab': tab==="internal-link"}>
                        Internal Page
                    </a>
                    <a href="javascript:;" onclick={switchTab.bind(this, 'document')} class={'vertical-tab': true, 'active-tab': tab==="document"}>
                        Document or File
                    </a>

                </div>
                <div class="col-sm-8 tab-box">
                    <div show={tab==="external-link"}>
                        <label>Enter the URL of the page here</label>
                        <input type="text" name="link" class="form-control" placeholder="https://...">            
                    </div>
                    <div show={tab==="internal-link"}>
                        <internal-link-picker callback={linkPicked}></internal-link-picker>
                    </div>
                    <div show={tab==="document"}>
                        <file-library ispicker={true} callback={filePicked} ></file-library>
                    </div>
                </div>
            </div>
            
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Insert</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     
     self.tab = 'internal-link';
     self.url = '';

     self.switchTab = function(tab) {
         self.tab = tab;
     }

     self.linkPicked = function(o) {
         self.url = o.url;
     };
     
     self.filePicked = function(file) {
         console.log('filePicked ', file);
         self.url = file.url;
         self.saveWidget();
     }

     self.close = function() {
         self.unmount();
         self.opts.parentElement.modal('hide');
     };
     
     self.saveWidget = function() {
         if (!self.url) {
             self.url = self.link.value;
         }
         self.opts.callback(self.url);
         self.close();
     };

     
    </script>
</insert-link-modal>

<internal-link-picker>
    <div if={!loaded}>
        Loading &hellips;
    </div>
    <div if={loaded}>
        <label>Pick a page or blog post</label>
        <select name="pageSelector" class="form-control"></select>
    </div>
    <script>
     var self = this;
     self.loaded = false;
     self.items = [];

     self.on('updated', function() {
         if (self.loaded) {
             var data = [{id: '', text: 'Pick a page'}];
             self.items.forEach(function(item) {
                 data.push({
                     id: item.permalink,
                     text: item.title
                 });
             });
             console.log('data', data);
             $(self.pageSelector).select2({
                 data: data,
                 placeholder: 'Select an option'
             }).on('select2:select', function (evt) {
                 console.log('selected ', evt, this, self.pageSelector.value);
                 self.opts.callback({url: self.pageSelector.value});
             });
         }
     });


     self.on('mount', function() {
         stallion.request({
             url: '/st-publisher/all-live-contents',
             success: function(o) {
                 self.update({items: o.pager.items, loaded: true});
             }
         });
     });

    </script>
</internal-link-picker>

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

<image-collection-widget-modal>
    <div>
        <div class="modal-header"><h4>Configure Gallery</h4></div>
        <div class="modal-body">
            <image-collection-widget-configure widgetdata={opts.widgetdata} name="imageWidget"></image-collection-widget-configure>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Update</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <script>
     var self = this;
     console.log('image-collection-model ', opts.widgetdata);
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

</image-collection-widget-modal>

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

<html-widget-configure>
    <div>
        <div class="form-group">
            <label>Enter any custom HTML you wish</label>
            <textarea class="form-control" name="html" style="min-height: 360px;"></textarea>
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     var data = self.opts.widgetData && self.opts.widgetData.data ? self.opts.widgetData.data : {};
     data.html = data.html || '';

     self.setFormData(data);

     self.buildData = function() {
         var data = self.getFormData();
         var html = data.html;
         var widgetData = {
             isBlock: true,
             type: 'html',
             label: 'HTML',
             previewHtml: '',
             data: data,
             html: html
         };
         return widgetData;
     };

     self.on('mount', function() {
         if (self.opts.parent) {
             self.opts.parent.update({header: 'Custom HTML Widget', updateDisabled: false});
         }
     });

    </script>


</html-widget-configure>

<html-form-widget-configure>
    <div>
        <div class="form-group">
            <label>The HTML for your form</label>
            <textarea  style="min-height: 360px;" class="form-control" name="formHtml"></textarea>
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     var data = self.opts.widgetData && self.opts.widgetData.data ? self.opts.widgetData.data : {};
     

     var FORM_HTML = "<form id=\"stallion-contact-form\" class=\"pure-form pure-form-stacked st-contacts-form\"><div class=\"st-form-success\" style=\"display:none;\">\n                    Thank you for reaching out, we will get back to you promptly!\n                </div>\n                <label for=\"name\">Your name</label>\n                <input class=\"pure-input-1\" name=\"name\" type=\"text\" placeholder=\"Your name\" required=\"required\">\n\n                <label for=\"email\">Email</label>\n                <input class=\"pure-input-1\" name=\"email\" type=\"email\" placeholder=\"Email\" required=\"required\">\n                <label>Your message</label>\n                <textarea class=\"pure-input-1\" name=\"message\" required=\"required\" style=\"height: 74px;\"></textarea>\n                <p>\n                    <button type=\"submit\" class=\"pure-button pure-button-primary\">Get in touch</button>\n                </p>\n            </fieldset>\n</form>";

     if (!data.formHtml) {
         data.formHtml = FORM_HTML;
     }

     self.setFormData(data);

     self.buildData = function() {
         var data = self.getFormData();
         var html = data.formHtml;
         var widgetData = {
             isBlock: true,
             type: 'html',
             label: 'HTML',
             previewHtml: '<form...>',
             data: data,
             html: html
         };
         return widgetData;
     };

     self.on('mount', function() {
         if (self.opts.parent) {
             self.opts.parent.update({header: 'Configure HTML Form', updateDisabled: false});
         }
     });

    </script>
</html-form-widget-configure>


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
    <div if={screen==="selector"}>
        <div style="margin-bottom: 1em;">
            <a href="javascript:;" onclick={showScreen.bind(this, 'collection')}>&#171; Back to collection</a>
        </div>
        <image-selector hideUrlTab={true} callback={imageSelectCallback}></image-selector>
    </div>
    <div show={screen ==="collection"}>
        <div class="row p">
            <div class="col-sm-6 small-image-upload-target-column"> 
                <image-uploader callback={imageSelectCallback}></image-uploader>
            </div>
            <div class="col-sm-6 add-image-button-column">
                <div>
                    or &nbsp; <button onclick={showScreen.bind(this, 'selector')} class="btn btn-default btn-md">Add image from your library &#187;</button>
                </div>
            </div>
        </div>
        <p if={!images.length}>
            <em><big>No images yet.</big></em>
        </p>
        <div name="included-images-list">
            <div each={img, i in images} class="image-thumb-box item">
                <a class="delete-icon" href="javascript:;" onclick={deleteImage.bind(this, i)}>&#215;</a>
                <div class="image-wrap-box">
                    <img src={img.src} title={img.caption} >
                </div>
                <div class="caption-wrap-box">
                    <div style="height: 90px;" class="">
                        <a if={!img.caption && !img.showAddCaption} onclick={showAddCaption.bind(this, img)} href="javascript:;">Add caption</a>
                        <textarea  if={img.caption || img.showAddCaption}  type="text" placeholder="Caption (optional)" class="form-control" onchange={updateCaption.bind(this, img)} >{img.caption}</textarea>
                        
                        <a if={!img.link && !img.showAddLink} onclick={showAddLink.bind(this, img)} href="javascript:;">Add link</a>
                        <input if={img.link || img.showAddLink} type="text" placeholder="https://somesite.com" onchange={updateLink.bind(this, img)} class="form-control" value={img.link}>
                    </div>
                </div>
            </div>
        </div>
        <p>
            
        </p>
        <hr>
        <div class="form-group">
            <label>How should the images be laid out?</label>
            <select name="layout" class="form-control" onchange={layoutChanged} >
                <option value="image-grid-medium">Grid with medium images</option>
                <option value="image-grid-small">Grid with small images</option>
                <option value="image-columns">Two Columns Responsive (One column on small screens)</option>
                <option value="image-one-column">One Column</option>
                <option value="image-slider">Slider</option>
                <option value="custom">Use custom CSS styling</option>
            </select>
        </div>
        <div if={showCustomCss} class="form-group">
            <label>Custom CSS class(es)</label>
            <input type="input" class="form-control" name="customCssClass" placeholder="Enter one or more CSS class names, separated by spaces."/>
        </div>
        <div class="checkbox p">
            <label><input type="checkbox" name="galleryOnClick" value="true"> Open slideshow mode when an image is clicked on?</label>
        </div>
        <p show={false}>
            <a href="javascript:;" onclick={update.bind(this, {showAdvanced: true})}>Show advanced options &#187;</a>
        </p>
        <div show={false} class="advanced-options st-top-spaced">
            <div class="p">
                <a href="javascript:;" onclick={update.bind(this, {showAdvanced: false})}>&#171; Hide advanced options.</a>
            </div>
            
        </div>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     if (self.opts.widgetdata) {
         self.opts.widgetData = self.opts.widgetdata;
     }
     var data = (self.opts.widgetData && self.opts.widgetData.data) ? self.opts.widgetData.data : {};
     data.layout = data.layout || 'image-grid-medium';
     self.images = data.images || [];
     self.useDefaultStyling = data.useDefaultStyling !== undefined ? data.useDefaultStyling : true;
     self.screen = 'collection';
     self.showCustomCss = data.layout === 'custom';

     self.on('updated', function() {
         Sortable.create(self['included-images-list'], {
             draggable: '.item',
             handle: '.image-wrap-box',
             onEnd: function (evt) {
                 evt.oldIndex;  // element's old index within parent
                 evt.newIndex;  // element's new index within parent
                 var img = self.images[evt.oldIndex];
                 self.images.splice(evt.oldIndex, 1);
                 self.images.splice(evt.newIndex, 0, img);
                 console.log(self.images);
             }
         });
     });

     self.deleteImage = function(index) {
         self.images.splice(index, 1);
         self.update();
     }

     self.showAddCaption = function(img) {
         img.showAddCaption = true;
         self.update();
     }

     self.showAddLink = function(img, evt) {
         img.showAddLink = true;
         self.update();
     }

     self.layoutChanged = function() {
         if (self.layout.value === 'custom') {
             self.showCustomCss = true;
         } else {
             self.showCustomCss = false;
         }
     }

     self.setFormData(data);     


     self.updateCaption = function(img, evt) {
         img.caption = evt.target.value;
     }

     self.updateLink = function(img, evt) {
         img.link = evt.target.value;
     }


     self.showScreen = function(screen) {
         self.update({screen: screen});
     }

     self.imageSelectCallback = function(imageInfo) {
         var img = {
             src: imageInfo.mediumUrl,
             height: imageInfo.mediumHeight || imageInfo.height,
             width: imageInfo.mediumWidth || imageInfo.width,
             originalSrc: imageInfo.url,
             originalHeight: imageInfo.height,
             originalWidth: imageInfo.width
         }
         self.images.push(img);
         self.update({screen: 'collection'});
         if (self.opts.parent) {
             self.opts.parent.update({updateDisabled: false});
         }
     }

     self.removeImage = function() {

     };

     if (self.widgetData && self.widgetData.data && self.widgetData.data) {
         self.images = self.widgetData.data.images || [];
         self.useDefaultStyling = self.widgetData.data.useDefaultStyling !== false;
         self.customCssClass = self.widgetData.data.customCssClass;
         if (self.customCssClass || !self.useDefaultStyling) {
             self.showAdvanced = true;
         }
     }

     function buildHtml(data) {
         var $ul = $('<ul class="st-image-collection"></ul>');
         if (data.layout !== 'custom') {
             $ul.addClass('st-image-collection-default').addClass(data.layout);
         } else {
             $ul.addClass(data.customCssClass);
         }
         if (data.layout === 'image-grid-medium' || data.layout === 'image-grid-small') {
             $ul.addClass('image-grid');
         }

         if (data.galleryOnClick) {
             $ul.addClass('with-slideshow');
         }
         
         self.images.forEach(function(img) {
             var $img = $('<img>');
             $img.attr('src', img.src)
                 .attr('data-height', img.height)
                 .attr('data-width', img.width)
                 .attr('data-original-src', img.originalSrc)
                 .attr('data-original-height', img.originalHeight)
                 .attr('data-original-width', img.originalWidth)
                 ;
             var $li = $('<li></li>').addClass('item');
             if (img.link && !data.galleryOnClick) {
                 var $a = $('<a></a>');
                 $a.attr('href', img.link);
                 $a.append($img);
                 $li.append($a);
             } else {
                 $li.append($img);
             }
             if (img.caption && img.link) {
                 var $caption = $('<div></div>').addClass("image-caption");
                 var $a2 = $('<a></a>');
                 $a2.html(img.caption);
                 $a2.attr('href', img.link);
                 $caption.append($a2);
                 $li.append($caption);
             } else if (img.caption) {
                 var $caption = $('<div></div>').addClass("image-caption").html(img.caption);
                 $li.append($caption);
             }
             $ul.append($li);
         });
         var $div = $('<div></div').addClass('st-image-collection-wrapper').addClass(data.layout);
         $div.append($ul);
         return $div.get(0).outerHTML;
     }

     self.buildData = function() {
         var data = self.getFormData();
         data.images = self.images;
         var html = buildHtml(data);
         var widgetData = {
             isBlock: true,
             type: 'image-collection',
             label: 'Image Collection',
             previewHtml: '<img src="' + self.images[0].src + '">',
             data: data,
             html: html
         };
         return widgetData;
     };

     self.on('mount', function() {
         var disabled = true;
         if (self.images.length > 0) {
             disabled = false;
         }
         if (self.opts.parent) {
             self.opts.parent.update({header: 'Configure Image Collection/Gallery', updateDisabled: disabled});
         }
     });

     
    </script>
</image-collection-widget-configure>




<image-widget-configure>
    <div if={tab==='selector'}>
        <image-selector callback={selectImageCallback}></image-selector>
    </div>

    <div if={tab==='formatting'}>
        <div style="border-bottom: 1px solid #F4F4F4; padding-bottom: 1em; margin-bottom: 1em;">
            <img src="{image.thumbUrl}"" style="max-width: 50px; max-width: 50px; display:inline-block; margin-right: 20px; border: 1px solid #CCC;">
            <a target="_blank" href="{image.url}">{image.thumbUrl}</a>
            <a class="btn btn-default btn-sm" href="javascript;" onclick={showTab.bind(this, 'selector')}>Change Image</a>
        </div>
        <image-full-formatting name="formatting" widgetData={widgetData}></image-full-formatting>
    </div>
    <script>
     var self = this;
     self.mixin('databind');
     self.tab = 'selector';
     self.widgetData = self.opts.widgetData || {};
     self.widgetData.data = self.widgetData.data || {};
     self.image = {};
     if (self.widgetData.data.image) {
         self.image = JSON.parse(JSON.stringify(self.widgetData.data.image));
     }

     if (self.widgetData.data.src) {
         self.tab = 'formatting';
     }

     self.urlChange = function() {
         var url = self.src.value;
         if (url.indexOf('://') > -1) {
             self.selectImageCallback(url);
         }
     };

     self.selectImageCallback = function(image) {
         self.image = image;
         self.update({
             tab: 'formatting', 
             imageUrl: image.mediumUrl, image: image
         });
         if (opts.parent && opts.parent.update) {
             self.opts.parent.update({updateDisabled: false});
         }
     }

     self.showTab = function(tabName) {
         self.tab = tabName;
     }

     self.buildData = function() {
         var newData = self.tags.formatting.getFormData();
         newData.image = self.image;
         console.log('image ', newData.image);
         newData.src = self.image.mediumUrl;
         var isBlock = (newData.title || newData.caption || newData.alignment !== 'inline') ? true : false;
         var html = buildHtml(newData, isBlock);
         var widgetData = {
             isBlock: isBlock,
             label: 'Image',
             previewHtml: '<img src="' + self.image.thumbUrl + '">',
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
         $imgOuter = $('<div></div>').addClass('image-outer');
         var $img = $('<img>');
         $imgOuter.append($img);
         $wrap.append($imgOuter);
         $img.attr('src', data.src).attr('alt', data.altText).attr('title', data.altText);

         /*
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

         */
         $wrap.addClass('st-image-' + data.alignment);
         
         
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
         $wrap.append($("<div></div>").addClass("image-bottom"));
         return $wrap.get(0).outerHTML;
     };

     if (self.markdown) {
         self.markdown = parseOutWidgetHtmlFromContent(markdown);
     }

     
     self.on('mount', function() {
         var disabled = true;
         console.log(self.widgetData);
         if (self.widgetData.data.src) {
             disabled = false;
        }
        if (self.opts.parent) {
            self.opts.parent.update({header: 'Configure Image Widget', updateDisabled: disabled});
        }
     });


    </script>
</image-widget-configure>


<image-full-formatting>
    <div>
        <h3>Formatting</h3>

        <div class="row">
            <div class="col-sm-6">
                <h5>Align</h5>                
                <div class="form-group">
                    <label for="alignCenter">Center <br><input type="radio" name="alignment" value="center"></label>        
                    <label for="alignLeft">Left <br><input type="radio"  name="alignment" value="left"></label>
                    <label for="alignRight">Right <br><input type="radio" name="alignment" value="right"></label>
                    <label for="alignRight">Image Left, Caption Right<br><input type="radio" name="alignment" value="caption-right"></label>
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
     var widgetData = self.opts.widgetdata || {};
     var data = widgetData.data || {};
     console.log('formatting data' , data);
     data.alignment = data.alignment || 'center';
     data.borderColor = data.borderColor || '#777';
     data.borderWidth = data.borderWidth === undefined ? 0 : data.borderWidth;
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


     self.setFormData(data);


     self.advancedVisible = false;
     
     self.toggleAdvancedOptions = function() {
         self.update({advancedVisible: !self.advancedVisible});
     }
     

     
    </script>
</image-full-formatting>



