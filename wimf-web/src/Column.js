// @flow
import React from "react";
import { css } from "glamor";

type Width = 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8;

type ColumnProps = {
  children: any,
  width: Width
};

const columnStyle = css({
  display: "flex",
  flexDirection: "column"
});

function getWidth(w: Width) {
  return `${12.45 * w}%`;
}

export default function Column(props: ColumnProps) {
  return (
    <div
      {...columnStyle}
      style={{ width: props.width ? getWidth(props.width) : getWidth(2) }}
    >
      {props.children}
    </div>
  );
}
