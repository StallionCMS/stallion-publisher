
var StallionApplicationRouter = null;

(function() {

    var admin = {};

    admin.init = function() {

        if ($('#stallion-publisher-main-vue-app').length < 1) {
            return;
        }

        Vue.directive('st-tooltip', {
            bind: function () {
                // do preparation work
                // e.g. add event listeners or expensive stuff
                // that needs to be run only once
                //debugger;
                $(this.el).tooltip({});
            },
            update: function (newValue, oldValue) {
                // do something based on the updated value
                // this will also be called for the initial value

            },
            unbind: function () {
                // do clean up work
                // e.g. remove event listeners added in bind()
            }
        });        

        // The router needs a root component to render.
        // For demo purposes, we will just use an empty one
        // because we are using the HTML as the app template.
        // !! Note that the App is not a Vue instance.
        var App = Vue.extend({
            data: function() {
                return {
                    className: 'someclass'
                }
            }
        })

        // Define some routes.
        // Each route should map to a component. The "component" can
        // either be an actual component constructor created via
        // Vue.extend(), or just a component options object.
        // We'll talk about nested routes later.
        //router.map({
        var routes = [
            {
                path: '/',
                name: 'dashboard-home',
                component: vueComponents['dashboard-home']
            },
            {
                path: '/new-page',
                component: vueComponents['new-page']
            },
            {
                path: '/new-post',
                component: vueComponents['new-post']
            },
            {
                path: '/edit-content/:contentId',
                component: vueComponents['page-editor']
            },
            {
                path: '/ui-demo',
                name: 'ui-demo',
                component: vueComponents['ui-demo']
            },
            {
                path: '/pages',
                name: 'pages',
                component: vueComponents['contents-table']
            },
            {
                path: '/pages/:page',
                name: 'pages-page',
                component: vueComponents['contents-table']
            },
            {
                path: '/posts',
                name: 'posts',
                component: vueComponents['contents-table']
            },
            {
                path: '/posts/:page',
                name: 'posts-page',
                component: vueComponents['contents-table']
            },
            {
                path: '/contacts',
                component: vueComponents['contacts-table']
            },
            {
                path: '/tiny',
                component: vueComponents['tiny-demo']
            },
            {
                path: '/files',
                component: vueComponents['file-library']
            },
            {
                path: '/file-upload',
                component: vueComponents['file-upload']
            },
            {
                path: '/comments',
                component: vueComponents['comments-table']
            },
            {
                path: '/settings/authors',
                component: vueComponents['settings-authors']
            },
            {
                path: '/settings/author/new',
                component: vueComponents['settings-author-details']
            },
            {
                path: '/settings/author/:userId',
                component: vueComponents['settings-author-details']
            },
            {
                path: '/settings/extra-html',
                component: vueComponents['settings-extra-html']
            },
            {
                path: '/settings/global-modules',
                component: vueComponents['settings-global-modules']
            },
            {
                path: '/settings/site-information',
                component: vueComponents['settings-site-information']
            },
            {
                path: '/tomes',
                component: vueComponents['tomes-table']
            },
            {
                path: '/tomes/*any',
                component: vueComponents['tomes-table']
            }
        ];

        
        // Create a router instance.
        // You can pass in additional options here, but let's
        // keep it simple for now.
        var router = new VueRouter({
            routes: routes,
            transitionOnLoad: true
        });
        

        router.beforeEach(function(to, from, next) {
            if (to.path.indexOf('/edit-content') === 0) {
                $('#stallion-publisher-main-vue-app').addClass('st-editor-page');
            }
            next();
        });

        var after = function(myRouter) {
            return function(to, from) {
                if (to.path.indexOf('/edit-content') !== 0) {
                    $('#stallion-publisher-main-vue-app').removeClass('st-editor-page');
                }
                //debugger;
                if (myRouter.app.$refs.sidebar) {
                    myRouter.app.$refs.sidebar.updatePath(to.path);
                }
            };

        }(router);

        router.afterEach(after);
        
        // Now we can start the app!
        // The router will create an instance of App and mount to
        // the element matching the selector #app.
        //router.start(App, )


        var App = new Vue(
            {
                router: router
            }
        );
        var AppMounted = App.$mount('#stallion-publisher-main-vue-app');

        
        StallionApplicationRouter = router;
    };
    
    //admin.init();
    Dropzone.autoDiscover = false;
    $(document).ready(admin.init);
        
}());

