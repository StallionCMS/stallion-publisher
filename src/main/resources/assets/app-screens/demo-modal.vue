<style lang="scss">

</style>
<template>
    <modal-base class="demo-modal-vue" ref="themodal" title="Demo modal" v-on:close="$emit('close')">
        <div slot="body">
            <div class="row">
                <div class="col-sm-12">
                    <h1>This is a demo modal!!!</h1>
                </div>
            </div>
        </div>
    </modal-base>
</template>
<script>
 module.exports = {
     data: function() {
         return {

         }
     },
     methods: {
     }
 };
</script>
