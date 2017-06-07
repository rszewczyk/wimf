// @flow
import React from "react";
import ReactDOM from "react-dom";
import { plugins } from "glamor";
import prefixer from "glamor-autoprefixer";
import App from "./App";

plugins.add(prefixer);

ReactDOM.render(
  <App initialRequest="/api/summary" />,
  document.getElementById("root")
);
