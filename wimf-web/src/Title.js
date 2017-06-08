// @flow
import React from "react";
import { css } from "glamor";

type Size = 1 | 2 | 3 | 4 | 5 | 6;

type TitleProps = {
  title: string,
  description?: string,
  size: Size
};

const titleStyle = css({
  marginBottom: "1.0rem",
  display: "flex",
  flexDirection: "column",
  " h1, h2, h3, h4, h5, h6": {
    display: "inline-block"
  }
});

export default function(props: TitleProps) {
  const { title, description, size } = props;

  return (
    <div {...titleStyle}>
      {React.createElement(`h${size || 3}`, null, title)}
      {description && <div>{description}</div>}
    </div>
  );
}
