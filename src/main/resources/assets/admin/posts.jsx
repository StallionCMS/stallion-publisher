
var PostRow = React.createClass({
    render: function() {
        var post = this.props.post;
        return (
            <tr>
                <td>{post.name}</td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        );
    }
});

var PostsPage = React.createClass({
    getInitialState: function() {
        return {
            posts: []
        };
    },
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            var posts = [];
            for (var x =0; x< 50; x++) {
                posts.push({
                    id: x,
                    name: 'My post'
                });
            }
            self.setState({posts: posts});
        }, 10);
        
    },
    render: function() {
        var self = this;
        var header = this.props.header || "Blog Posts";
        var rows = this.state.posts.map(function(post) {
            return (<PostRow post={post} key={post.id} />);
        });
        return (
            <div>
                <h2 className="page-header">{header}</h2>
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



var PagesPage = React.createClass({
    render: function() {
        return (
            <PostsPage isPosts={false} header="Pages" />
        );
    }
});
