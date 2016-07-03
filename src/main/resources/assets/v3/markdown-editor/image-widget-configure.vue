

<template>
    <div class="image-widget-configure">
        <image-selector v-if="tab==='selector'" :callback="selectImageCallback"></image-selector>
        <div v-if="tab==='formatting'">
            <div style="border-bottom: 1px solid #F4F4F4; padding-bottom: 1em; margin-bottom: 1em;">
                <img v-bind:src="image.thumbUrl"" style="max-width: 50px; max-width: 50px; display:inline-block; margin-right: 20px; border: 1px solid #CCC;">
                <a target="_blank" v-bind:href="image.url">{{image.url}}</a>
                <a class="btn btn-default btn-sm" href="javascript:;" @click="tab='selector'">Change Image</a>
            </div>
            <image-full-formatting v-ref:formatting name="formatting" :widget-data="widgetData"></image-full-formatting>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         widgetData: Object,
         insertCallback: Function,
         okToInsert: {
             twoWay: true
         }
     },
     data: function() {
         return {
             tab: 'selector',
             image: null
         }
     },
     methods: {
         getWidgetData: function() {
             var wd = this.widgetData;
             wd.label = 'Image';
             wd.data = this.$refs.formatting.getData();
             wd.data.image = this.image;
             wd.data.isBlock = true;
             wd.previewHtml = '<img src="' + this.image.thumbUrl + '">'
             //wd.data.embedLink = this.embedLink;
             //wd.data.embedCode = this.embedCode;
             wd.html = this.buildHtml(wd.data.image, wd.data);
             return wd;
         },
         selectImageCallback: function(image) {
             this.image = image;
             this.tab = 'formatting';
             this.okToInsert = true;
         },
         buildHtml: function(image, data) {
             var $wrap = $('<div></div>')
             $wrap.addClass('st-image-wrapper');
             $imgOuter = $('<div></div>').addClass('image-outer');
             var $img = $('<img>');
             $imgOuter.append($img);
             $wrap.append($imgOuter);
             $img.attr('src', data.src).attr('alt', data.altText).attr('title', data.altText);
             
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
             
         }
     }
 }

</script>
