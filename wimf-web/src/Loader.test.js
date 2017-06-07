import React from "react";
import renderer from "react-test-renderer";
import Loader from "./Loader";

it("renders correctly", () => {
  expect(renderer.create(<Loader />).toJSON()).toMatchSnapshot();
});
