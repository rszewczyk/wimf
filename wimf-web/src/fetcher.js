// @flow
import React, { Component } from "react";
import fetch, { Request, Response, Headers } from "./fetch";

export type FetchState = {
  loading: boolean,
  fetchedAt: number,
  data: any,
  error: {
    message: string
  } | null,
  headers: Headers
};

export type FetcherComponentProps = FetchState & {
  fetch: (Request | string) => Promise<any>
};

type FetcherProps = {
  initialRequest?: Request | string,
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

  // TODO: figure out how to type check this
  return (
    Wrapped: any
    // Wrapped:
    //   | Class<React$Component<void, FetcherComponentProps, any>>
    //   | ((props: FetcherComponentProps) => ?React$Element<any>)
  ) => {
    class Fetcher extends Component {
      state: FetchState = {
        loading: false,
        fetchedAt: 0,
        data: null,
        error: null,
        headers: new Headers()
      };

      props: FetcherProps;

      fetch = async (request: Request | string) => {
        this.setState({ loading: true, error: null });
        try {
          const res = await doFetch(request);
          this.setState({
            data: res.data,
            headers: res.headers,
            fetchedAt: Date.now()
          });

          return res.data;
        } catch (e) {
          this.setState({ error: { message: e.message } });
        } finally {
          this.setState({ loading: false });
        }
      };

      componentDidMount() {
        if (this.props.initialRequest) {
          this.fetch(this.props.initialRequest);
        }
      }

      render() {
        const { initialRequest, baseRef, ...props } = this.props;
        return (
          <Wrapped
            {...this.state}
            ref={baseRef}
            fetch={this.fetch}
            {...props}
          />
        );
      }
    }

    return Fetcher;
  };
}
