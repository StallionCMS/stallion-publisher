
var ContactRow = React.createClass({
    render: function() {
        var contact = this.props.contact;
        return (
            <tr>
                <td>{contact.name}</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        );
    }
});

var ContactsScreen = React.createClass({
    getInitialState: function() {
        return {
            contacts: []
        };
    },
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            var contacts = [];
            for (var x =0; x< 50; x++) {
                contacts.push({
                    id: x,
                    name: 'My contact'
                });
            }
            self.setState({contacts: contacts});
        }, 10);
        
    },
    render: function() {
        var self = this;
        var rows = this.state.contacts.map(function(contact) {
            return (<ContactRow contact={contact} key={contact.id} />);
        });
        return (
            <div>
                <h2 className="page-header">Contacts</h2>
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Author</th>
                            <th>Published</th>
                            <th></th>
                            <th></th>
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


