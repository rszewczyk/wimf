import React from "react";
import renderer from "react-test-renderer";
import ButtonBar from "./ButtonBar";
import Button from "./Button";

it("renders correctly", () => {
  const oneBtn = renderer
    .create(<ButtonBar><Button>Submit</Button></ButtonBar>)
    .toJSON();
  expect(oneBtn).toMatchSnapshot();

  const threeBtn = renderer
    .create(
      <ButtonBar><Button>Submit</Button><Button>Cancel</Button></ButtonBar>
    )
    .toJSON();
  expect(threeBtn).toMatchSnapshot();
});
