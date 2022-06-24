<template>
    <vue-navbar></vue-navbar>
    <router-view />
</template>

<script>
import VueNavbar from "@/components/VueNavbar.vue";

export default {
    components: {
        VueNavbar,
    },
};
</script>

<style src="@/styles/root.css"></style>
<style src="@/styles/swal.css"></style>
<style>
body {
    margin: 0;
    padding: 0;
    min-width: 100%;
    min-height: 100%;
}

#app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    min-width: 100%;
    min-height: 100%;
}
</style>
