

<template>
    <div class="widget-modal">
        <modal-base v-ref:themodal :shown.sync="shown" :large="true" :title="title" >
            <div slot="body">
                <div v-if="!widgetType">
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
                        <a href="javascript:;" @click="selectWidget('image-simple', 'Choose Image to Insert')">
                            <span class="icon icon-image2"></span> Sourced Image 
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
                <div v-if="widgetType">
                    <embed-widget-configure v-ref:active v-if="widgetType==='embed'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert"></embed-widget-configure>
                    <image-widget-configure v-ref:active v-if="widgetType==='image'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" ></image-widget-configure>
                    <image-simple-configure v-ref:active v-if="widgetType==='image-simple'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" ></image-widget-configure>
                    <image-collection-widget-configure v-ref:active v-if="widgetType==='image-collection'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgetType==='image-collection'"></image-collection-widget-configure>
                    <html-widget-configure v-ref:active v-if="widgetType==='html'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgetType==='html'"></html-widget-configure>
                    <html-form-widget-configure v-ref:active v-if="widgetType==='html-form'" :widget-data="activeWidgetData" :insert-callback="doInsertCallback" :ok-to-insert.sync="okToInsert" v-if="widgetType==='html-form'"></html-form-widget-configure>
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
         widgetType: '',
         widgetData: null
     },
     data: function() {
         var activeWidgetData = {data:{type: this.widgetType}};
         if (this.widgetData && this.widgetData.type) {
             activeWidgetData = JSON.parse(JSON.stringify(this.widgetData));
             this.widgetType = activeWidgetData.type;
         }
         return {
             title: "Insert Widget",
             activeWidgetData: activeWidgetData,
             okToInsert: false
         }
     },
     created: function() {

     },
     methods: {
         doInsertCallback: function() {

         },
         cancel: function() {
             this.$refs.themodal.close();
         },
         insertWidget: function() {
             var previousGuid = this.widgetData.guid || '';
             var widgetData = this.$refs['active'].getWidgetData();
             if (previousGuid) {
                 widgetData.guid = previousGuid;
             } else {
                 widgetData.guid = stPublisher.generateUUID();
             }
             this.callback(widgetData);
             this.$refs.themodal.close();
         },
         selectWidget: function(type, title) {
             if (this.widgetData && this.widgetData.type) {
                 this.activeWidgetData = this.widgetData;
             } else {
                 this.activeWidgetData = {
                     type: type + '',
                     data: {}
                 }
             }
             this.widgetType = type;
             this.title = title || this.title;
         }
     }
     
     
 }

</script>
