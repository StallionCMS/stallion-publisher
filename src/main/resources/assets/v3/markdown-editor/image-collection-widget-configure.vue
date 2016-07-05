

<template>
    <div class="image-collection-widget-configure">
        <div v-show='screen==="selector"'>
            <div style="margin-bottom: 1em;">
                <a href="javascript:;" @click="screen='collection'">&#171; Back to collection</a>
            </div>
            <image-selector :hideUrlTab="true" :callback="imageSelectCallback"></image-selector>
        </div>
        <div v-show='screen ==="collection"'>
            <div class="row p">
                <div class="col-sm-6 small-image-upload-target-column"> 
                    <image-uploader :callback="imageSelectCallback"></image-uploader>
                </div>
                <div class="col-sm-6 add-image-button-column">
                    <div>
                        or &nbsp; <button @click='screen="selector"' class="btn btn-default btn-md">Add image from your library &#187;</button>
                    </div>
                </div>
            </div>
            <p v-if="!images.length">
                <em><big>No images yet.</big></em>
            </p>
            <div class="included-images-list">
                <div v-for='img in images' class="image-thumb-box item">
                    <a class="delete-icon" href="javascript:;" @click="deleteImage(i)">&#215;</a>
                    <div class="image-wrap-box">
                        <img :src="img.src" :title="img.caption" >
                    </div>
                    <div class="caption-wrap-box">
                        <div style="height: 90px;" class="">
                            <a v-if="!img.caption && !img.showAddCaption" @click='img.showAddCaption=true' href="javascript:;">Add caption</a>
                            <textarea v-if='img.caption || img.showAddCaption'  type="text" placeholder="Caption (optional)" class="form-control" @change='updateCaption(img)' v-model="img.caption" ></textarea>
                            
                            <a v-if='!img.link && !img.showAddLink' @click='img.showAddLink = true' href="javascript:;">Add link</a>
                            <input v-if='img.link || img.showAddLink' type="text" placeholder="https://somesite.com" v-model="img.link" class="form-control">
                        </div>
                    </div>
                </div>
            </div>
            <p>
            </p>
            <hr>
            <div class="form-group">
                <label>How should the images be laid out?</label>
                <select name="layout" class="form-control" v-model="layout">
                    <option value="image-grid-medium">Grid with medium images</option>
                    <option value="image-grid-small">Grid with small images</option>
                    <option value="image-columns">Two Columns Responsive (One column on small screens)</option>
                    <option value="image-one-column">One Column</option>
                    <option value="image-slider">Slider</option>
                    <option value="custom">Use custom CSS styling</option>
                </select>
            </div>
            <div v-if="layout==='custom'" class="form-group">
                <label>Custom CSS class(es)</label>
                <input type="input" class="form-control" name="customCssClass" placeholder="Enter one or more CSS class names, separated by spaces." v-model="galleryOnClick" v-bind:true-value="true" v-bind:false-value="false"/>
            </div>
            <div class="checkbox p">
                <label><input type="checkbox" name="galleryOnClick" v-bind:true-value="true" v-bind:false-value="false" v-model="galleryOnClick"> Open slideshow mode when an image is clicked on?</label>
            </div>
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
         var data = JSON.parse(JSON.stringify(this.widgetData.data || {}));
         return {
             screen: 'collection',
             images: data.images  || [],
             layout: data.layout || 'image-grid-medium',
             galleryOnClick: data.galleryOnClick === undefined ? true : false
         }
     },
     ready: function() {
         if (this.images) {
             this.okToInsert = true;
         }
     },
     attached: function() {
         var self = this;
         var listEle = $(this.$el).find('.included-images-list').get(0);
         Sortable.create(listEle, {
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
         
         
     },
     methods: {
         imageSelectCallback: function(imageInfo) {
             var self = this;
             var img = {
                 src: imageInfo.mediumUrl,
                 height: imageInfo.mediumHeight || imageInfo.height,
                 width: imageInfo.mediumWidth || imageInfo.width,
                 originalSrc: imageInfo.url,
                 originalHeight: imageInfo.height,
                 originalWidth: imageInfo.width
             }
             self.images.push(img);
             self.screen = 'collection';
             self.okToInsert = true;
         },
         deleteImage: function(index) {

         },
         getWidgetData: function() {
             var self = this;
             var data = {
                 layout: self.layout,
                 galleryOnClick: self.galleryOnClick,
                 images: self.images
             }
             var html = self.buildHtml(data);
             var widgetData = {
                 isBlock: true,
                 type: 'image-collection',
                 label: 'Image Collection',
                 previewHtml: '<img src="' + self.images[0].src + '">',
                 data: data,
                 html: html
             };
             return widgetData;
         },
         buildHtml: function(data) {
             var self = this;
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
     }
 }

</script>
