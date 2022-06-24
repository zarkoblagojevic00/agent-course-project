<template>
    <div class="container">
        <div class="logo-container">
            <img alt="Agent logo" src="../assets/agent-logo-64.svg" />
            <div class="app-name gradient-text">Agent Demo</div>
        </div>
        <nav>
            <div>
                <router-link to="/">Home</router-link>
                <router-link to="/scraper">MusicShopScraper</router-link>
            </div>
        </nav>
    </div>
</template>

<script>
export default {
    name: "NavBar",
};
</script>

<style scoped>
.container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 10%;
    background: var(--background);
    border-bottom: 2px var(--primary-dark) solid;
}

.logo-container {
    display: flex;
    justify-content: flex-start;
    align-items: center;
}

.app-name {
    font-size: 1.8rem;
}

nav a {
    text-decoration: none;
    margin-left: 25px;
    font-size: 1.05rem;
    color: var(--control-border-color);
}

nav a.router-link-exact-active {
    color: #fc96e7;
    cursor: default;
}

@media screen and (min-device-width: 1200px) and (max-device-width: 1600px) and (-webkit-min-device-pixel-ratio: 1) {
    .container {
        padding: 5px 2%;
    }
}
</style>
