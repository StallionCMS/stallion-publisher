<style>
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
    <div class="vue-datetime-picker">
        <input class="form-control date-input" type="text" v-model="date" name="datepicker">
        <select class="form-control time-input" v-model="hour" class="form-control" name="hour" min="0">
            <option value="0">12 AM</option>
            <option value="1">1 AM</option>
            <option value="2">2 AM</option>
            <option value="3">3 AM</option>
            <option value="4">4 AM</option>
            <option value="5">5 AM</option>
            <option value="6">6 AM</option>
            <option value="7">7 AM</option>
            <option value="8">8 AM</option>
            <option value="9">9 AM</option>
            <option value="10">10 AM</option>
            <option value="11">11 AM</option>
            <option value="12">12 PM</option>
            <option value="13">1 PM</option>
            <option value="14">2 PM</option>
            <option value="15">3 PM</option>
            <option value="16">4 PM</option>
            <option value="17">5 PM</option>
            <option value="18">6 PM</option>
            <option value="19">7 PM</option>
            <option value="20">8 PM</option>
            <option value="21">9 PM</option>
            <option value="22">10 PM</option>
            <option value="23">11 PM</option>
        </select> :
        <input class="minute-input form-control" v-model="minute" type="number" size="2"  max="59" step="1">
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
         self.pikaday = new Pikaday({
             field: $(self.$el).find('[name="datepicker"]').get(0),
             onSelect: function(date) {
                 console.log('new date picked', self.pikaday.toString());
             }             
         });

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
