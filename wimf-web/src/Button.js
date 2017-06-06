// @flow
import React from "react";
import { css, merge } from "glamor";

type ButtonProps = {
  onClick: Event => void,
  children: any,
  primary?: boolean
};

const buttonStyle = css({
  fontSize: ".875rem",
  fontWeight: 700,
  cursor: "pointer",
  display: "inline-block",
  lineHeight: "1.125rem",
  padding: ".5rem 1rem",
  margin: 0,
  border: "1px solid transparent",
  verticalAlign: "middle",
  borderRadius: "4px"
});

const buttonPrimaryStyle = merge(buttonStyle, {
  color: "#fff",
  backgroundColor: "#0074d9"
});

export default function Button(props: ButtonProps) {
  return (
    <button
      {...(props.primary ? buttonPrimaryStyle : buttonStyle)}
      onClick={props.onClick}
    >
      {props.children}
    </button>
  );
}
