import fetchProxy from "./fetch-proxy.js";

const httpProxy = fetchProxy("agents");

export default {
    getRunningAgents: async () => httpProxy.executeRequest("running", "GET"),
    getAgentClasses: async () => httpProxy.executeRequest("classes", "GET"),
};
