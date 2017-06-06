// @flow
import React from "react";
import ReactDOM from "react-dom";
import "glamor-reset";
import { plugins } from "glamor";
import App from "./App";

plugins.add("glamor-autoprefixer");

ReactDOM.render(
  <App initialRequest="/api/summary" />,
  document.getElementById("root")
);
