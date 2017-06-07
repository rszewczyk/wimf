import React from "react";
import renderer from "react-test-renderer";
import NoResults from "./NoResults";

it("renders correctly", () => {
  expect(renderer.create(<NoResults />).toJSON()).toMatchSnapshot();
});
