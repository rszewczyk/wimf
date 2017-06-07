import React from "react";
import renderer from "react-test-renderer";
import Button from "./Button";

it("renders correctly", () => {
  expect(renderer.create(<Button>Submit</Button>).toJSON()).toMatchSnapshot();
});
