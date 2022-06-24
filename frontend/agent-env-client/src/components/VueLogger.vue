<template>
    <div class="logger-container">
        <div class="logger-header">
            <div class="logger-title">Console</div>
            <button
                @click="clearConsole"
                class="logger-clear-button clickable primary transition-ease-in"
            >
                Clear
            </button>
        </div>
        <div class="logger-content">
            <div
                class="logger-message"
                v-for="(message, idx) in content"
                :key="idx"
            >
                &#9654; {{ message }}
            </div>
        </div>
    </div>
</template>

<script>
import { getWSRootPath } from "@/services/paths";
export default {
    data() {
        return {
            content: JSON.parse(localStorage.getItem("logger-content")) || [],
        };
    },
    created() {
        const wsPath = `${getWSRootPath()}/logger`;
        let socket = new WebSocket(wsPath);
        socket.onopen = () => {
            console.log(`Logger socket is opened`);
            if (this.content.length === 0) {
                this.content.push(`Connected to: ${wsPath}`);
            }
        };
        socket.onclose = () => {
            socket = null;
            console.log("Logger socket is closed.");
        };
        socket.onmessage = (message) => {
            this.content.push(message.data);
            localStorage.setItem(
                "logger-content",
                JSON.stringify(this.content)
            );
        };
    },
    methods: {
        clearConsole() {
            this.content = [` Connected to: ${getWSRootPath()}/logger`];
            localStorage.setItem(
                "logger-content",
                JSON.stringify(this.content)
            );
        },
    },
};
</script>

<style scoped>
.logger-container {
    text-align: left;
    color: var(--control-border-color);
    padding: 0 1em;

    display: flex;
    flex-direction: column;

    min-height: 100%;
    height: 100%;
    box-sizing: border-box;
}

.logger-header {
    flex: 3%;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    padding-bottom: 0.5rem;
}

.logger-title {
    font-size: 1.25rem;
}

.logger-clear-button {
    padding: 0.35em 1.25em;
    margin-bottom: 3px;
    border-radius: 0.25em;
}

.logger-content {
    border: 3px solid var(--background-lighter);
    padding: 0.5em 0.2em;
    border-radius: 0.25em;
    min-width: 100%;
    max-width: 100%;
    overflow-y: auto;
    word-wrap: break-word;
    flex: 88%;
    font-family: "Lucida Console", "Courier New", monospace;
    font-size: 0.96rem;
    line-height: 1rem;
}

.logger-message {
    margin-bottom: 5px;
}
</style>
