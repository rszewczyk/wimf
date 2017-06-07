// @flow
import React from "react";
import { css, select } from "glamor";

type FilterProps = {
  name: string,
  value: Array<string>,
  options: Array<string>,
  onChange: (e: Event) => void
};

const filterStyle = css(
  {
    display: "flex",
    flexDirection: "column",
    padding: "0.5rem",
    marginBotton: "1rem"
  },
  select(" label", {
    fontSize: ".875rem",
    fontWeight: 700,
    marginBottom: "0.5rem"
  }),
  select(" select", {
    height: "100px"
  })
);

export default function Filter(props: FilterProps) {
  const { name, value, options, onChange } = props;

  return (
    <div {...filterStyle}>
      <label>{name}</label>
      <select value={value} multiple onChange={onChange}>
        {options.map((t, i) =>
          <option key={i} value={t} children={t} title={t} />
        )}
      </select>
    </div>
  );
}
