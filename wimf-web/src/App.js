// @flow
import React from "react";
import wrapWithFetcher from "./fetcher";
import { Request } from "./fetch";

const ServerHello = wrapWithFetcher()(props => <h1>{props.data}</h1>);

export default function App() {
  const req = new Request("/api/inspection");
  req.headers.set("Accept", "text/plain");

  return (
    <div>
      <p>
        This page tests integration with the services backend. If everything
        goes well you should see "Hello World" in bold below:
      </p>
      <ServerHello request={req} />
    </div>
  );
}
