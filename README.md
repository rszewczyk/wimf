# What's in my food?

An example full stack web application that explores New York's public data for restaurant inspection
results.

## Getting Started With Docker
  1. Install Java 8 JDK
  2. Install Docker and Docker Compose
  3. Run `./gradlew build` to build and tag the docker images
  4. Run `docker-compose up -d` to start the application and it's dependencies
  5. Visit `http://localhost:9000` in your browser

## Motivation

I've been working primarily in Go for some time now and the last time I was seriously involved with a JVM
based project, I was working in Groovy/Grails. With this project I am attempting to relearn "the basics". As
such I've made a conscious choice to avoid large frameworks that are aimed at rapid development in favor of
a focused set of technologies with the aim of understanding how things are pieced together.

What's in my food uses the follow technologies:

1. React for the UI
2. Jersey for REST
3. Postgres via JDBI for data
4. JavaRx
5. Hibernate Validation
6. Jackson for JSON
