var Router = ReactRouter;
var DefaultRoute = ReactRouter.DefaultRoute;
var Route = ReactRouter.Route;

var NotFound = React.createClass({
    render: function() {
        return (
            <h4>Page not found</h4>
        );
    }
});
    

var routes = (
  <Route name="main">
      <Route name="dashboard" path='/' handler={Dashboard} />
      <Route name="newPost" path='/new-post' handler={NewBlogPost} />
      <Route name="newPage" path='/new-page' handler={NewPage} />      
      <Route name="posts" path='/posts' handler={PostsPage} />
      <Route name="pages" path='/pages' handler={PagesPage} />
      <Route name="comments" path='/comments' handler={CommentsScreen} />
      <Route name="widgets" path='/settings/widgets' handler={WidgetsScreen} />
      <Route name="sitemap" path='/settings/sitemap' handler={SitemapSettingsScreen} />
      <Route name="contacts" path='/contacts' handler={ContactsScreen} />
      <Route name="files" path='/files' handler={FilesScreen} />
      <Route name="authors" path='/settings/authors' handler={AuthorsSettingsPage} />
      <Route name="blogs" path='/settings/blogs' handler={BlogsSettingsPage} />
      <Route name="settingsSiteInformation" path='/settings/site-information' handler={SiteInformationSettingsPage} />
      <Route name="extraHtml" path='/settings/extra-html' handler={ExtraHtmlSettingsPage} />
      <Route name="notFound" path='*' handler={NotFound} />
  </Route>
);


var setActivePage = function(name) {
    console.log('active class is ', name);
    $('.nav li').removeClass('active');
    if (name) {
        $('.nav li.' + name).addClass('active');
    }
};


Router.run(routes, Router.HashLocation, function(Root) {
    ReactDOM.render(<Root />, document.getElementById('react-app'));
    var routes = Root.getCurrentRoutes();
    if (routes) {
        var route = routes[routes.length - 1];
        if (route) {
            setActivePage(route.name);
        }
    }
});


ReactDOM.render(<SidebarConfigSection/>, document.getElementById('sidebar-config-container'));
