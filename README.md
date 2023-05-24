# Quarkus OPA Secure Microservice Demo

## Run and Develop Locally

### Requirements

For local build and run:

* Java 17+
* [opa](https://github.com/open-policy-agent/opa)
* [conftest](https://github.com/open-policy-agent/conftest) for validating the application configuration

### Configuration

Create a `.env` file with the following content. Leave the providers you
do not want to use blank.

```bash
GITHUB_CLIENT_ID=YOUR-CREDENTIALS
GITHUB_CLIENT_SECRET=YOUR-CREDENTIALS
```

Follow the instructions in the [Quarkus well-known OIDC proviers documentation](https://quarkus.io/guides/security-openid-connect-providers) 
to create an OAuth2 in GitHub.

## Run and Develop With Gitpod

### Requirements

* [Gitpod](https://gitpod.io) account

### Configuration

* In the Gitpod user settings, add environment variables for the SSO providers you want to use.

| Variable Name        | Scope            | Content          |
|----------------------|------------------|------------------|
| GITHUB_CLIENT_ID     | az82/osm-quarkus | YOUR-CREDENTIALS |
| GITHUB_CLIENT_SECRET | az82/osm-quarkus | YOUR-CREDENTIALS |


## Build

```bash
./mvnw package
```

## Run

Start OPA:

```bash
opa run -s src/main/rego/access-policy.rego
```

Start the service:

```bash
env $(cat .env | xargs) ./mvnw compile quarkus:dev
```

The server will be listening at http://localhost:8080

## Validate the Application Configuration

```bash
conftest -p src/main/rego/config-policy.rego test src/main/resources/application.yaml
```

## Copyright & License

Copyright 2023 Andreas Zitzelsberger, released under the [MIT License](LICENSE).

The Maven Wrapper is used under the Apache License, Version 2.0.