<style lang="scss">
 .contact-details-vue {
     
 }
</style>

<template>
    <div class="contact-details-vue">
        <loading-div v-if="$loadingRouteData"></loading-div>
        <div v-if="!$loadingRouteData">
            <h2>{{ contact.displayName }}</h2>
            
        </div>
    </div>
</template>

<script>
 module.exports = {

 }
</script>
