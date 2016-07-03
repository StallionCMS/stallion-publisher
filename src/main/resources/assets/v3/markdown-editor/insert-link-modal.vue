

<template>
    <div class="insert-link-modal">
        <modal-base v-ref:themodal :shown="true" title="Insert Link">
            <div slot="body">
                <div class="row">
                    <div class="col-sm-4">
                        <a href="javascript:;" v-on:click="tab='internal-link'" v-bind:class="{'vertical-tab': true, 'active-tab': tab==='internal-link'}">
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
                            <input type="text" name="link" class="form-control" v-model="link" placeholder="https://...">            
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
         shown: {
             twoWay: true
         },
         callback: Function
     },
     data: function() {
         return {
             tab: 'internal-link',
             link: ''
         }
     },
     created: function() {
         
     },
     methods: {
         linkPicked: function(link) {
             this.link = link;
             this.callback(this.link);
             this.$refs.themodal.close();             
         },
         filePicked: function(file) {
             this.link = file.url;
             this.callback(this.link);
             this.$refs.themodal.close();
         }
     }
     
     
 }
</script>
