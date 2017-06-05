// @flow
import React, { Component } from "react";
import {
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

const colors = ["#8884d8", "#82ca9d", "#ffc658"];

type ComboChartProps = {
  data: Array<Object>,
  type: "line" | "bar" | "barStacked"
};

export default class ComboChart extends Component {
  props: ComboChartProps;

  render() {
    const { data, type } = this.props;
    const { value, ...dataKeys } = data[0];

    const ChartComp = type === "line" ? LineChart : BarChart;
    return (
      <ChartComp width={800} height={300} data={data}>
        <YAxis />
        <XAxis dataKey="value" />
        <Tooltip />
        <Legend />
        {type === "line" && <CartesianGrid strokeDasharray="3 3" />}
        {type === "line"
          ? Object.keys(dataKeys).map((k, i) =>
              <Line type="monotone" key={i} dataKey={k} stroke={colors[i]} />
            )
          : Object.keys(dataKeys).map((k, i) =>
              <Bar
                stackId={type === "barStacked" ? "value" : null}
                key={i}
                dataKey={k}
                fill={colors[i]}
              />
            )}
      </ChartComp>
    );
  }
}
