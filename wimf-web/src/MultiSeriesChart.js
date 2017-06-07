// @flow
import React, { Component } from "react";
import {
  ResponsiveContainer,
  LineChart,
  BarChart,
  Line,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from "recharts";
import Pane from "./Pane";
import NoResults from "./NoResults";
import Title from "./Title";

const colors = [
  "#8884d8",
  "#82ca9d",
  "#ffc658",
  "#83a6ed",
  "#8dd1e1",
  "#a4de6c",
  "#d0ed57"
];

type ComboChartProps = {
  data: Array<Object>,
  type: "line" | "bar" | "barStacked",
  children?: any,
  title: string,
  description?: string
};

export default class ComboChart extends Component {
  props: ComboChartProps;

  render() {
    const { data, type, children, title, description } = this.props;
    const { value, ...first } = data[0] || {};
    const dataKeys = Object.keys(first);

    const hasData = dataKeys.length > 0;

    const ChartComp = type === "line" ? LineChart : BarChart;

    return (
      <Pane border marginY={1}>
        <Title title={title} description={description} />
        {hasData
          ? <ResponsiveContainer width="98%" height={300}>
              <ChartComp data={data}>
                <YAxis />
                <XAxis dataKey="value" />
                <Tooltip />
                <Legend />
                {type === "line" && <CartesianGrid strokeDasharray="3 3" />}
                {type === "line"
                  ? dataKeys.map((k, i) =>
                      <Line
                        type="monotone"
                        key={i}
                        dataKey={k}
                        stroke={colors[i]}
                      />
                    )
                  : dataKeys.map((k, i) =>
                      <Bar
                        stackId={type === "barStacked" ? "value" : null}
                        key={i}
                        dataKey={k}
                        fill={colors[i]}
                      />
                    )}
                {children}
              </ChartComp>
            </ResponsiveContainer>
          : <NoResults />}
      </Pane>
    );
  }
}
