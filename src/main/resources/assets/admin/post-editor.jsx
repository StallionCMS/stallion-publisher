var Spinner = React.createClass({
    render: function() {
        return (
            <div>
                <div style={{marginLeft: 'auto', marginRight: 'auto', display: 'inline-block'}}>Loading...</div>
            </div>);
    }
});

var PostEditor = React.createClass({
    getInitialState: function() {
        return {'content': this.props.content || "Type to start editing..."};
    },
    componentDidMount: function() {
        this.simplemde = new SimpleMDE({ element: document.getElementById("MyID") });
        this.simplemde.value(this.state.content);
    },
    _insertWidget: function() {
        var $node = $('<div class="line-widget"><span class="widget-label">Contact form</span> <span class="line-widget-edit btn btn-default btn-xs">Edit widget</span> <span class="line-widget-delete btn btn-xs btn-default">remove &#xd7;</span></div>').addClass('line-widget');
        var widget = this.simplemde.codemirror.addLineWidget(1, $node.get(0), {});
        $node.find('.line-widget-delete').click(function() {
            widget.clear();
        });
    },
    render: function() {
        return (
            <div>
                <textarea id="MyID"></textarea>
                <div><a href="javascript:;" className="btn btn-primary" onClick={this._insertWidget}>Insert widget</a></div>
            </div>
        );
    }
});

var NewBlogPost = React.createClass({
    componentDidMount: function() {
        var self = this;
        setTimeout(function() {
            self.setState({'ctx': {
                'defaultTitle': 'My Default title',
                post: {
                    content: 'My post is the stuff'
                }
            }});
        }, 10);
        
    },
    
    render: function() {
        var self = this;
        if (!this.state || !this.state.ctx) { return (<Spinner />); }
        var header = this.props.header || "New Blog Post";
        return (
            <div>
                <h2 className="page-header">{header}</h2>
                <div className="row">
                    <div className="col-md-12">
                        <PostEditor content={self.state.ctx.post.content} />
                    </div>
                </div>
            </div>
        );
    }
    
});
 
var NewPage = React.createClass({
    render: function() {
        return (
            <NewBlogPost isPage={true} header="New page" />
        );
    }
});
