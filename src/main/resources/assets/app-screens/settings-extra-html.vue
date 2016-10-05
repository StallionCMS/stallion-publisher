<style>
 
</style>



<template>
    <div>
        <loading-div v-if='$loadingRouteData'></loading-div>
        <div v-if="!$loadingRouteData">
            <h3>Extra HTML and CSS</h3>
            <form @submit.prevent='save'>
                <div class="form-group">
                    <label>Head HTML</label>
                    <textarea class="form-control" name="headHtml" v-model="data.headHtml"></textarea>
                </div>
                <div class="form-group">
                    <label>Footer HTML</label>
                    <textarea class="form-control" name="footerHtml" v-model="data.footerHtml"></textarea>
                </div>
                <div class="form-group">
                    <button class="btn btn-primary st-button-submit">Save Changes</button>
                </div>
            </form>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             data: {
                 footerHtml: '',
                 headHtml: ''
             }
         };
     },
     route: {
         data: function(transition) {
             var self = this;
             this.fetchData(function() {
                 transition.next();
             });
         }
     },
     methods: {
         moment: moment,
         save: function() {
             stallion.request({
                 url: '/st-publisher/config/update-site-settings',
                 method: 'POST',
                 data: this.data,
                 form: this.$el,
                 success: function(o) {
                     stallion.showSuccess("Changes saved");
                 }
             });
         },
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         },
         fetchData: function(callback) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/config/site-settings',
                 success: function (o) {
                     self.data = o;
                     
                     if (callback) callback();
                 }
             });
         }
     }
 }
</script>
