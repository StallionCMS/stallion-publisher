var SidebarConfigSection = React.createClass({
    getInitialState: function() {
        return {configShown: window.location.hash.indexOf('/settings') > -1};
    },
    toggleConfigShown: function() {
        this.setState({configShown: !this.state.configShown});
        console.log('update config shown');
    },
    render: function() {
        var shown = (
            <div>
                <a href="javascript:;" className="show-config-link shown" onClick={this.toggleConfigShown}>Configuration 	&#9662;</a>
                <ul className="nav nav-sidebar config-options">
                    <li><a href="#/settings/authors">Authors</a></li>
                    <li><a href="#/settings/blogs">Blogs</a></li>
                    <li><a href="#/settings/extra-html">Extra HTML and CSS</a></li>
                    <li className="widgets"><a href="#/settings/widgets">Template Widgets</a></li>
                    <li className="sitemap"><a href="#/settings/sitemap">Sitemap</a></li>
                    <li><a href="#/settings/site-information">Site Information</a></li>
                </ul>
            </div>
        );
        var hidden = (
            <div>
                <a className="show-config-link not-shown" href="javascript:;"  onClick={this.toggleConfigShown}>Configuration &#9656;</a>
                <ul className="nav nav-sidebar">
                </ul>
            </div>
        );
        if (this.state.configShown) {
            return shown;
        } else {
            return hidden;
        }
    }
});
