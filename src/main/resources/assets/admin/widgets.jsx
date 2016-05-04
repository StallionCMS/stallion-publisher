
var WidgetRow = React.createClass({
    render: function() {
        var widget = this.props.widget;
        return (
            <tr>
                <td>
                    <a href="javascript:;" className="btn btn-default btn-xs">Edit</a>
                </td>
                <td>
                    <div><big>{widget.name}</big></div>
                    <div>{widget.description}</div>
                </td>
                <td><small>{widget.type}</small></td>
            </tr>
        );
    }
});

/**
Widget Configuration


widgets/my-widget.toml

name: 
description: 
type: 

If custom:

template:
fields:
maxEntries: 1
minEntries: 
editorJsx: 
publicJs:
publicCss: 

Builtin widgets:

- slider
- site map
- links

    

*/

var WidgetsScreen = React.createClass({
    getInitialState: function() {
        return {
            widgets: []
        };
    },
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            var widgets = [
                {
                    name: 'Top Menu',
                    description: 'The links that appear in the very top of your home page.',
                    type: 'Links',
                    thumbnail: '',
                    data: {
                        links: [
                            {
                                'label': 'Menu',
                                'url': '/menu'
                            },
                            {
                                'label': 'Contact & Hours',
                                'url': '/contact'
                            }
                        ]
                    }
                },
                {
                    name: 'Site map menu',
                    description: 'The site map style menu that appears on the bottom of all pages',
                    type: 'Site Map',
                    data: {
                        columns: [
                            {
                                links: [
                                    {
                                        'label': 'Key Information',
                                        },
                                    {
                                        'label': 'Menu',
                                        'url': '/menu'
                                    },
                                    {
                                        'label': 'Contact & Hours',
                                        'url': '/contact'
                                    }
                                ]
                            },
                            {
                                links: [
                                    {
                                        'label': 'Company',
                                        },
                                    {
                                        'label': 'Management Team',
                                        'url': '/menu'
                                    },
                                    {
                                        'label': 'Investor Relations',
                                        'url': '/contact'
                                    }
                                ]
                            }                            
                        ]
                    }
                }

            ];
            
            self.setState({widgets: widgets});
        }, 10);
        
    },
    render: function() {
        var self = this;
        var rows = this.state.widgets.map(function(widget) {
            return (<WidgetRow widget={widget} key={widget.id} />);
        });
        return (
            <div>
                <h2 className="page-header">Widgets</h2>
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <th></th>
                            <th>Name</th>
                            <th>Type</th>
                        </tr>
                    </thead>
                    <tbody>
                        {rows}
                    </tbody>
                </table>
            </div>
        );
    }
});


