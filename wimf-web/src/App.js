// @flow
import React, { Component } from "react";
import * as time from "d3-time";
import { Brush } from "recharts";
import MultiSeriesChart from "./MultiSeriesChart";
import type { FetcherComponentProps } from "./fetcher";

const months = [
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec"
];

type Term = {
  value: string,
  count: number
};

type Buckets = {
  [string]: Array<Term>
};

export function dateMonthCounts(
  buckets: Buckets,
  minStr: string,
  maxStr: string
) {
  const [min, max] = [new Date(minStr), new Date(maxStr)];
  const r = [time.timeMonth(min), ...time.timeMonths(min, max)];

  // initialize each bucket to zero
  const counts = {};
  const keys = Object.keys(buckets);
  r.forEach(d => {
    counts[d] = keys.reduce((acc, next) => ({ ...acc, [next]: 0 }), {
      value: `${months[d.getMonth()]} ${d.getFullYear()}`
    });
  });

  // for each date in each series - determine the bucket to which it belongs and
  // add its count to the bucket count
  keys.forEach(k =>
    buckets[k].forEach(b => {
      counts[time.timeMonth(new Date(b.value))][k] += b.count;
    })
  );

  return r.map(d => counts[d]);
}

export function termCounts(buckets: Buckets, terms: Array<string>) {
  const keys = Object.keys(buckets);
  const counts = {};
  terms.forEach(t => {
    counts[t] = keys.reduce((acc, next) => ({ ...acc, [next]: 0 }), {
      value: t
    });
  });

  keys.forEach(k => buckets[k].forEach(v => (counts[v.value][k] += v.count)));

  return terms
    .map(t => counts[t])
    .filter(({ value, ...agg }) => Object.keys(agg).some(k => agg[k] > 0));
}

export function createRequest(filters: Filters): string {
  const queryString = Object.keys(filters)
    .map(filterName =>
      filters[filterName]
        .map(val => `filter=${encodeURIComponent(`${filterName}=${val}`)}`)
        .join("&")
    )
    .filter(f => !!f)
    .join("&");

  return "/api/summary?" + queryString;
}

type Filters = {
  inspection_type: Array<string>,
  cuisine: Array<string>,
  boro: Array<string>
};

type AppState = {
  filters: Filters
};

export default class App extends Component {
  props: FetcherComponentProps;
  state: AppState = {
    filters: {
      inspection_type: [],
      cuisine: [],
      boro: []
    }
  };

  filterChange = (filter: string, e: any) => {
    this.setState({
      filters: {
        ...this.state.filters,
        [filter]: [...e.target.options]
          .filter(o => o.selected)
          .map(o => o.value)
      }
    });
  };

  clearFilters = (e: Event) => {
    e.preventDefault();
    const cleared = {
      filters: {
        inspection_type: [],
        cuisine: [],
        boro: []
      }
    };
    this.setState(cleared);
    this.props.fetch(createRequest(cleared.filters));
  };

  applyFilters = (e: Event) => {
    e.preventDefault();
    this.props.fetch(createRequest(this.state.filters));
  };

  render() {
    const { data, error, loading } = this.props;
    if (!data) {
      return loading ? <div>loading</div> : <div>no data!</div>;
    }
    if (this.props.error) {
      return <div children={this.props.error.message} />;
    }

    const {
      gradesByDate,
      gradesByBoro,
      gradesByCuisine,
      gradesByInspectionType,
      minDate,
      maxDate,
      terms
    } = this.props.data;

    const { filters } = this.state;

    return (
      <div>
        <div>
          <label>Boro</label>
          <select
            value={filters.boro}
            multiple
            onChange={this.filterChange.bind(null, "boro")}
          >
            {terms.boro.map((t, i) =>
              <option key={i} value={t} children={t} />
            )}
          </select>
          <label>Cuisine</label>
          <select
            value={filters.cuisine}
            multiple
            onChange={this.filterChange.bind(null, "cuisine")}
          >
            {terms.cuisine.map((t, i) =>
              <option key={i} value={t} children={t} />
            )}
          </select>
          <label>Inspection Type</label>
          <select
            value={filters.inspection_type}
            multiple
            onChange={this.filterChange.bind(null, "inspection_type")}
          >
            {terms.inspection_type.map((t, i) =>
              <option key={i} value={t} children={t} />
            )}
          </select>
          <button onClick={this.applyFilters} children="apply" />
          <button onClick={this.clearFilters} children="clear" />
        </div>
        <MultiSeriesChart
          type="line"
          data={dateMonthCounts(gradesByDate, minDate, maxDate)}
        >
          <Brush dataKey="name" height={30} stroke="#8884d8" />
        </MultiSeriesChart>
        <MultiSeriesChart
          type="bar"
          data={termCounts(gradesByBoro, terms.boro)}
        />
        <MultiSeriesChart
          type="barStacked"
          data={termCounts(gradesByCuisine, terms.cuisine)}
        />
        <MultiSeriesChart
          type="bar"
          data={termCounts(gradesByInspectionType, terms.inspection_type)}
        />
      </div>
    );
  }
}
