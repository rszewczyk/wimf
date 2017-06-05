// @flow
import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import wrapWithFetcher from "./fetcher";

const FetchApp = wrapWithFetcher()(App);

ReactDOM.render(
  <FetchApp initialRequest="/api/summary" />,
  document.getElementById("root")
);
