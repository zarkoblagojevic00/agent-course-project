<template>
    <div class="agent-explorer-container">
        <div class="agent-explorer-header">
            <div class="agent-explorer-title">Agent explorer</div>
        </div>
        <div class="ae-container">
            <form class="form-wrapper">
                <div class="control-wrapper">
                    <span class="input-label">Performative* </span>
                    <select
                        class="control transition-ease"
                        v-model="newMessage.performative"
                    >
                        <option
                            class="performative-option"
                            v-for="(performative, idx) in performatives"
                            :key="idx"
                            :value="performative"
                            :disabled="
                                isInPerformatives(
                                    performative,
                                    internalPerformatives
                                )
                            "
                        >
                            {{ performative }}
                        </option>
                    </select>
                </div>
                <div
                    v-if="
                        isInPerformatives(
                            newMessage.performative,
                            chatMasterPerformatives
                        )
                    "
                >
                    <div class="control-wrapper">
                        <span class="input-label">Username* </span>
                        <input
                            ref="username"
                            class="control transition-ease"
                            v-model="credentials.username"
                            type="text"
                            placeholder="Enter your username"
                        />
                    </div>
                    <div
                        v-if="newMessage.performative !== 'LOG_OUT'"
                        class="control-wrapper"
                    >
                        <span class="input-label">Password* </span>
                        <input
                            class="control transition-ease"
                            v-model="credentials.password"
                            type="password"
                            placeholder="Enter your password"
                        />
                    </div>
                </div>

                <div
                    v-if="
                        isInPerformatives(
                            newMessage.performative,
                            agentToAgentPerformatives
                        )
                    "
                >
                    <div class="control-wrapper">
                        <span class="input-label">Sender* </span>
                        <select
                            class="control transition-ease"
                            v-model="newMessage.recipients[0]"
                        >
                            <option
                                class="performative-option"
                                v-for="(agent, idx) in userAgents"
                                :key="idx"
                                :value="agent"
                                :disabled="
                                    agent.host.alias !== localHost ||
                                    agent.name === oneRecipient.name
                                "
                            >
                                {{ agent.name }}@{{ agent.host.alias }}
                            </option>
                        </select>
                    </div>
                    <div
                        v-if="newMessage.performative === 'SEND_MESSAGE_USER'"
                        class="control-wrapper"
                    >
                        <span class="input-label">Recipient* </span>
                        <select
                            class="control transition-ease"
                            v-model="oneRecipient"
                        >
                            <option
                                class="performative-option"
                                v-for="(agent, idx) in userAgents"
                                :key="idx"
                                :value="agent"
                                :disabled="
                                    agent.name === newMessage.recipients[0].name
                                "
                            >
                                {{ agent.name }} @{{ agent.host.alias }}
                            </option>
                        </select>
                    </div>

                    <div>
                        <textarea
                            @keydown.enter.exact.prevent="sendMessage"
                            ref="messageSubject"
                            class="chat-textarea chat-subject-textarea control"
                            name="chat-message-subject"
                            placeholder="Enter subject"
                            v-model="newAgentMessage.subject"
                        ></textarea>
                        <textarea
                            @keydown.enter.exact.prevent="sendMessage"
                            class="chat-textarea chat-content-textarea control"
                            name="chat-message-content"
                            placeholder="Type away..."
                            v-model="newAgentMessage.content"
                        ></textarea>
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
import messageService from "@/services/message-service.js";
import agentService from "@/services/agent-service.js";
import { getWSRootPath } from "@/services/paths.js";

export default {
    data() {
        return {
            localHost: process.env.VUE_APP_HOST,
            performatives: [],
            credentials: {
                username: "",
                password: "",
            },
            oneRecipient: {},
            agents: [],
            internalPerformatives: [
                // "REGISTER",
                // "LOG_IN",
                // "LOG_OUT",
                // "GET_REGISTERED",
                // "GET_LOGGED_IN",

                // // user agents
                // "SEND_MESSAGE_ALL",
                // "SEND_MESSAGE_USER",
                "RECEIVE_MESSAGE",
                "GET_ALL_MESSAGES",
                "GET_ALL_CHAT_MESSAGES",
                "GET_USER_CHAT_MESSAGES",

                "OTHER_USER_LOGIN",
                "OTHER_USER_REGISTER",
                "OTHER_USER_LOGOUT",

                "RECEIVE_LOGGED_IN_FROM_MASTER_NODE",
                "RECEIVE_REGISTERED_FROM_MASTER_NODE",

                "REGISTER_FROM_OTHER_NODE",
                "LOG_IN_FROM_OTHER_NODE",
                "LOG_OUT_FROM_OTHER_NODE",
            ],
            chatMasterPerformatives: ["REGISTER", "LOG_IN", "LOG_OUT"],
            submitOnlyPerfomatives: [
                "GET_REGISTERED",
                "GET_LOGGED_IN",
                "GET_ALL_MESSAGES",
            ],
            agentToAgentPerformatives: [
                "SEND_MESSAGE_ALL",
                "SEND_MESSAGE_USER",
            ],

            newMessage: {
                performative: "",
                sender: {},
                recipients: [],
                content: "",
            },
            newAgentMessage: {
                sender: "",
                recipient: "",
                subject: "",
                content: "",
            },
        };
    },
    async created() {
        this.performatives = await (
            await messageService.getPerformatives()
        ).json();
    },

    async mounted() {
        const wsPath = `${getWSRootPath()}/agents`;
        let socket = new WebSocket(wsPath);
        socket.onopen = async () => {
            console.log(`RunningAgents socket is opened`);
            const response = await agentService.getRunningAgents();
            this.agents = await response.json();
            this.newMessage.performative = "REGISTER";
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

    computed: {
        userAgents() {
            return this.agents.filter((a) => a.type.name === "UserAgent");
        },
    },

    methods: {
        isInPerformatives(performative, list) {
            return list.some((internal) => internal === performative);
        },
        async sendMessage() {
            if (
                this.isInPerformatives(
                    this.newMessage.performative,
                    this.chatMasterPerformatives
                )
            ) {
                this.newMessage.content =
                    this.newMessage.performative === "LOG_OUT"
                        ? this.credentials.username
                        : bsonStringify(this.credentials);
            } else if (
                this.isInPerformatives(
                    this.newMessage.performative,
                    this.agentToAgentPerformatives
                )
            ) {
                this.newAgentMessage.sender =
                    this.newMessage.recipients[0].name;
                this.newAgentMessage.recipient =
                    this.newMessage.performative === "SEND_MESSAGE_ALL"
                        ? "all"
                        : this.oneRecipient.name;
                this.newMessage.content = bsonStringify(this.newAgentMessage);
            }
            await messageService.sendMessage(this.newMessage);
            console.log(this.newMessage);
            this.newMessage.content = "";
        },
    },

    watch: {
        "newMessage.performative": {
            handler(value) {
                if (
                    this.isInPerformatives(
                        value,
                        this.chatMasterPerformatives
                    ) ||
                    this.isInPerformatives(value, this.submitOnlyPerfomatives)
                ) {
                    this.newMessage.recipients = this.agents.filter(
                        (a) =>
                            a.type.name === "ChatMasterAgent" &&
                            a.host.alias === this.localHost
                    );
                }
            },
            immediate: true,
        },
    },
};

const bsonStringify = (obj) => JSON.stringify(obj).replaceAll(`"`, `'`);
</script>

<style scoped>
.agent-explorer-container {
    text-align: left;
    color: var(--control-border-color);

    display: flex;
    flex-direction: column;

    height: 100%;
    box-sizing: border-box;
}

.agent-explorer-header {
    flex: 3%;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    padding-bottom: 0.5rem;
}

.agent-explorer-title {
    font-size: 1.25rem;
    padding: 0 0.5em;
}

.ae-container {
    background: var(--background);
    flex: 88%;
    border: 3px solid var(--background-lighter);
    border-radius: 0.25em;
}

.form-wrapper {
    margin: 0 1.5em;
}

.input-label {
    margin-top: 1em;
    font-size: 1.12rem;
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
    margin-top: 0.8em;
    display: flex;
    justify-content: center;
    align-items: center;
}

.submit-button {
    width: 50%;
    font-size: 1.2rem;
    padding: 0.5em;
}

.chat-textarea {
    resize: none;
}

.chat-subject-textarea {
    margin-top: 1.5em;
    height: 45px;
}

.chat-content-textarea {
    margin-top: 0.5em;
    height: 115px;
}
</style>
