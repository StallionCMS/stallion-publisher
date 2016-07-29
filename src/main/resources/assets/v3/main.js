
var StallionApplicationRouter = null;

(function() {

    var admin = {};

    admin.init = function() {

        if ($('#stallion-publisher-main-vue-app').length < 1) {
            return;
        }

        // The router needs a root component to render.
        // For demo purposes, we will just use an empty one
        // because we are using the HTML as the app template.
        // !! Note that the App is not a Vue instance.
        var App = Vue.extend({})
        
        // Create a router instance.
        // You can pass in additional options here, but let's
        // keep it simple for now.
        var router = new VueRouter({
            transitionOnLoad: true
        });

        // Define some routes.
        // Each route should map to a component. The "component" can
        // either be an actual component constructor created via
        // Vue.extend(), or just a component options object.
        // We'll talk about nested routes later.
        router.map({
            '/': {
                component: vueComponents['dashboard-home']
            },
            '/new-page': {
                component: vueComponents['new-page']
            },
            
            '/new-post': {
                component: vueComponents['new-post']
            },
            '/edit-content/:contentId': {
                component: vueComponents['page-editor']
            },
            '/pages': {
                name: 'pages',
                component: vueComponents['contents-table']
            },
            '/pages/:page': {
                name: 'pages',
                component: vueComponents['contents-table']
            },
            '/posts': {
                name: 'posts',
                component: vueComponents['contents-table']
            },
            '/posts/:page': {
                name: 'posts',
                component: vueComponents['contents-table']
            },
            '/contacts': {
                component: vueComponents['contacts-table']
            },
            '/tiny': {
                component: vueComponents['tiny-demo']
            },            
            '/files': {
                component: vueComponents['file-library']
            },
            '/file-upload': {
                component: vueComponents['file-upload']
            },
            '/comments': {
                component: vueComponents['comments-table']
            },
            '/settings/authors': {
                component: vueComponents['settings-authors']
            },
            '/settings/extra-html': {
                component: vueComponents['settings-extra-html']
            },
            '/settings/global-modules': {
                component: vueComponents['settings-global-modules']
            },
            '/settings/site-information': {
                component: vueComponents['settings-site-information']
            },
            '/tomes': {
                component: vueComponents['tomes-table']
            },
            '/tomes/*any': {
                component: vueComponents['tomes-table']
            },
        });

        router.beforeEach(function(transition) {
            console.log('routing!');
            transition.next();
        });
        
        // Now we can start the app!
        // The router will create an instance of App and mount to
        // the element matching the selector #app.
        router.start(App, '#stallion-publisher-main-vue-app')
        StallionApplicationRouter = router;
    };
    
    //admin.init();
    Dropzone.autoDiscover = false;
    $(document).ready(admin.init);
        
}());

