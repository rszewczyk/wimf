// @flow
import React from "react";
import { css } from "glamor";

type TitleProps = {
  title: string,
  description?: string
};

const titleStyle = css({
  marginBottom: "1.0rem"
});

export default function(props: TitleProps) {
  const { title, description } = props;
  return (
    <div {...titleStyle}>
      <h3>{title}</h3>
      {description && <div>{description}</div>}
    </div>
  );
}