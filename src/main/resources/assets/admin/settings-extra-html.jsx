var ExtraHtmlSettingsPage = React.createClass({
    render: function() {
        return (
            <div id="site-settings-page">
                <h2 className="page-header">Extra HTML and CSS</h2>
                <form>
                    <div className="form-group">
                        <label>Head section HTML</label>
                        <ReactTextarea className="form-control" name="headHtml" />
                    </div>
                    <div className="form-group">
                        <label>Footer section HTML</label>
                        <textarea className="form-control" name="footerHtml"></textarea>
                    </div>
                    <div className="form-group">
                        <label>Extra CSS</label>
                        <textarea className="form-control" name="footerHtml"></textarea>
                    </div>
                    <button type="submit" className="btn btn-primary">Save</button>
                </form>            
            </div>
        );
    }
});

