<template>
    <div class="container whole-page dashboard">
        <div class="dashboard-comp search-results">
            <vue-sorters
                :items="searchResults"
                :sortBy="sortBy"
                @sorted="onSorted"
            >
            </vue-sorters>
            <div v-for="(result, idx) in searchResults" :key="idx">
                <a :href="result.link" target="_blank" class="link-to-og">
                    <div class="result-info-container">
                        <div class="result-info-item">{{ result.item }}</div>
                        <div class="left-info">
                            <div class="result-info-type">
                                {{ result.instrumentType }}
                            </div>
                            <div v-if="result.price" class="result-info-price">
                                {{ result.price }} <span>rsd</span>
                            </div>
                            <div v-else>Call for price</div>
                        </div>
                    </div>
                </a>
            </div>
        </div>

        <div class="dashboard-comp search-form">
            <form class="form-wrapper">
                <div class="control-wrapper">
                    <span class="input-label">Product type* </span>
                    <select
                        class="control transition-ease"
                        v-model="searchDTO.type"
                    >
                        <option
                            v-for="(type, idx) in productTypes"
                            :key="idx"
                            :value="type"
                        >
                            {{ type }}
                        </option>
                    </select>
                </div>
                <div class="control-wrapper">
                    <span class="input-label">Keyword* </span>
                    <input
                        ref="username"
                        class="control transition-ease"
                        v-model="searchDTO.keyword"
                        type="text"
                        placeholder="Enter a keyword"
                    />
                </div>
                <div>
                    <div class="control-wrapper">
                        <span class="input-label">From* </span>
                        <input
                            class="control transition-ease"
                            v-model="searchDTO.fromPrice"
                            type="number"
                            min="1"
                            step="1"
                            placeholder="Enter base price"
                        />
                    </div>
                    <div class="control-wrapper">
                        <span class="input-label">To* </span>
                        <input
                            class="control transition-ease"
                            v-model="searchDTO.toPrice"
                            type="number"
                            min="1"
                            step="1"
                            placeholder="Enter top price"
                        />
                    </div>
                </div>

                <div class="submit-container">
                    <input
                        class="submit-button clickable primary-comp transition-ease-in"
                        type="submit"
                        @click.prevent="sendMessage"
                    />
                </div>
            </form>
        </div>
    </div>
</template>

<script>
import { getWSRootPath } from "@/services/paths.js";
import agentService from "@/services/agent-service.js";
import messageService from "@/services/message-service";

import VueSorters from "@/components/VueSorters.vue";

export default {
    components: {
        VueSorters,
    },
    data() {
        return {
            localHost: process.env.VUE_APP_HOST,
            searchDTO: {
                type: "ELECTRIC_GUITAR",
                keyword: "",
                fromPrice: 0,
                toPrice: 1000000,
            },
            productTypes: [
                "ELECTRIC_GUITAR",
                "ACOUSTIC_GUITAR",
                "CLASSICAL_GUITAR",
                "BASS_GUITAR",
            ],
            agents: [],
            newMessage: {
                performative: "GET_SEARCHED",
                sender: {
                    name: "front",
                },
                recipients: [],
                content: "",
            },
            searchAgentTypes: ["MasterSearchAgent", "SlaveSearchAgent"],
            searchResults: [],

            sortBy: {
                item: "Name",
                price: "Price",
            },
        };
    },

    async mounted() {
        let socket = new WebSocket(`${getWSRootPath()}/agents`);
        socket.onopen = async () => {
            console.log(`RunningAgents socket is opened`);
            const response = await agentService.getRunningAgents();
            this.agents = await response.json();
            this.newMessage.recipients = this.agents.filter(
                (aid) =>
                    this.searchAgentTypes.some(
                        (type) => type === aid.type.name
                    ) && aid.host.alias === this.localHost
            );

            let searchSocket = new WebSocket(`${getWSRootPath()}/harvest`);
            searchSocket.onopen = async () => {
                console.log(`Harvest Agents socket is opened`);
                this.sendMessage();
            };
            searchSocket.onclose = () => {
                searchSocket = null;
                console.log("Harvest Agents socket is closed.");
            };
            searchSocket.onmessage = (message) => {
                console.log("On message Harvest");
                this.searchResults = [];
                this.searchResults = JSON.parse(message.data);
            };
        };
        socket.onclose = () => {
            socket = null;
            console.log("RunningAgents socket is closed.");
        };
        socket.onmessage = (message) => {
            console.log(this.agents);
            this.agents = [];
            this.agents = JSON.parse(message.data);
            console.log("AFTER ASIGNMENT");
            console.log(this.agents);
        };
    },

    methods: {
        sendMessage() {
            this.newMessage.content = bsonStringify(this.searchDTO);
            messageService.sendMessage(this.newMessage);
        },

        onSorted(value) {
            this.searchResults = value;
        },
    },
};

const bsonStringify = (obj) => JSON.stringify(obj).replaceAll(`"`, `'`);
</script>

<style scoped>
.container {
    background-color: #013a6b;
    background-image: -webkit-linear-gradient(
        -80deg,
        var(--background) 38%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -moz-linear-gradient(
        -80deg,
        var(--background) 38%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -ms-linear-gradient(
        -80deg,
        var(--background) 38%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: -o-linear-gradient(
        -80deg,
        var(--background) 38%,
        var(--primary-dark) 0%,
        var(--primary-comp-dark) 85%
    );
    background-image: linear-gradient(
        -80deg,
        var(--background) 38%,
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
    margin: 0 20px;
}

.search-results {
    flex: 60%;
    overflow-y: auto;
}

.search-form {
    flex: 28%;
    border: 3px solid var(--background-lighter);
    border-radius: 0.7em;
}

.form-wrapper {
    margin: 0 1.5em;
}

.input-label {
    margin-top: 1em;
    font-size: 1.12rem;
    color: var(--primary-comp);
}

.control {
    background: transparent;
    font-size: 1.2rem;
    color: var(--control-border-color);
}

.control:focus {
    border-color: var(--primary-comp);
}

.submit-container {
    margin-top: 3.8em;
    display: flex;
    justify-content: center;
    align-items: center;
}

.submit-button {
    width: 50%;
    font-size: 1.2rem;
    padding: 0.5em;
}

.result-info-container {
    background: var(--background);
    min-height: 60px;
    height: 60px;
    margin-bottom: 10px;

    display: flex;
    justify-content: space-between;
    padding: 0 0.7em;
    align-items: center;
    color: var(--control-border-color);
    border: 3px solid var(--background-lighter);
}

.link-to-og {
    text-decoration: none;
}

.left-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 30%;
}
</style>
