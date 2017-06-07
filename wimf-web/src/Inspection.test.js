import React from "react";
import renderer from "react-test-renderer";
import Inspection from "./Inspection";

it("renders correctly", () => {
  expect(
    renderer
      .create(
        <Inspection
          businessName="foo.bar.org"
          violationDescription="ew"
          inspectionDate="1/2/2016"
        />
      )
      .toJSON()
  ).toMatchSnapshot();
});
