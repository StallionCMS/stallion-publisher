

<template>
    <div class="html-widget-configure">
        <div class="form-group">
            <label>Enter any custom HTML you wish</label>
            <autogrow-textarea class="form-control" name="html" style="min-height: 360px;" v-model="html"></autogrow-textarea>
        </div>
    </div>
</template>

<script>
 
 module.exports = {
     props: {
         insertCallback: Function,
         okToInsert: {
             twoWay: true
         }
     },
     data: function() {
         return {
             html: ''
         }
     },
     ready: function() {
         this.okToInsert = true;
     },
     methods: {
         getWidgetData: function() {
             var self = this;
             var data = {};
             var html = this.html;
             var widgetData = {
                 isBlock: true,
                 type: 'html',
                 label: 'HTML',
                 previewHtml: '',
                 data: data,
                 html: this.html
             };
             return widgetData;
         }
     }
 }

</script>
