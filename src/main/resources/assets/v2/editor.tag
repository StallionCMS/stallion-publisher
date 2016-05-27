

<edit-post>
    <div if={loading}>
        <loading-div></loading-div>
    </div>
    <div if={!loading}>
        <h3 if={!postId}>New Post</h3>
        <h3 if={postId}>Edit Post</h3>
        <div>
            <label>Post title</label>
            <input name="title" type="text" value={post.title} class="form-control">
        </div>
        <div class="row">
            <div class="col-md-6">
                <label>Post body</label>
                <textarea name="originalContent" class="form-control">{post.originalContent}</textarea>
            </div>
            <div class="col-md-6" >
                <div id="live-preview-column">
                    <label>Live preview</label>
                    <div class="preview-dirty-overlay">Blog post being edited.<br>Waiting to refresh preview.</div>
                    <iframe class="preview-iframe" style="width: 100%; height: 100%; min-height: 600px; " name="previewIframe"></iframe>
                </div>
            </div>
        </div><!-- end pure-g -->
    </div>
    <script>
     var self = this;
     self.postId = self.opts.postId;
     self.post = {
         title: 'A new blog post, click to edit this title',
         originalContent: 'This is the post body. It supports **Markdown**.',
         widgets: []
     };
     self.dirty = false;
     self.lastContent = self.post.originalContent;
     self.lastTitle = self.post.title;
     self.lastWidgetCount = 0;
     self.widgetDatas = [];
     self.lineWidgets = [];
     self.lineWidgetByGuid = {};
     

     function loadAllLineWidgetsFromPost() {
         console.log('load all line widgets for post');
         self.lineWidgets.forEach(function(lineWidget) {
             lineWidget.clear();
         });
         self.lineWidgets = [];
         self.post.widgets.forEach(function(widgetData) {
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
             var thisLine = cm.getRange({line: line, ch:0}, {line: line+1, ch: 0}).trim('\n');
             console.log('line is ', thisLine);
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
                 insertLeft: insertLeft,
                 widgetGuid: widgetData.guid,
                 //handleMouseEvents: true,
                 readOnly: true
             });
         marker.widgetGuid = widgetData.guid;
         cm.execCommand('goLineDown');
         marker.deleted = false;
         self.lineWidgets.push(marker);
         self.lineWidgetByGuid[widgetData.guid] = marker;
         $node.find('.line-widget-delete').click(function() {
             cm.doc.removeLineClass(line, 'wrap', 'editor-line-with-widget');
             var newWidgets = [];
             self.post.widgets.forEach(function(existing) {
                 if (existing.guid && existing.guid !== widgetData.guid) {
                     newWidgets.push(existing);
                 }
             });
             self.post.widgets = newWidgets;
             marker.deleted = true;
             marker.clear();
             widgetData.deleted = true;
             reloadPreview();
         });
         $node.find('.line-widget-edit').click(function() {
             showRiotModal({
                 mountOpts: {
                     widgetData: widgetData,
                     callback: function(widgetData) {
                         self.post.widgets.forEach(function(existing) {
                             if (existing.guid === widgetData.guid) {
                                 existing.data = widgetData.data;
                                 existing.html = widgetData.html;
                                 existing.isBlock = widgetData.isBlock;
                                 return false;
                             }
                         });
                         reloadPreview(true);
                     }
                 }
             });         
         });
         return line;
         
     }
    
     
     
     function insertWidget() {
         //var $node = $("<button>BOOKMARK</button>");
         //self.simplemde.codemirror.setBookmark({line: line, ch: 0}, {widget: $node.get(0)});
         //return;
         
         showRiotModal({
             mountOpts: {
                 callback: function(widgetData) {
                     var line = addLineWidget(widgetData, true);
                     self.post.widgets.push(widgetData);
                     widgetData.line = line;                     
                     reloadPreview();
                 }
             }
         });         
         return;
         
     }
     
     if (!self.postId) {
         self.loading = false;
     } else {
         self.loading = true;
     }

     function onEditorChange () {
         if (self.dirty === false && !self.loading) {
             showPreviewDirty();             
         };
         debouncedReload();

     };

     function parseOutWidgetHtmlFromContent(content) {
         return content.replace(/<rawHtml><!\-\-widget:[\w\-]*\-\->[\s\S]*?<!\-\-end-widget:[\w\-]*\-\-><\/rawHtml>/g, '');
     }


     function getContentWithWidgetHtml() {
         var content = self.simplemde.value();
         var lines = content.split('\n');
         var widgetByLine = {};
         self.post.widgets.forEach(function(widgetData) {
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
                     html = "  \n" + html + "";
                 }
                 line += html;
             });
             newLines.push(line);
         };
         var content = newLines.join('\n');
         return content;
     };

     function reloadPreview(force) {
         var title = self.title.value;
         var originalContent = self.simplemde.value();
         if (!force && title === self.post.title && originalContent === self.post.originalContent && self.post.widgets.length === self.lastWidgetCount) {
             previewNotDirty();
             console.log('nothing changed, no reload');
             return;
         }
         console.log('save draft, reload the preview!');
         self.lastWidgetCount = self.post.widgets.length;
         syncWidgetInformationWithCodeMirror();
         var content = getContentWithWidgetHtml();
         stallion.request({
             url: '/st-publisher/posts/' + self.postId + '/update-draft',
             method: 'POST',
             data: {
                 title: self.title.value,
                 originalContent: content,
                 widgets: self.post.widgets
             },
             success: function(post) {
                 self.previewIframe.contentWindow.location.reload();
                 previewNotDirty();
             }
         });
     };

     /**
      * Removes all widgets that have been deleted in the editor from the self.post object.
      * Syncs the line numbers for each widget in self.post.widgets with the actual line number in the editor
     */
     function syncWidgetInformationWithCodeMirror() {
         var cm = self.simplemde.codemirror;
         var newWidgets = []
         var widgetByGuid = {};
         self.post.widgets.forEach(function(widget) {
             widgetByGuid[widget.guid] = widget;
         });
         cm.getAllMarks().forEach(function(mark) {
             var widget = widgetByGuid[mark.widgetGuid];
             console.log('mark widget guid ', mark.widgetGuid, ' widget data', widget, 'mark ', mark);
             if (!widget) {
                 return;
             }
             if (!mark.lines) {
                 return;
             }
             widget.line = cm.doc.getLineNumber(mark.lines[0]);
             newWidgets.push(widget);
         });
         self.post.widgets = newWidgets;
     }


     
     var lastLine = 0;
     function cursorMoves() {
         console.log('cursor moves');
         var cm = self.simplemde.codemirror;
         var cursor = cm.doc.getCursor();
         var lineInfo = cm.lineInfo(cursor.line);
         console.log(cursor.line, cm.doc.lineCount(), lineInfo, lineInfo.widgets);
         var marks = cm.doc.findMarks({line: cursor.line, ch: 0}, {line: cursor.line, ch: 1000});
         console.log('marks ', marks);
         if (marks && marks.length) {
             if (cursor.line + 1 === cm.doc.lineCount()) {
                 cm.replaceRange("\n", {line:cursor.line+1, ch:999999});
             } else if (cursor.line === 0) {
                 cm.replaceRange("\n", {line:0, ch:0});
             }
             if (cursor.line > lastLine) {
                 //cm.doc.setCursor({line: cursor.line + 1, ch: cursor.ch});
                 console.log('move cursor down');                 
                 cm.execCommand('goLineDown');
                 lastLine = cursor.line + 1;
             } else {
                 var newLine = cursor.line -1;
                 console.log('newLien ', newLine);
                 cm.execCommand('goLineUp');
                 lastLine = cursor.line - 1;
             }
         } else {
             lastLine = cursor.line;
         }
     };

     function beforeChange(cm, change) {
         var marks = cm.doc.findMarks({line: change.from.line, ch: 0}, {line: change.to.line, ch: 999999});
         //console.log('beforeChange, marks ', marks, change.from.line, change.to.line);
         if (marks && marks.length > 0) {
             change.cancel();
         }
     };

     this.on('mount', function(){
         self.editorToolbarTop = 140;
         self.editorToolbarWidth = 500;
         self.previewTop = 140;
         self.previewWidth = 500;
         self.simplemde = new SimpleMDE({toolbar: makeToolbar(), element: self.originalContent });
         self.simplemde.codemirror.on('beforeChange', beforeChange);                  
         self.simplemde.codemirror.on('cursorActivity', cursorMoves);
         setTimeout(function() {
             self.editorToolbarTop = $('.editor-toolbar').offset().top;
             self.editorToolbarWidth = $('.editor-toolbar').width() + 22;
             self.previewTop = $('#live-preview-column').offset().top;
             self.previewWidth = $('#live-preview-column').width();
             var viewPortHeight = $(window).height();
             console.log('viewportHeight ', $(window).height());
             var newHeight = $(window).height() - self.previewTop - 30;
             console.log('new frame height ', newHeight);
             $('.preview-iframe').css({'height': newHeight + 'px'});
         }, 100);
         $(window).scroll(function() {
             if ($(window).scrollTop() > self.editorToolbarTop) {
                 $('.editor-toolbar').css({'background-color': 'white', 'opacity': 1, 'border-bottom': '1px solid #999', 'position': 'fixed', 'z-index': 1000, 'top': '0px', 'width': self.editorToolbarWidth + 'px'});
             } else {
                 $('.editor-toolbar').css({'position': 'static', 'width': '100%'});
             }
             if ($(window).scrollTop() > self.previewTop) {
                 $('#live-preview-column').css({'position': 'fixed', 'width': self.previewWidth + 'px', 'top': '0px'});
             } else {
                 $('#live-preview-column').css({'position': 'static', 'width': '100%'});
             }
             console.log('scrolling');
         });
         
         if (!self.postId) {
             this.simplemde.value(self.post.originalContent);
             return;
         }

         $(self.title).change(function() {
             reloadPreview();
         });

         $(self.title).keypress(function() {
             onEditorChange();
         });

         
         self.simplemde.value('Loading...');
         
         stallion.request({
             url: '/st-publisher/posts/' + self.postId,
             success: function (o) {
                 self.post = o;
                 self.loading = false;
                 self.update();
                 self.post.originalContent = parseOutWidgetHtmlFromContent(self.post.originalContent);
                 self.simplemde.value(self.post.originalContent);
                 self.lastWidgetCount = self.post.widgets.length;
                 self.previewIframe.src = self.post.slug + "?stPreview=yes";
                 previewNotDirty();
                 loadAllLineWidgetsFromPost();
             }
         });
         self.simplemde.codemirror.on("change", onEditorChange);         
     });


     // Returns a function, that, as long as it continues to be invoked, will not
     // be triggered. The function will be called after it stops being called for
     // N milliseconds. If `immediate` is passed, trigger the function on the
     // leading edge, instead of the trailing.
     function debounce(func, wait, immediate) {
	 var timeout;
	 return function() {
	     var context = this, args = arguments;
	     var later = function() {
		 timeout = null;
		 if (!immediate) func.apply(context, args);
	     };
	     var callNow = immediate && !timeout;
	     clearTimeout(timeout);
	     timeout = setTimeout(later, wait);
	     if (callNow) func.apply(context, args);
	 };
     };

     var showPreviewDirty = function() {
         $(self.previewIframe).addClass("dirty");
         self.dirty = true;
         $('.preview-dirty-overlay').css({'display': 'block'});
     }

     var previewNotDirty = function() {
         $(self.previewIframe).removeClass("dirty");
         self.dirty = false;
         $('.preview-dirty-overlay').hide();
     };


     var debouncedReload = debounce(reloadPreview, 1000, false);


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
                 className: "fa fa-cube",
                 title: "Insert Widget",
             },
             "|", // Separator
         ];
         return toolbar;
     };

    </script>
</edit-post>



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
        </div>
        <div class="modal-footer">
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    </div>
    <div if={widgetType}>
        <div class="modal-header"><h4>Configure widget</h4></div>
        <div class="modal-body">
            <embed-widget-configure name="embedWidget" if={widgetType == 'embed'}></embed-widget-configure>
            <image-widget-configure name="imageWidget" if={widgetType == 'image'}></embed-widget-configure>
        </div>
        <div class="modal-footer">
            <button class="btn btn-primary" style="float: left;" onclick={saveWidget}>Update</button>
            <a href="javascript:;" onclick={close}>Cancel</a>
        </div>
    <script>
     var self = this;
     self.widgetType = '';

     self.selectWidget = function(widgetType) {
         var widgetTag = self.tags[widgetType + 'Widget'];
         var widgetData = self.widgetData;
         if (!widgetData || !Object.keys(widgetData).length) {
             widgetData = {data: {}, type: widgetType, label: '', html: ''};
         }
         widgetTag.widgetData = widgetData;
         self.update({widgetType: widgetType});
     };

     self.saveWidget = function() {
         var widgetTag = self.tags[self.widgetType + 'Widget'];
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
             console.log('no saveChanges function on widget ', widgetTag);
         }
     };
     
     self.close = function() {
         self.unmount();
         self.opts.parentElement.modal('hide');
     };

     if (self.opts.widgetData) {
         self.widgetData = JSON.parse(JSON.stringify(self.opts.widgetData));
         self.selectWidget(self.widgetData.type);
     };
     
    </script>
</widget-modal>

<embed-widget-configure>
    <div class="form-group">
        <label>The HTML Embed code</label>
        <textarea class="form-control" autofocus="autofocus" name="embedCode" value={widgetData.data.embedCode}></textarea>
    </div>
    <script>
     var self = this;
     self.on('mount', function() {

     });
     self.buildData = function() {
         var widgetData = {
             label: 'Embed Code',
             data: {
                 embedCode: self.embedCode.value
             },
             html: self.embedCode.value
         };
         return widgetData;
     };
    </script>
</embed-widget-configure>

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
        <div class="form-group">
            <label>Insert the URL of the image here:</label>
            <input type="text" class="form-control" name="src" >
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
                    <label for="alignInline">Inline <input type="radio"  name="alignment" value="inline"></label>
                    <label for="alignLeft">Left <input type="radio"  name="alignment" value="left"></label>
                    <label for="alignRight">Right <input type="radio" name="alignment" value="right"></label>
                    <label for="alignCenter">Center <input type="radio" name="alignment" value="center"></label>            
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

     self.selectImageCallback = function(url) {
         self.update({tab: 'formatting', imageUrl: url});
     }

     self.showTab = function(tabName) {
         self.tab = tabName;
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

     
    </script>
</image-widget-configure>

<image-uploader>
    <div>
        <h3>Upload file</h3>
        <form action="/st-publisher/upload-file"
              class="dropzone"
              id="my-awesome-dropzone">
            
        </form>        
    </div>
    <script>
     var self = this;

     self.on('mount', function() {
         self.dropzone = new Dropzone(".dropzone", {
             dictDefaultMessage: "Drag one more more files here. Or click to open a file picker."
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
