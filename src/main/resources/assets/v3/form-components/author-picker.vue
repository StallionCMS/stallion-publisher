

<template>
    <select class="form-control" name="authorSelect" v-model="value">
        <option>Choose an author</option>
    </select>
</template>


<script>
 module.exports = {
     props: {
         value: {
             twoWay: true
         }
     },

     compiled: function() {
         var self = this;
         
         stallion.request({
             url: '/st-publisher/active-authors',
             success: function(o) {
                 var data = [];
                 o.authors.forEach(function(au) {
                     data.push({
                         id: au.author.id,
                         text: au.user.displayName
                     });
                 });
                 $(self.$el).select2({
                     width: '100%',
                     data: data
                 }).change(function() {
                     console.log('select2 change!');
                     self.value = $(this).val();
                 });
             }
         });
     },
     watch: {
         'value': function(a, b) {
             console.log('author changed ', a, b);
         }
     }
 };

</script>
