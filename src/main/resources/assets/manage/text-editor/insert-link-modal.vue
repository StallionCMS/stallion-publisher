<style lang="scss">
 .insert-link-modal-vue {
     .tab-box {
         border-top: 1px solid #E9E9E9;
         border-left: 1px solid #E9E9E9;
         padding-top: 1em;
         margin-left: -16px;
         padding-left: 28px;
     }
     .tab-left {
         z-index: 3;
     }
     .tabs-row {

     }
 }
</style>

<template>
    <div class="insert-link-modal-vue">
        <modal-base v-ref:themodal :shown.sync="shown" title="Insert Link" :callback="modalClosed">
            <div slot="body">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="form-group">
                            <label>Link text</label>
                            <input type="text" v-model="text" class="form-control">
                        </div>
                    </div>
                </div>
                <div class="row" style="tabs-row">
                    <div class="col-sm-4 tab-left">
                        <a v-if="!hideInternalPages" href="javascript:;" v-on:click="tab='internal-link'" v-bind:class="{'vertical-tab': true, 'active-tab': tab==='internal-link'}">
                            Internal Page
                        </a>
                        <a href="javascript:;" v-on:click="tab='external-link'" v-bind:class="{'vertical-tab': true, 'active-tab': tab==='external-link'}">
                            External Link
                        </a>
                        <a href="javascript:;" v-on:click="tab='document'" v-bind:class="{'vertical-tab': true, 'active-tab': tab==='document'}">
                            Document or File
                        </a>
                    </div>
                    <div class="col-sm-8 tab-box">
                        <div v-if='tab==="external-link"'>
                            <label>Enter the URL of the page here</label>
                            <input type="text" autofocus="autofocus" name="link" class="form-control" v-model="link" placeholder="https://..." @keyup.enter="insert">            
                        </div>
                        <div v-if='tab==="internal-link"'>
                            <internal-link-picker :callback="linkPicked"></internal-link-picker>
                        </div>
                        <div v-if='tab==="document"'>
                            <file-library :ispicker="true" :callback="filePicked" ></file-library>
                        </div>
                    </div>
                </div><!-- /.row -->
            </div><!-- end .body -->
        </modal-base>
    </div>
</template>

<script>
 module.exports = {
     props: {
         hideInternalPages: false,
         text: '',
         link: '',
         shown: {
             twoWay: true
         },
         callback: Function
     },
     data: function() {
         var tab = 'internal-link';
         if (this.link  || this.hideInternalPages) {
             tab = 'external-link';
         }
         return {
             tab: tab
         }
     },
     created: function() {
         
     },
     methods: {
         insert: function() {
             this.callback(this.link, this.text);
             this.$refs.themodal.close();             
         },
         modalClosed: function() {
             this.callback(this.link, this.text);
         },
         linkPicked: function(o) {
             this.link = o.url;
             this.callback(this.link, (self.text || o.text));
             this.$refs.themodal.close();             
         },
         filePicked: function(file) {
             if (file.name && !this.text) {
                 this.text = file.name;
             }
             this.link = file.url;
             this.callback(this.link, this.text);
             this.$refs.themodal.close();
         }
     }
     
     
 }
</script>
