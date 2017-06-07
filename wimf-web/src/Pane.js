// @flow
import React from "react";
import { css, merge } from "glamor";

type PaneProps = {
  children?: any,
  border?: boolean,
  style?: {
    [string]: string
  }
};

const paneStyle = css({
  padding: "1rem"
});

const withBorder = merge(paneStyle, {
  border: "1px solid gray",
  borderRadius: "3px"
});

export default function Pane(props: PaneProps) {
  const { border, style, children } = props;
  return (
    <div {...(border ? withBorder : paneStyle)} style={style}>
      {children}
    </div>
  );
}
