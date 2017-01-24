<style>
 
</style>



<template>
    <div>
        <loading-div v-if='isLoading'></loading-div>
        <div v-if="!isLoading">
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
             isLoading: true,
             data: {
                 footerHtml: '',
                 headHtml: ''
             }
         };
     },
     created: function() {
         this.onRoute();
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
