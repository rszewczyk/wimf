// @flow
import ponyfill from "fetch-ponyfill";

const fetchInit = ponyfill();

export default fetchInit.fetch;
export const Request = fetchInit.Request;
export const Headers = fetchInit.Headers;
export const Response = fetchInit.Response;
