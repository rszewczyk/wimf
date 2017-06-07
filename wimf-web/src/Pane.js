// @flow
import React from "react";
import { css, merge } from "glamor";

type WhiteSpace = 1 | 2 | 3 | 4 | 5;

type PaneProps = {
  children?: any,
  border?: boolean,
  marginY?: WhiteSpace,
  marginX?: WhiteSpace
};

const paneStyle = css({
  padding: "1rem"
});

const withBorder = merge(paneStyle, {
  border: "1px solid gray",
  borderRadius: "3px"
});

const margins = ["0.5rem", "0.875rem", "1.0rem", "2.0rem", "4.0rem"];

export default function Pane(props: PaneProps) {
  const { border, marginX, marginY, children } = props;
  return (
    <div
      {...(border ? withBorder : paneStyle)}
      style={{
        marginTop: marginY ? margins[marginY] : 0,
        marginBottom: marginY ? margins[marginY] : 0,
        marginRight: marginX ? margins[marginX] : 0,
        marginLeft: marginX ? margins[marginX] : 0
      }}
    >
      {children}
    </div>
  );
}
