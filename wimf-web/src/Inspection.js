// @flow
import React from "react";
import { css } from "glamor";

type InspectionProps = {
  businessName: string,
  violationDescription: string,
  inspectionDate: string
};

const inspectionStyle = css({
  borderBottom: "1px solid gray",
  overflow: "hidden",
  fontSize: "0.875rem",
  "> div h5 ": {
    display: "inline-block",
    marginBottom: "0.5rem",
    marginRight: "1.0rem"
  }
});

export default function Inspection(props: InspectionProps) {
  const { businessName, violationDescription, inspectionDate } = props;

  return (
    <div {...inspectionStyle}>
      <div>
        <h5>{businessName}</h5>
        <span>{inspectionDate}</span>
      </div>
      <p>{violationDescription}</p>
    </div>
  );
}
