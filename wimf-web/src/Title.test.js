import React from "react";
import renderer from "react-test-renderer";
import Title from "./Title";

it("renders correctly", () => {
  const withoutDescription = renderer
    .create(<Title title="some title" />)
    .toJSON();
  expect(withoutDescription).toMatchSnapshot();

  const withDescription = renderer
    .create(<Title title="some title" description="some description" />)
    .toJSON();
  expect(withDescription).toMatchSnapshot();
});
