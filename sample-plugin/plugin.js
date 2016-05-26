
(function() {

    // Register widget that we can insert via the text editor
    Publisher.registerWidget({
        label: 'Product Preview',
        description: '',
        thumbnail: '',
        riotTag: 'product-preview-widget'
    });
    
    // Register admin menu item
    Publisher.registerAdmin({
        pluginName: pluginName,
        label: 'Products',
        route: '/products',
        routes: [
            {
                route: '/products',
                riotTag: 'products-table'
            },
            {
                route: '/products-edit/*',
                riotTag: 'products-edit-product'
            }
        ],
        scripts: [
            {
                url: 'products:/products-admin.riot.tag',
                processor: 'riot'
            }
        ],
        stylesheets: [
            {
                url: 'products:/products-admin.css'
            }
        ]
    });

    // Add to the global stylesheet
    Publisher.registerGlobalAssets({
        scripts: [
            {
                url: 'products:/products-public.js'
            }
        ],
        stylesheets: [
            {
                url: 'products:/products-public.css'
            }
        ]
    });
    
    var controller = stallion
        .modelRegistration()
        .columns({
            // TODO: define the properties of the model
            name: new stallion.StringCol({}),
            description: new stallion.StringCol({}),
            slug: new StringCol({uniqueKey: true}),
            // Set to the current UTC time when creating a new instance
            createdAt: new DateTimeCol({nowOnCreate: true}),
            // Set to the current UTC time when updating a an instance;
            updatedAt: new DateTimeCol({nowOnUpdate: true})
        })
        // TODO: define the bucket name
        .bucket('products')
        .register();

    var publicScreens = {
        _meta: {
            baseRoute: '',
            defaultRole: 'anon',
            produces: 'application/json'
        },
        viewProduct: {
            route: '/products/*productSlug',
            params: stallion.pathParam('productSlug'),
            handle: function(productSlug) {
                var product = controller.find({slug: productSlug, published: true}).firstOrNotFound();
                // TODO: this template is defined per-site
                return stallion.renderTemplate('products-plugin/products-page.jinja', {page: product});
            }
        }
    };
    
    var apiEndpoints = {
        _meta: {
            // TODO: change this base URL
            baseRoute: '/st-publisher/api/v1/products',
            role: 'staff'
        },
        create: {
            route: '/',
            method: 'POST',
            params: [stallion.mapParam()],
            handle: function(newObj) {
                var obj = controller.newModel(
                    stallion.SafeMerger.with()
                    // TODO: set the fields that are either required or optional
                        .required('title', 'author')  // not-null, not empty
                        .notNull('content')           // not-null, can be empty
                        .optional('description')      // if null, won't be included
                        .toMap()
                );
                controller.save(obj);
                return obj;
            }
        },
        update: {
            route: '/',
            method: 'POST',
            params: [stallion.mapParam()],
            handle: function(newObj) {
                var obj = controller.newModel(
                    stallion.SafeMerger.with()
                    // TODO: set the fields that are either required or optional
                        .required('title', 'author')  // not-null, not empty
                        .notNull('content')           // not-null, can be empty
                        .optional('description')      // if null, won't be included
                        .toMap()
                );
                controller.save(obj);
                return obj;
            }
        },
        get: {
            route: '/:id',
            method: 'GET',
            params: [stallion.pathParam('id')],
            handle: function(id) {
                return controller.forIdOrNotFound(id);
            }
        },
        list: {
            route: '/',
            method: 'GET',
            params: [],
            handle: function(newObj) {
                var obj = controller.newModel(
                    stallion.SafeMerger.with()
                    // TODO: set the fields that are either required or optional
                        .required('title', 'author')  // not-null, not empty
                        .notNull('content')           // not-null, can be empty
                        .optional('description')      // if null, won't be included
                        .toMap()
                );
                controller.save(obj);
                return obj;
            }

        }
    };
    

    
}());
