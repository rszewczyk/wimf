// @flow
import React from "react";
import { css, merge } from "glamor";

const rotate = css.keyframes({
  "0%": { transform: "rotate(0deg)" },
  "50%": { transform: "rotate(180deg)" },
  "100%": { transform: "rotate(360deg)" }
});

const ballStyle = css({
  "> div": {
    backgroundColor: "#fff",
    borderRadius: "100%",
    margin: "2px",
    animationFillMode: "both",
    border: "4px solid",
    borderColor: "#fff",
    borderBottomColor: "transparent",
    height: "52px",
    width: "52px",
    background: "transparent !important",
    display: "inline-block",
    animation: `${rotate} 0.75s 0s linear infinite`
  }
});

const smallStyle = merge(ballStyle, {
  "> div": {
    border: "2px solid",
    borderColor: "#fff",
    height: "20px",
    width: "20px"
  }
});

type BallProps = {
  small?: boolean,
  style?: { [string]: string }
};

export default function Ball({ small, style }: BallProps) {
  return (
    <div {...(small ? smallStyle : ballStyle)} style={style}>
      <div />
    </div>
  );
}
