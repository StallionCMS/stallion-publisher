<style lang="scss">
 .new-page-vue {
     .recent-page-choice {
         width: 250px;
         height: 300px;
         display: inline-block;
         margin-right: 20px;
     }

     .page-title {
         text-overflow: ellipsis;
         white-space: nowrap;
         overflow: hidden;
         height: 30px;
     }
     iframe {
         border: 1px solid #999;
     }
     .frame-mask {
         position: absolute;
         z-index: 100;
         height: 200px;
         width: 250px;
         opacity: .2;
         padding-top: 100px;
         padding-left: 40px;
         background-color: #888;
     }
     .click-mask {
         position: absolute;
         z-index: 200;
         height: 250px;
         width: 250px;
         padding-top: 80px;
         text-align: center;
         background-color: transparent;
         button.btn-primary {
             
         }
     }
     .recent-page-choice {
         height: 250px;
         overflow: hidden;
     }
     .no-thumb {
         border: 1px solid #ddd;
         width: 140px;
         height: 170px;
         display: inline-block;
         text-align: center;
         margin-right: 25px;
         font-size: 18px;
         .template-inner {
             margin-top: 66px;
             display: inline-block;
         }
     }
     .template-choice {
         display: inline-block;
         margin-right: 10px;
         cursor: pointer;
     }
     .template-choice:hover .image-wrap, .template-choice:hover .no-thumb {
         background-color: #F8F8F8;
     }
     
     .template-choice img {
         min-width: 100px;
         max-width: 190px;
         max-height: 150px;
         min-height: 120px;
     }
     
 }
</style>

<template>
    <div class="new-page-vue">
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if='!$loadingRouteData'>
            <h2>Create a new page</h2>
            <div class="recent-pages">
                <h4>Clone a recent page</h4>
                <div>
                    <div class="recent-page-choice" v-for='page in recentPages'>
                        <div v-st-tooltip class="page-title" data-toggle="tooltip" data-placement="bottom"  :title="page.title">{{ page.title }}</div>
                        <div class="click-mask" @click="onClone(page.id)"><button class="btn btn-primary">Clone</button></div>
                        <div class="frame-mask"></div>
                        <iframe v-bind:src="'/st-publisher/content/' + page.id + '/view-latest-version'" class="frame-25" style="width: 1000px; height: 800px;"></iframe>
                    </div>
                </div>
            </div>
            <div class="page-templates">
                <h4>New from a template</h4>
                <div>
                    <div @click='onChooseTemplate(template.template)' class="template-choice" v-for='template in templates'>
                        <div class="no-thumb" v-if="!template.thumb">
                            <div class="template-inner">{{ template.template }}</div>
                        </div>
                        <div v-if="template.thumb" class="image-wrap">
                            <img v-if="template.thumb" :src="template.thumb">
                            <br>
                            {{ template.template }}
                        </div>
                    </div>
                </div>
            </div>
            <div class="special-templates">
                <h4>New from a special template</h4>
                <div>
                    <div @click='onChooseTemplate(template.template)' class="template-choice" v-for='template in specialTemplates'>
                        <div class="no-thumb" v-if="!template.thumb">
                            <div class="template-inner">{{ template.template }}</div>
                        </div>
                        <div v-if="template.thumb" class="image-wrap">
                            <img v-if="template.thumb" :src="template.thumb">
                            <br>
                            {{ template.template }}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
 module.exports = {
     data: function() {
         return {
             templates: [],
             specialTemplates: [],
             recentPages: []
         }
     },
     ready: function() {
         var self = this;
     },
     route: {
         data: function(transition) {
             var self = this;
             stallion.request({
                 url: '/st-publisher/content/choose-page-template-context',
                 method: 'GET',
                 success: function(o) {
                     self.templates = o.templates;
                     self.specialTemplates = o.specialTemplates;
                     self.recentPages = o.recentPages;
                     transition.next();
                     setTimeout(function() {
                         $(self.$el).find('.page-title').tooltip();
                     }, 1000);
                     
                 }
             });
         }
     },
     methods: {
         onChooseTemplate: function(template) {
             this.createNewPageAndRedirect(0, template);
         },
         onClone: function(cloneId) {
             this.createNewPageAndRedirect(cloneId, '');
         },
         createNewPageAndRedirect: function(cloneId, template) {
             var self = this;
             console.log('new page cloneId ', cloneId, 'template ', template);
             stallion.request({
                 url:'/st-publisher/content/new-for-editing',
                 method: 'POST',
                 data: {
                     type: 'page',
                     cloneId: cloneId,
                     template: template
                 },             
                 success: function(o) {
                     window.location.hash = '/edit-content/' + o.postId;
                 }
             });
         }
     }
 }

</script>
