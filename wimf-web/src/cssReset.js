import { css } from "glamor";

css.global("body, h1, h2, h3, h4, h5, h6", {
  fontFamily: "SF UI Text,Roboto,Helvetica Neue,Helvetica,sans-serif"
});

css.global("body", {
  color: "#111111",
  backgroundColor: "#ffffff",
  lineHeight: 1.625,
  fontSize: "100%"
});

css.global("select", {
  fontSize: "0.875rem",
  padding: "0.5rem",
  border: "1px solid rgba(0,0,0,.125)",
  borderRadius: "3px",
  lineHeight: 1.75
});

css.global("html", {
  boxSizing: "border-box"
});

css.global("*, *:before, *:after", {
  boxSizing: "border-box"
});
