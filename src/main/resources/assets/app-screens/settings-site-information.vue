<style>
 
</style>



<template>
    <div>
        <loading-div v-if='isLoading'></loading-div>
        <div v-if="!isLoading">
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
                    <!--
                    <a href="javascript:;" @click="modalShown=true">Show Modal</a>
                    -->
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-md btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
        <!-- 
        <demo-modal v-if="modalShown" v-on:close="modalShown=false"></demo-modal>
        -->
    </div> 
</template>

<script>
 module.exports = {
     data: function() {
         return {
             data: {},
             isLoading: true,
             modalShown: false
         };
     },
     created: function() {
         this.fetchData();
     },
     watch: {
         '$route': function() {
             this.onRoute();
         }
     },
     methods: {
         onRoute: function() {
             this.fetchData();
         },
         moment: moment,
         smartFormatDate: function(date) {
             var m = moment(date);
             return m.fromNow();
         },
         saveSettings: function() {
             var self = this;
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
         fetchData: function() {
             var self = this;
             stallion.request({
                 url: '/st-publisher/config/site-settings',
                 success: function (o) {
                     self.data = o;
                     self.isLoading = false;
                 }
             });
         }
     }
 }
</script>
