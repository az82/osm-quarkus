package main

import future.keywords

forbidden_providers = { "github", "google" }

deny[msg] {
    # Fail if the Google OAuth2 client is used
    provider := input["quarkus.oidc.provider"]
    provider in forbidden_providers
    msg := sprintf("%s is disallowed as an identity provider", [provider])
}

deny[msg] {
    # Fail if the log level is lower than INFO
    regex.match("quarkus\\.log\\.category\\..*\\.level", key)
    value := input[key]
    value in ["ALL","TRACE","DEBUG"]
    msg := sprintf("%s=%s uses a log level lower than INFO which is disallowed in production", [key, value])
}