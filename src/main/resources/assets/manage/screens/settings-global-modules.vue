<style>
 
</style>



<template>
    <div class="settings-global-modules-vue table-page">
        <loading-div v-if='$loadingRouteData'></loading-div>
        <div v-if="!$loadingRouteData">
            <st-data-table v-ref:table :table-definition="tableDefinition" :columns="columns" :label="'global module'" :browser-url-template="'#!/settings/global-modules'" :data-url="'/st-publisher/global-modules/list'" :route="$route" table-class="table">
                <div class="actions-slot" slot="actions">
                </div>
            </st-data-table>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             columns: [
                 {
                     actions: [{
                         className: 'btn btn-default btn-xs',
                         label: 'Edit',
                         getLink: function(item) {
                             return '#!/settings/edit-module/' + item.id
                         }
                     }]
                 },
                 {
                     field: 'name'
                 },
                 {
                     field: 'type'
                 }
             ]
         };
     },
     route: {
         data: function() {
         }
     },
     methods: {

     }
 }
</script>
