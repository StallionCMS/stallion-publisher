<style>
 
</style>



<template>
    <div>
        <loading-div v-if='$loadingRouteData'></loading-div>
        <div v-if="!$loadingRouteData">
            <h2>Site Information</h2>
            <form @submit.prevent='saveSettings' name="settingsForm">
                <div class="form-group">
                    <label>Site Title</label>
                    <input class="form-control" v-model="data.siteTitle">
                </div>
                <div class="form-group">
                    <label>Site Description</label>
                    <textarea class="form-control" v-model="data.siteDescription"></textarea>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-md btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </div> 
</template>

<script>
 module.exports = {
     data: function() {
         return {
             data: {}
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
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         },
         saveSettings: function() {
             stallion.request({
                 url: '/st-publisher/config/update-site-settings',
                 method: 'POST',
                 //form: self.settingsForm,
                 data: this.data,
                 success: function(o) {
                     stallion.showSuccess("Changes saved");
                 }
             });
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
