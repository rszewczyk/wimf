// @flow
import React from "react";
import { css } from "glamor";

type ButtonBarProps = {
  children: any
};

const barStyle = css({
  "> *": {
    marginLeft: "0.5rem",
    marginRight: "0.5rem"
  },
  padding: "0.5rem"
});

export default function ButtonBar(props: ButtonBarProps) {
  return (
    <div {...barStyle}>
      {props.children}
    </div>
  );
}
