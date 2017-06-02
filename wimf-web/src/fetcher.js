// @flow
import React, { Component } from "react";
import fetch, { Request, Response, Headers } from "./fetch";

export type FetchState = {
  loading: boolean,
  fetchedAt: number,
  data: any,
  error:
    | {
        message: string
      }
    | null,
  headers: Headers
};

type FetcherProps = {
  request: Request | string,
  baseRef?: HTMLElement => void
};

export default function wrapWithFetcher(
  fetchFunc: (Request | string) => Promise<any> = fetch
) {
  async function doFetch(req: Request | string): any {
    const response: Response = await fetchFunc(req);
    if (!response.ok) {
      return Promise.reject(
        new Error(response.statusText || "Non successful response status")
      );
    }

    const asText =
      req instanceof Request && req.headers.get("Accept").startsWith("text");

    const data = asText ? await response.text() : await response.json();

    return { data, headers: response.headers };
  }

  return (
    Wrapped:
      | Class<React$Component<void, FetchState, any>>
      | ((props: FetchState) => ?React$Element<any>)
  ) => {
    class Fetcher extends Component<void, FetcherProps, FetchState> {
      state = {
        loading: false,
        fetchedAt: 0,
        data: null,
        error: null,
        headers: new Headers()
      };

      async fetch(request: Request | string) {
        this.setState({ loading: true, error: null });
        try {
          const res = await doFetch(request);
          this.setState({
            data: res.data,
            headers: res.headers,
            fetchedAt: Date.now()
          });
        } catch (e) {
          this.setState({ error: { message: e.message } });
        } finally {
          this.setState({ loading: false });
        }
      }

      componentDidMount() {
        this.fetch(this.props.request);
      }

      componentWillReceiveProps(nextProps: FetcherProps) {
        if (nextProps.request !== this.props.request) {
          this.fetch(nextProps.request);
        }
      }

      render() {
        return <Wrapped {...this.state} ref={this.props.baseRef} />;
      }
    }

    return Fetcher;
  };
}
