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
    border: "4px solid #fff",
    borderBottomColor: "transparent",
    height: "52px",
    width: "52px",
    background: "transparent !important",
    display: "inline-block",
    animation: `${rotate} 0.75s 0s linear infinite`
  }
});

const overlayStyle = css({
  position: "fixed",
  top: 0,
  left: 0,
  bottom: 0,
  right: 0,
  background: "rgba(0,0,0,.8)",
  display: "none",
  zIndex: 999
});

const overlayVisibleStyle = merge(overlayStyle, {
  display: "flex",
  alignItems: "center",
  justifyContent: "center"
});

type LoaderProps = {
  visible: boolean
};

export default function Loader(props: LoaderProps) {
  return (
    <div {...(props.visible ? overlayVisibleStyle : overlayStyle)}>
      <div {...ballStyle}>
        <div />
      </div>
    </div>
  );
}
