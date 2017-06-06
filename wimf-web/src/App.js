// @flow
import React, { Component } from "react";
import * as time from "d3-time";
import { Brush } from "recharts";
import debounce from "lodash/debounce";
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

export function dateMonthCounts(buckets: Buckets, range: Array<Date>) {
  // initialize each bucket to zero
  const counts = {};
  const keys = Object.keys(buckets);
  range.forEach(d => {
    counts[d.toString()] = keys.reduce((acc, next) => ({ ...acc, [next]: 0 }), {
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

  return range.map(d => counts[d.toString()]);
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

type Filters = {
  inspection_type: Array<string>,
  cuisine: Array<string>,
  boro: Array<string>
};

type AppState = {
  filters: Filters,
  dateRange: Array<Date>,
  startDateIndex: number,
  endDateIndex: number
};

function twoDigit(n: number): string {
  return n < 10 ? `0${n}` : `${n}`;
}

function makeTimeStamp(d: Date) {
  return `${d.getFullYear()}-${twoDigit(d.getMonth() + 1)}-${twoDigit(
    d.getDate()
  )}T00:00:00`;
}

function makeFilter(name: string, val: string, op: string = "="): string {
  return `filter=${encodeURIComponent(`${name}${op}${val}`)}`;
}

export function createRequest(state: AppState): string {
  const { filters, startDateIndex, endDateIndex, dateRange } = state;

  let queryString = Object.keys(filters)
    .map(n => filters[n].map(val => makeFilter(n, val)).join("&"))
    .filter(f => f.length > 0)
    .join("&");

  if (startDateIndex > -1) {
    queryString = `${queryString}&${makeFilter(
      "inspection_date",
      makeTimeStamp(dateRange[startDateIndex]),
      ">"
    )}`;
  }

  if (endDateIndex > -1) {
    queryString = `${queryString}&${makeFilter(
      "inspection_date",
      makeTimeStamp(dateRange[endDateIndex]),
      "<"
    )}`;
  }

  return "/api/summary?" + queryString;
}

export default class App extends Component {
  props: FetcherComponentProps;
  state: AppState = {
    filters: {
      inspection_type: [],
      cuisine: [],
      boro: []
    },
    dateRange: [],
    startDateIndex: -1,
    endDateIndex: -1
  };

  dateRangeChange = debounce(
    ({ startIndex, endIndex }) => {
      const indexState = {
        startDateIndex: startIndex,
        endDateIndex: endIndex
      };
      this.setState(indexState);
      this.props.fetch(
        createRequest({
          ...this.state,
          ...indexState
        })
      );
    },
    750,
    {
      trailing: true
    }
  );

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
      },
      dateRange: [],
      startDateIndex: -1,
      endDateIndex: -1
    };
    this.setState(cleared);
    this.props.fetch(createRequest(cleared));
  };

  applyFilters = (e: Event) => {
    e.preventDefault();
    this.props.fetch(createRequest(this.state));
  };

  componentWillReceiveProps(nextProps: FetcherComponentProps) {
    const { data } = nextProps;

    if (data && !this.props.data) {
      const [min, max] = [new Date(data.minDate), new Date(data.maxDate)];
      const dateRange = [time.timeMonth(min), ...time.timeMonths(min, max)];
      this.setState({
        dateRange,
        startDateIndex: 0,
        endDateIndex: dateRange.length - 1
      });
    }
  }

  render() {
    const { data, error, loading } = this.props;
    if (!data) {
      return loading ? <div>loading</div> : <div>no data!</div>;
    }
    if (error) {
      return <div children={error.message} />;
    }

    const {
      gradesByDate,
      gradesByBoro,
      gradesByCuisine,
      gradesByInspectionType,
      terms
    } = data;

    const { filters, dateRange, startDateIndex, endDateIndex } = this.state;

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
          data={dateMonthCounts(gradesByDate, dateRange)}
        >
          <Brush
            dataKey="name"
            height={30}
            stroke="#8884d8"
            onChange={this.dateRangeChange}
            startIndex={startDateIndex}
            endIndex={endDateIndex}
          />
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
