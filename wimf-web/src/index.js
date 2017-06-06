// @flow
import React from "react";
import ReactDOM from "react-dom";
import "glamor-reset";
import { plugins } from "glamor";
import App from "./App";
import wrapWithFetcher from "./fetcher";

plugins.add("glamor-autoprefixer");

const FetchApp = wrapWithFetcher()(App);

ReactDOM.render(
  <FetchApp initialRequest="/api/summary" />,
  document.getElementById("root")
);
