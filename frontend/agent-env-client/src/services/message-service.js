import fetchProxy from "./fetch-proxy.js";

const httpProxy = fetchProxy("messages");

export default {
    getPerformatives: async () => httpProxy.executeRequest("", "GET"),
    sendMessage: async (newMessage) =>
        httpProxy.executeRequest("", "POST", newMessage),
};
