# What's In My Food?

An example full stack web application that explores New York's public data for [restaurant inspection
results](https://data.cityofnewyork.us/Health/DOHMH-New-York-City-Restaurant-Inspection-Results/43nn-pn8j).

## Getting Started With Docker

  1. Install a Java 8 JDK
  2. Install Docker and Docker Compose
  3. Run `./gradlew dockerBuildImage` to build and tag the docker images
  4. Run `docker-compose up -d` to start the application and it's dependencies
  5. Visit `http://localhost:9000` in your browser

It will take a minute or so to ingest all 200,000+ records. You can check to see if the ingest process is complete by running `docker-compose logs ingest`.

## Building Without Docker

  1. Install a Java 8 JDK
  2. Run a Postgres server and create a database
  3. Run `./gradlew build`
  4. Unzip the archive in the `./wimf-ingest/build/distributions` directory and invoke the `wimf-ingest` script. Use `wimf-ingest --help` for guidance on providing the correct database connection parameters.
  5. Unzip the archive in the `./wimf-services/build/distributions` directory and invoke the `wimf-services` script. Use `wimf-services --help` for guidance on providing the correct database connection parameters.
  6. Run `./gradlew wimf-web:run`. This will start the UI development server and open a new tab in your default browser with the correct URL. It will take several seconds for the assets to compile before you see the UI.

## Motivation

I've been working primarily in Go for some time now and the last time I was seriously involved with a JVM based project, I was working in Groovy/Grails. With this project I am attempting to relearn "the basics". As
such I've made a conscious choice to avoid large frameworks that are aimed at rapid development in favor of a focused set of technologies with the goal of understanding how things are pieced together.

What's In My Food uses the following core technologies:

#### React for the UI

[React](https://facebook.github.io/react/) is a declarative, component based Javascript view library. You can think of a React component as a function that
takes data and turns it into another form of data (the DOM) - the same input always produces the same output. This makes components written in React easy to
understand and test. In addition to React, What's In My Food features CSS written entirely in Javascript using [Glamor](https://github.com/threepointone/glamor/tree/v3).
There are many CSS in JS solutions out there, but Glamor is one of the few that feels "right". It's real CSS but without the selector madness and you get
to treat styles as values which is important in a component based UI.

I used Facebook's [Create React App](https://github.com/facebookincubator/create-react-app/) CLI to get up and running. It uses [Webpack](https://webpack.js.org/) under the hood to
intelligently bundle javascript and other assets. This was one of my few concessions to the need to "move fast". Create React App is focused entirely on working out of the box so
it doesn't offer any hooks to the underlying Webpack configuration. I have lots of experience with tinkering with Webpack so I was fully prepared to "eject" from CRA and manage
things myself (Webpack is notoriously hard to get started with) - but it turned out I never had to.

Other notables include [Flow](https://flow.org/en/) for static type checking, [Jest](https://facebook.github.io/jest/) for unit testing, [Prettier](https://github.com/prettier/prettier)
for code formatting and [ESLint](http://eslint.org/) for linting. Most of this works out of the box with Create React App.

#### Postgres and JDBI for data

Perhaps a bit of an anachronism in the NoSQL age and maybe even a bit counter intuitive - after all, if relational, why not ORM? However, What's In My Food
is entirely about data exploration, and SQL is a good way to talk to data. JDBI on the surface is a straightforward way to use SQL in your application. In
practice though it took a little longer than I expected to figure things out. The documentation is sparse and I had to rely on some trial and error/reading of source code to
get things working. If I had to do it again with the benefit of hindsight, I might have been swayed by the relative ease of something like Elasticsearch.

#### Jersey for REST

I chose [Jersey](https://jersey.github.io/) because it's well documented, mature and has a very straightforward API. It also makes very few assumptions about
the rest of your stack, and can be deployed in a number of ways so it's good for the sort of mix and match approach that I took here. It was also easy to test my
Jersey resources.

#### JavaRx

To be honest, this was a bit of a luxury choice. I've been wanting to dive into FRP style programming for awhile and I thought I had a somewhat decent use case for it here. And it
did seem to work out well for the ingest side of things. I just barely scratched the surface with [JavaRx](https://github.com/ReactiveX/RxJava) and observables but I found it
productive to think of processing event streams in the same way that I would a generic collection (map, reduce, etc). As a bonus it turns out that testing these streams is very
straightforward.

#### Other bits and pieces

  - [Hibernate Validator](http://hibernate.org/validator/) for checking user inputs and keeping nasty stuff out of the data layer.
  - [Jackson](https://github.com/FasterXML/jackson) for going to/from JSON
  - [React Virtualized](https://github.com/bvaughn/react-virtualized) for efficiently rendering large lists
  - [Recharts](http://recharts.org/#/en-US/) for charts
  - [HK2](https://hk2-project.github.io/) for a little bit of dependency injection

## What did I Learn?

I learned that this is a relatively laborious way to build an application. But that was somewhat expected and intentional. It is also something that I'm sure gets easier with practice. The payoff was that I did find it edifying to deal directly with the details that in the past were hidden behind many layers of Grail/Spring/Hibernate. I feel like I have an incrementally better understanding of how to build multi tiered systems in Java.

That being said however there are toolkits out there that potentially represent better tradeoffs in terms of the rapid development of something like Grails and the flexibility and greater control you get with this approach. When programming in Go, I frequently use [Gokit](https://gokit.io/) and for Java, [Dropwizard](http://www.dropwizard.io/0.9.2/docs/) looks promising.

## Prior art

Many thanks to [Jonathan Gilday](https://github.com/gilday) for showing me [how to microservice](https://github.com/gilday/how-to-microservice) in Java and helping to review this repo.
