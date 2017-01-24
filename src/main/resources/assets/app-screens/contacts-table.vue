<style>
 
</style>



<template>
    <div class="contacts-table-vue table-page">
        <loading-div v-if="isLoading"></loading-div>
        <div v-if="!isLoading">
            <st-data-table ref="table" :columns="columns" :label="'contact'" :browser-url-template="'#!/contacts'" :data-url="'/st-publisher/contacts/list'" :route="$route" table-class="table">
            </st-data-table>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             isLoading: false,
             columns: [
                 {
                     actions: [
                         {
                             label: 'Details',
                             className: 'btn btn-xs btn-default',
                             getLink: function(item) {
                                 return '#!/contact-details/' + item.id;
                             }
                         }
                     ]
                 },
                 {
                     title: 'Name',
                     field: 'displayName'
                 },
                 {
                     title: 'Email',
                     field: 'email'
                 },
                 {
                     title: 'Created',
                     render: function(item) {
                         return moment(item.createdTicks).fromNow();
                     }
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
