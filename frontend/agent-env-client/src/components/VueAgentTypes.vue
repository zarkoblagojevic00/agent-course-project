<template>
    <div class="agent-types-container">
        <div class="agent-types-header">
            <div class="agent-types-title">Agent Types</div>
        </div>
        <div class="at-container">
            <div
                v-for="(type, idx) in agentTypes"
                :key="idx"
                class="one-agent-type underline-container"
            >
                {{ type.name }}
            </div>
        </div>
    </div>
</template>

<script>
import { getWSRootPath } from "@/services/paths.js";
import agentService from "@/services/agent-service.js";

export default {
    data() {
        return {
            agentTypes: [],
        };
    },
    created() {
        const wsPath = `${getWSRootPath()}/types`;
        let socket = new WebSocket(wsPath);
        socket.onopen = async () => {
            console.log(`AgentTypes socket is opened`);
            const response = await agentService.getAgentClasses();
            this.agentTypes = await response.json();
        };
        socket.onclose = () => {
            socket = null;
            console.log("AgentTypes socket is closed.");
        };
        socket.onmessage = (message) => {
            this.agentTypes = JSON.parse(message.data);
        };
    },
};
</script>

<style scoped>
.agent-types-container {
    text-align: left;
    color: var(--control-border-color);

    display: flex;
    flex-direction: column;

    height: 100%;
    box-sizing: border-box;
}

.agent-types-header {
    flex: 3%;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    padding-bottom: 0.5rem;
}

.agent-types-title {
    font-size: 1.25rem;
    padding: 0 0.5em;
}

.at-container {
    background: var(--background);
    flex: 88%;
    border: 3px solid var(--background-lighter);
    border-radius: 0.25em;
}

.one-agent-type:hover {
    background-color: #213349;
}

.one-agent-type {
    min-height: 60px;
    border-bottom: 2px solid var(--background-lighter);

    display: flex;
    align-items: center;
    font-size: 0.95rem;
    padding: 0 1em;
}
</style>
