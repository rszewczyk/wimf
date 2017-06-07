// @flow
import React from "react";
import { css } from "glamor";

const noResultsStyle = css({
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  height: "280px",
  margin: "1rem 2rem 1rem 1rem",
  backgroundColor: "#F0F0F0",
  "> div": {
    margin: "1rem"
  }
});

export default function NoResults() {
  return (
    <div {...noResultsStyle}>
      <div>
        No Results. Do you have a filter applied? Try widening your criteria.
      </div>
    </div>
  );
}
