var SiteInformationSettingsPage = React.createClass({
    componentDidMount: function() {
        stallion.autoGrow({
            preGrowCallback: function() {},
            postGrowCallback: function() {}
        }, $('#site-settings-page textarea'));
    },
    render: function() {
        return (
        <div id="site-settings-page">
            <h2 className="page-header">Site Information</h2>
            <form>
                <div className="form-group">
                    <label>Site description</label>
                    <ReactTextarea className="form-control" name="headHtml" />
                </div>
                <button type="submit" className="btn btn-primary">Save</button>
            </form>            
        </div>
        );
    }
});

