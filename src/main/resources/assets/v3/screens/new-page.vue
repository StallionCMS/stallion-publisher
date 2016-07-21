<style>

</style>

<template>
    <div>
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if='!$loadingRouteData'>
            <div class="recent-pages">
                <h5>Clone a recent page</h5>
                <div>
                    <div class="recent-page-choice" v-for='page in recentPages' style="overflow: hidden; width: 250px; height: 300px; display: inline-block; margin-right: 20px;">
                        <div>{{ page.title }}</div>
                        <div style="position: absolute; z-index: 100; height: 250px; width: 300px; opacity: .2; padding-top: 100px; padding-left: 40px; background-color: white;"><button class="btn btn-primary">Clone</button></div>
                        <iframe v-bind:src="'/st-publisher/content/' + page.id + '/view-latest-version'" class="frame-25" style="width: 1000px; height: 800px;"></iframe>
                    </div>
                </div>
            </div>
            <div class="page-templates">
                <h5>Start from a template</h5>
                <div>
                    <div @click='onChooseTemplate(template.template)' class="template-choice" v-for='template in templates'>
                        <img v-bind:src="template.thumbnail">
                        <br>
                        {{ template.template }}
                    </div>
                </div>
            </div>
            <div class="special-templates">
                <h5>Special templates</h5>
                <div>
                    <div @click='onChooseTemplate(template.template)' class="template-choice" v-for='template in specialTemplates'>
                        <img v-bind:src="template.thumbnail">
                        <br>
                        {{ template.template }}
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
