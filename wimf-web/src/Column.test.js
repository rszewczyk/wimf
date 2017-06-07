import React from "react";
import renderer from "react-test-renderer";
import Column from "./Column";

it("renders correctly", () => {
  const widthOne = renderer
    .create(<Column width={1}><p>Hello</p></Column>)
    .toJSON();
  expect(widthOne).toMatchSnapshot();

  const widthThree = renderer
    .create(<Column width={3}><div>Foobar!!</div></Column>)
    .toJSON();
  expect(widthThree).toMatchSnapshot();
});
