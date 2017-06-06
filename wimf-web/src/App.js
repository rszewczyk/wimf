// @flow
import React, { Component } from "react";
import * as time from "d3-time";
import { Brush } from "recharts";
import debounce from "lodash/debounce";
import MultiSeriesChart from "./MultiSeriesChart";
import type { FetcherComponentProps } from "./fetcher";
import Loader from "./Loader";
import Filter from "./Filter";
import Page from "./Page";
import Column from "./Column";
import Overflow from "./Overflow";
import Button from "./Button";
import ButtonBar from "./ButtonBar";

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

function clearSelection() {
  if (window.getSelection) {
    if (window.getSelection().empty) {
      window.getSelection().empty();
    } else if (window.getSelection().removeAllRanges) {
      window.getSelection().removeAllRanges();
    }
  }
}

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
  endDateIndex: number,
  resetDates: boolean
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
    startDateIndex: 0,
    endDateIndex: 0,
    resetDates: true
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
      clearSelection();
    },
    500,
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

  clearFilters = () => {
    const cleared = {
      ...this.state,
      filters: {
        inspection_type: [],
        cuisine: [],
        boro: []
      },
      resetDates: true
    };
    this.setState(cleared);
    this.props.fetch(createRequest(cleared));
  };

  applyFilters = () => {
    this.props.fetch(createRequest(this.state));
  };

  componentWillReceiveProps(nextProps: FetcherComponentProps) {
    const { data } = nextProps;

    if (data && this.state.resetDates) {
      const [min, max] = [new Date(data.minDate), new Date(data.maxDate)];
      const dateRange = [time.timeMonth(min), ...time.timeMonths(min, max)];

      this.setState({
        dateRange,
        startDateIndex: 0,
        endDateIndex: dateRange.length - 1,
        resetDates: false
      });
    }
  }

  render() {
    const { data, error, loading } = this.props;
    if (!data) {
      return loading ? <Loader visible /> : <div>no data!</div>;
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
      <Page>
        <Column width={2}>
          <h3 style={{ marginLeft: "0.5rem" }}>Filters:</h3>
          <Filter
            name="Boro"
            value={filters.boro}
            onChange={this.filterChange.bind(null, "boro")}
            options={terms.boro}
          />
          <Filter
            name="Cuisine"
            value={filters.cuisine}
            onChange={this.filterChange.bind(null, "cuisine")}
            options={terms.cuisine}
          />
          <Filter
            name="Inspection Type"
            value={filters.inspection_type}
            onChange={this.filterChange.bind(null, "inspection_type")}
            options={terms.inspection_type}
          />
          <ButtonBar>
            <Button primary onClick={this.applyFilters} children="Apply" />
            <Button onClick={this.clearFilters} children="Clear" />
          </ButtonBar>
        </Column>
        <Column width={6}>
          <Overflow>
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
          </Overflow>
        </Column>
        <Loader visible={loading} />
      </Page>
    );
  }
}
