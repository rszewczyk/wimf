import React from "react";
import renderer from "react-test-renderer";
import Pane from "./Pane";

it("renders correctly", () => {
  const borderless = renderer
    .create(
      <Pane>
        <h1>hello!</h1>
        <p>foo</p>
      </Pane>
    )
    .toJSON();
  expect(borderless).toMatchSnapshot();

  const withBorder = renderer
    .create(
      <Pane border>
        <h1>hello!</h1>
        <p>foo</p>
      </Pane>
    )
    .toJSON();
  expect(withBorder).toMatchSnapshot();

  const withMargins = renderer
    .create(
      <Pane border marginX={1} marginY={2}>
        <h1>hello!</h1>
        <p>foo</p>
      </Pane>
    )
    .toJSON();
  expect(withMargins).toMatchSnapshot();
});
