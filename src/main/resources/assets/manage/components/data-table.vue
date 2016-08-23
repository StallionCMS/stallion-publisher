<style lang="scss">

 .data-table-vue {

 }

</style>

<template>
    <div class="data-table-vue">
        <table class="table table-striped">
            <slot name="thead">
                
            </slot>
            <slot name="tfooter">
                
            </slot>
            <slot name="tfoot">
                <tfoot>
                    I am table footer    
                </tfoot>
            </slot>
        </table>
    </div>
</template>
<script>
 module.exports = {
     props: {
         baseUrl: {
             required: true,
             type: String
         }
     },
     data: function() {
         console.log('load data table ' + this.baseUrl);
         return {
             
         };
     },
     methods: {
         query: function(params) {

         },
         makeUrl: function() {

         }
     }
     
 }
</script>
