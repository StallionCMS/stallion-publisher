
var FileRow = React.createClass({
    render: function() {
        var file = this.props.file;
        return (
            <tr>
                <td>{file.name}</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        );
    }
});

var FilesScreen = React.createClass({
    getInitialState: function() {
        return {
            files: []
        };
    },
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            var files = [];
            for (var x =0; x< 50; x++) {
                files.push({
                    id: x,
                    name: 'My file'
                });
            }
            self.setState({files: files});
        }, 10);
        
    },
    render: function() {
        var self = this;
        var rows = this.state.files.map(function(file) {
            return (<FileRow file={file} key={file.id} />);
        });
        return (
            <div>
                <h2 className="page-header">Files</h2>
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


