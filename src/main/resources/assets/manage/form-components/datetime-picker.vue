<style lang="scss">
 .datetime-picker-vue {
     .select2-selection {
         height: 34px;
         border: 1px solid #ccc;
         margin-top: -3px;
     }
     .timezone-input {
         margin-top: -4px;
         height: 34px;
         display: inline-block;
         vertical-align: 6%;
     }
 }
 .vue-datetime-picker input, .vue-datetime-picker select {
     display: inline-block;
 }

 .vue-datetime-picker .date-input {
     width: 100px;
 }
 .vue-datetime-picker .minute-input {
     width: 60px;
 }

 .vue-datetime-picker .time-input {
     width: 70px;
 }
 .vue-datetime-picker .timezone-input {
     width: 140px;     
 }
 
</style>

<template>
    <div class="vue-datetime-picker datetime-picker-vue">
        <input class="form-control date-input" type="text" v-model="date" name="datepicker">
        <select class="form-control time-input" v-model="hour" class="form-control" name="hour" min="0">
            <option value="0">12:00AM</option>
            <option value="0">12:30AM</option>
            <option value="1">1:00AM</option>
            <option value="1">1:30AM</option>
            <option value="2">2:00am</option>
            <option value="2">2:30am</option>
            <option value="3">3:00am</option>
            <option value="3">3:30am</option>
            <option value="4">4:00am</option>
            <option value="4">4:30am</option>
            <option value="5">5:00am</option>
            <option value="5">5:30am</option>
            <option value="6">6:00am</option>
            <option value="6">6:30am</option>
            <option value="7">7:00am</option>
            <option value="7">7:30am</option>
            <option value="8">8:00am</option>
            <option value="8">8:30am</option>
            <option value="9">9:00am</option>
            <option value="9">9:30am</option>
            <option value="10">10:00am</option>
            <option value="10">10:30am</option>
            <option value="11">11:00am</option>
            <option value="11">11:30am</option>
            <option value="12">12:00pm</option>
            <option value="12">12:30pm</option>
            <option value="13">1:00pm</option>
            <option value="13">1:30pm</option>
            <option value="14">2:00pm</option>
            <option value="14">2:30pm</option>
            <option value="15">3:00pm</option>
            <option value="15">3:30pm</option>
            <option value="16">4:00pm</option>
            <option value="16">4:30pm</option>
            <option value="17">5:00pm</option>
            <option value="17">5:30pm</option>
            <option value="18">6:00pm</option>
            <option value="18">6:30pm</option>
            <option value="19">7:00pm</option>
            <option value="19">7:30pm</option>
            <option value="20">8:00pm</option>
            <option value="20">8:30pm</option>
            <option value="21">9:00pm</option>
            <option value="21">9:30pm</option>
            <option value="22">10:00pm</option>
            <option value="22">10:30pm</option>
            <option value="23">11:00pm</option>
            <option value="23">11:30pm</option>
        </select> :
        <select class="timezone-input form-control" v-model="timezone" name="timezone">
            <option v-for="name in timezoneNames">{{ name }}</option>
        </select>
    </div>
</template>


<script>
 module.exports = {
     props: {
         value: {
             twoWay: true
         }
     },
     compiled: function() {
         var self = this;
         var tagsPattern = new RegExp('\\d+:\\d\\d(am|pm|AM|PM)');
         self.tagsPattern = tagsPattern;
         
         var self = this;
         self.pikaday = new Pikaday({
             field: $(self.$el).find('[name="datepicker"]').get(0),
             onSelect: function(date) {
                 console.log('new date picked', self.pikaday.toString());
             }             
         });
         $(this.$el)
             .find('select.time-input')
               .select2({
                   tags: true,
                   createTag: function (params) {
                       // Don't offset to create a tag if there is no @ symbol
                       if (!tagsPattern.test(params.term)) {
                           // Return null to disable tag creation
                           return null;
                       }
                       return {
                           id: params.term,
                           text: params.term
                       }
                   }
               })
               .on('change', function() {
                   // Not sure why this doesn't automatically update
                   self.value = $(this).val();
                   if (self.change) {
                       self.change($(this).val());
                   }
               })
             ;

     },
     data: function() {
         var self = this;
         var tz = moment.tz.guess();
         var mils = self.value;
         var m;
         // TODO: this should be explicit
         if (mils > 0 && mils < 2467378540) {
             mils = mils * 1000;
         }
         var m = moment(mils || new Date().getTime()).tz(tz);
         return {
             date: m.format('YYYY-MM-DD'),
             hour: m.hour(),
             minute: m.minute(),
             timezone: tz,
             timezoneNames: moment.tz.names()
         };
     },
     methods: {
         changed: function(a, b) {
             var self = this;
             var datestamp = self.date + ' ' + self.hour + ':' + self.minute;
             console.log('datetime changed ', datestamp, a, b);
             var m = moment.tz(datestamp, "YYYY-MM-DD H:m", self.timezone);
             console.log('New datetime: ', m.format(), m, m.valueOf());
             this.value = m.valueOf() / 1000;
         }
     },
     watch: {
         'hour': 'changed',
         'minute': 'changed',
         'timezone': 'changed',
         'date': 'changed'
     }
     
 };

</script>
