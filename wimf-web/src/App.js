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
import Button from "./Button";
import ButtonBar from "./ButtonBar";
import List from "./List";
import Pane from "./Pane";
import Inspection from "./Inspection";
import wrapWithFetcher from "./fetcher";

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
  resetDateRange: boolean,
  resetInspections: boolean
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

export function makeFilters(state: AppState): string {
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

  return queryString;
}

export function createSummaryRequest(state: AppState): string {
  return "/api/summary?" + makeFilters(state);
}

export function createInspectionsRequest(
  offset: number,
  state: AppState
): string {
  const filters = makeFilters(state);
  return `/api/inspection?offset=${offset}&limit=250&sort=inspection_date DESC&sort=business_id ASC${filters
    ? "&" + filters
    : ""}`;
}

export class App extends Component {
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
    resetDateRange: true,
    resetInspections: false
  };

  dateRangeChange = debounce(
    ({ startIndex, endIndex }) => {
      const indexState = {
        startDateIndex: startIndex,
        endDateIndex: endIndex
      };
      this.setState(indexState);
      this.props.fetch(
        createSummaryRequest({
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
    const { dateRange } = this.state;

    const cleared = {
      ...this.state,
      startDateIndex: 0,
      endDateIndex: dateRange.length - 1,
      filters: {
        inspection_type: [],
        cuisine: [],
        boro: []
      },
      resetDateRange: true,
      resetInspections: true
    };

    this.setState(cleared, () =>
      this.props.fetch(createSummaryRequest(cleared)).then(() => {
        this.setState({
          resetInspections: false
        });
      })
    );
  };

  applyFilters = () => {
    this.setState(
      {
        resetInspections: true
      },
      () =>
        this.props.fetch(createSummaryRequest(this.state)).then(() => {
          this.setState({
            resetInspections: false
          });
        })
    );
  };

  componentWillReceiveProps(nextProps: FetcherComponentProps) {
    const { data } = nextProps;

    if (data && this.state.resetDateRange) {
      const [min, max] = [new Date(data.minDate), new Date(data.maxDate)];
      const dateRange = [time.timeMonth(min), ...time.timeMonths(min, max)];

      this.setState({
        dateRange,
        startDateIndex: 0,
        endDateIndex: dateRange.length - 1,
        resetDateRange: false
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
      terms,
      total
    } = data;

    const {
      filters,
      dateRange,
      startDateIndex,
      endDateIndex,
      resetInspections
    } = this.state;

    return (
      <Page>
        <Column width={2}>
          <Pane>
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
          </Pane>
        </Column>
        <Column width={6}>
          <Pane>
            <MultiSeriesChart
              title="Monthly Grade Totals"
              description="All grades assigned per month. Drag the brush to adjust the date range for all other series."
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
              title="Grades by Boro"
              description="Grades broken down by the boro in which the establishment is located."
              type="bar"
              data={termCounts(gradesByBoro, terms.boro)}
            />
            <MultiSeriesChart
              title="Grades by Cuisine"
              type="barStacked"
              data={termCounts(gradesByCuisine, terms.cuisine)}
            />
            <MultiSeriesChart
              title="Grades by Inspection Type"
              type="bar"
              data={termCounts(gradesByInspectionType, terms.inspection_type)}
            />
            <List
              title="Inspection Results"
              description="Individual violations encountered during an inspection"
              total={total}
              filters={this.state}
              initialRequest={createInspectionsRequest(0, this.state)}
              rowComp={Inspection}
              reset={resetInspections}
              createRequest={(offset: number) =>
                createInspectionsRequest(offset, this.state)}
            />
          </Pane>
        </Column>
        <Loader visible={loading} />
      </Page>
    );
  }
}

export default wrapWithFetcher()(App);
