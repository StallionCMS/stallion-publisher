
var CommentRow = React.createClass({
    render: function() {
        var comment = this.props.comment;
        return (
            <tr>
                <td>{comment.name}</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        );
    }
});

var CommentsScreen = React.createClass({
    getInitialState: function() {
        return {
            comments: []
        };
    },
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            var comments = [];
            for (var x =0; x< 50; x++) {
                comments.push({
                    id: x,
                    name: 'My comment'
                });
            }
            self.setState({comments: comments});
        }, 10);
        
    },
    render: function() {
        var self = this;
        var rows = this.state.comments.map(function(comment) {
            return (<CommentRow comment={comment} key={comment.id} />);
        });
        return (
            <div>
                <h2 className="page-header">Comments</h2>
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


