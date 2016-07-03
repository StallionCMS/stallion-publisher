

<template>
    <div class="widget-modal">
        <modal-base v-ref:themodal :shown.sync="shown" :title="title" >
            <div slot="body">
                <div v-if="!widgettype">
                    <div class="widget-option">
                        <a href="javascript:;" @click="selectWidget('embed', 'Configure HTML Embed')">
                            <span class="icon icon-embed"></span> HTML Embed (Youtube Video, Slideshare, etc.)
                        </a>
                    </div>
                    <div class="widget-option">
                        <a href="javascript:;" @click="selectWidget('image', 'Choose Image to Insert')">
                            <span class="icon icon-image2"></span> Image
                        </a>
                    </div>
                    <div class="widget-option">
                        <a href="javascript:;" @click="selectWidget('image-collection', 'Configure Image Collection')">
                            <span class="icon icon-images3"></span> Image Collection/Gallery
                        </a>
                    </div>
                    <div class="widget-option">
                        <a href="javascript:;" @click="selectWidget('html-form', 'Insert HTML Form')">
                            <span class="icon icon-insert-template"></span> HTML Form
                        </a>
                    </div>
                    <div class="widget-option">
                        <a href="javascript:;" @click="selectWidget('html', 'Insert Custom HTML Code')">
                            <span class="icon icon-file-xml"></span> HTML Code
                        </a>
                    </div>
                </div>
                <div v-if="widgettype">
                    <embed-widget-configure v-ref:active v-if="widgettype==='embed'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert"></embed-widget-configure>
                    <image-widget-configure v-ref:active v-if="widgettype==='image'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgettype==='image'"></image-widget-configure>
                    <image-collection-widget-configure v-ref:active v-if="widgettype==='image-collection'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgettype==='image-collection'"></image-collection-widget-configure>
                    <html-widget-configure v-ref:active v-if="widgettype==='html'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgettype==='html'"></html-widget-configure>
                    <html-form-widget-configure v-ref:active v-if="widgettype==='html-form'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgettype==='html-form'"></html-form-widget-configure>
                </div>
            </div>
            <div slot="footer">
                <button class="btn btn-primary btn-md" :disabled="!okToInsert" @click="insertWidget">Insert Widget</button>
                <a @click="cancel" href="javascript:;">Cancel</a>
            </div>
        </modal-base>
    </div>
</template>

<script>
 module.exports = {
     props: {
         shown: {
             twoWay: true
         },
         callback: Function,
         widgettype: '',
         widgetdata: null,
         okToInsert: false
     },
     data: function() {
         return {
             title: "Insert Widget",
             activeWidgetData: null
         }
     },
     created: function() {
         console.log('insert widget-modal created');
     },
     methods: {
         doInsertCallback: function() {
             console.log('widget save callback');
         },
         cancel: function() {
             console.log('cancel insert widget');
         },
         insertWidget: function() {
             var widgetData = this.$refs['active'].getWidgetData();
             console.log('insert widget', this.$refs['active']);
             this.callback(widgetData);
             this.$refs.themodal.close();
         },
         selectWidget: function(type, title) {
             if (this.widgetdata && this.widgetdata.type) {
                 this.activeWidgetData = this.widgetdata;
             } else {
                 this.activeWidgetData = {
                     type: type + '',
                     data: {}
                 }
             }
             this.widgettype = type;
             this.title = title || this.title;
         }
     }
     
     
 }

</script>
