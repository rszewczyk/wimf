// @flow
import React from "react";
import { css } from "glamor";

type PageProps = {
  children: any
};

const pageStyle = css({
  display: "flex",
  paddingBottom: "2rem"
});

export default function Page(props: PageProps) {
  return (
    <div {...pageStyle}>
      {props.children}
    </div>
  );
}
