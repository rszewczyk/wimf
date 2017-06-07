import React from "react";
import renderer from "react-test-renderer";
import Page from "./Page";

it("renders correctly", () => {
  expect(
    renderer
      .create(
        <Page>
          <h1>hello!</h1>
          <p>foo</p>
        </Page>
      )
      .toJSON()
  ).toMatchSnapshot();
});
