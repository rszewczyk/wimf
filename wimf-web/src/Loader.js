// @flow
import React from "react";
import { css, merge } from "glamor";
import Spinner from "./Spinner";

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
      <Spinner />
    </div>
  );
}
