import { getHttpRootPath } from "./paths.js";

export default (resource) => ({
    executeRequest: async (path, method, data) =>
        fetch(`${getHttpRootPath()}/${resource}${path ? `/${path}` : ""}`, {
            method,
            headers: {
                "Content-Type": "application/json",
            },
            ...(method !== "GET" && { body: JSON.stringify(data) }),
        }),
});
