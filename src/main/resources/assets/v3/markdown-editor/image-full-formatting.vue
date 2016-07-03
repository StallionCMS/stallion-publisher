

<template>
    <div class="image-full-formatting">
        <h3>Formatting</h3>

        <div class="row">
            <div class="col-sm-6">
                <h5>Align</h5>                
                <div class="form-group">
                    <label for="alignCenter">Center <br><input type="radio" name="alignment" value="center"></label>        
                    <label for="alignLeft">Left <br><input type="radio"  name="alignment" value="left"></label>
                    <label for="alignRight">Right <br><input type="radio" name="alignment" value="right"></label>
                    <label for="alignRight">Image Left, Caption Right<br><input type="radio" name="alignment" value="caption-right"></label>
                </div>
            </div>
            <div class="col-sm-6">
                <div class="form-group">
                    <label>Alternative text/Hover text (appears for screen readers and mouse hover)</label>
                    <input type="text" class="form-control" name="altText"></textarea>
                </div>
                <div class="form-group">
                    <label>Link URL (optional) -- link to follow when image is clicked on</label>
                    <input type="text" class="form-control" name="linkUrl"></textarea>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <a show={!advancedVisible} href="javascript:;" onclick={toggleAdvancedOptions}>Show advanced options &#171;</a>
                <a show={advancedVisible} href="javascript:;" onclick={toggleAdvancedOptions}>&#187; Hide advanced options</a>
            </div>
        </div>
        <div class="row" show={advancedVisible}>
            <div class="col-sm-6">
                <div class="form-group">
                    <label>Title/Header (appears above image)</label>
                    <textarea class="form-control" style="height: 2.5em;" name="title"></textarea>
                </div>
                <div class="form-group">
                    <label>Caption (appears below image)</label>
                    <textarea class="form-control" style="height: 2.5em;" name="caption"></textarea>
                </div>
            </div>
            <div class="col-sm-6">
                <div>
                    <h5>Margin</h5>
                    <div style="margin-left: 60px;">Top: <input type="number" name="marginTop" >px</div>
                    <div><span>Left: <input type="number" name="marginLeft" >px</span><span style="margin-left: 20px">Right: <input type="number" name="marginRight">px</span></div>
                    <div style="margin-left: 60px;">Bottom: <input type="number" name="marginBottom" >px</div>
                </div>
                <div class="checkbox">
                    <label><input type="checkbox" name="constrain100" value="true"> Constrain to column width?</label>
                </div>
                <div class="form-group">
                    <label>Max-width</label>
                    <input type="number" name="maxWidth" >px
                </div>
                <div class="form-group">
                    <label>Max-height</label>
                    <input type="number" name="maxHeight" >px
                </div>
                <div class="form-group">
                    <label>Min-width</label>
                    <input type="number" name="minWidth" >px
                </div>
                <div class="form-group">
                    <label>Min-height</label>
                    <input type="number" name="minHeight" >px
                </div>
                <div class="form-group">
                    <label>Border width</label>
                    <input type="number" name="borderWidth">
                </div>
                <div class="form-group">
                    <label>Border color</label>
                    <input type="text" name="borderColor">
                </div>
            </div>
        </div>
    </div>
</template>

<script>
 module.exports = {
     props: {
         widgetData: Object,
     },
     data: function() {
         this.widgetData = this.widgetData || {data: {}};
         var data = JSON.parse(JSON.stringify(this.widgetData.data || {}));
         return {
             data: data
         }
     },
     methods: {
         getData: function() {
             return {};
         }
     }
 }

</script>
