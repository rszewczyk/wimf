// @flow
import React from "react";
import { css } from "glamor";

type PaneProps = {
  children?: any
};

const paneStyle = css({
  paddingLeft: "0.5rem",
  paddingRight: "0.5rem"
});

export default function Pane(props: PaneProps) {
  return (
    <div {...paneStyle}>
      {props.children}
    </div>
  );
}
