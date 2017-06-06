// @flow
import React, { Component } from "react";
import { List } from "react-virtualized";
import type { FetcherComponentProps } from "./fetcher";
import wrapWithFetcher from "./fetcher";
import Pane from "./Pane";

type FetchingListProps = FetcherComponentProps & {
  reset?: boolean,
  total: number,
  rowComp: any,
  title: string,
  description?: string,
  createRequest: number => string | Request
};

export class FetchingList extends Component {
  props: FetchingListProps;

  state: {
    // TODO: should be able to tighten the typing based on row component
    list: Array<any>
  } = {
    list: []
  };

  loadMore = () => {
    const { createRequest } = this.props;
    const { list } = this.state;
    return this.props.fetch(createRequest(list.length));
  };

  rowRenderer = ({
    index,
    key,
    style
  }: {
    index: number,
    key: any,
    style: { [string]: string }
  }) => {
    const { rowComp: Row } = this.props;
    const { list } = this.state;

    let content;
    if (index === list.length) {
      content = <a onClick={this.loadMore} children="load more" />;
    } else {
      content = <Row {...list[index]} />;
    }

    return (
      <div key={key} style={style}>
        {content}
      </div>
    );
  };

  componentWillReceiveProps(nextProps: FetchingListProps) {
    const { data, reset, createRequest, fetch } = nextProps;

    if (reset && !this.props.reset) {
      this.setState({ list: [] }, () => fetch(createRequest(0)));
      return;
    }

    if (data !== this.props.data) {
      this.setState(prevState => ({
        list: [...prevState.list, ...data]
      }));
    }
  }

  render() {
    const { list } = this.state;
    const { total, title, description } = this.props;

    if (total === 0) {
      return <div>No Data!</div>;
    }

    return (
      <Pane>
        <div style={{ marginBottom: "1.0rem" }}>
          <h3>{title}</h3>
          {description && <div>{description}</div>}
        </div>
        <List
          height={400}
          width={800}
          rowHeight={120}
          rowRenderer={this.rowRenderer}
          rowCount={list.length + 1}
        />
      </Pane>
    );
  }
}

export default wrapWithFetcher()(FetchingList);
