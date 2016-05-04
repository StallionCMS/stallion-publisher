var SitemapSettingsScreen = React.createClass({
    componentDidMount: function() {
    },
    render: function() {
        return (
        <div id="site-settings-page">
            <h2 className="page-header">Site Map</h2>
            <table>
                <tr>
                    <td>Products</td>
                    <td>Services</td>                    
                    <td>Company</td>
                </tr>
                <tr>
                    <td>Dedicated Servers</td>
                    <td>Custom Applications</td>
                    <td>Management Team</td>                    
                </tr>
                <tr>
                    <td>Cloud Servers</td>
                    <td>Security Audits</td>
                    <td>Investor Info</td>                    
                </tr>
                <tr>
                    <td>Managed Hosting</td>
                    <td>Devops as a service</td>
                    <td></td>                    
                </tr>
                <tr>
                    <td></td>
                    <td>Open source hostings</td>
                    <td></td>                    
                </tr>
                <tr>
                    <td><a href="#">Add page</a></td>
                    <td><a href="#">Add page</a></td>
                    <td><a href="#">Add page</a></td>                    
                </tr>
            </table>
        </div>
        );
    }
});

