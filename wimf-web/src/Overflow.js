// @flow
import React from "react";
import { css } from "glamor";

type OverflowProps = {
  children: any
};

const overflowStyle = css({
  overflowY: "scroll",
  width: "100%",
  paddingBottom: "2rem"
});

export default function Overflow(props: OverflowProps) {
  return (
    <div {...overflowStyle}>
      {props.children}
    </div>
  );
}
