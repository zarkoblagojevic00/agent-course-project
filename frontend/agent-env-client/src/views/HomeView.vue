<template>
    <div class="container whole-page dashboard">
        <div class="dashboard-comp agent-types">
            <vue-agent-types></vue-agent-types>
        </div>
        <div class="dashboard-comp running-agents">
            <vue-agent-explorer></vue-agent-explorer>
        </div>
        <div class="dashboard-comp logger">
            <vue-logger></vue-logger>
        </div>
    </div>
</template>

<script>
import VueLogger from "@/components/VueLogger.vue";
import VueAgentExplorer from "@/components/VueAgentExplorer.vue";
import VueAgentTypes from "@/components/VueAgentTypes.vue";
export default {
    name: "HomeView",
    components: {
        VueLogger,
        VueAgentTypes,
        VueAgentExplorer,
    },
};
</script>

<style scoped>
.container {
    background-color: #013a6b;
    background-image: -webkit-linear-gradient(
        -80deg,
        var(--background) 44%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -moz-linear-gradient(
        -80deg,
        var(--background) 44%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -ms-linear-gradient(
        -80deg,
        var(--background) 44%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -o-linear-gradient(
        -80deg,
        var(--background) 44%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: linear-gradient(
        -80deg,
        var(--background) 40%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
}

.dashboard {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dashboard-comp {
    height: 90%;
    min-height: 90%;
}

.agent-types {
    flex: 12%;
    margin-left: 20px;
}

.running-agents {
    flex: 32%;
    margin: 0 20px;
}

.logger {
    flex: 27%;
    margin-right: 20px;
}
</style>
