FROM gitpod/workspace-base:latest AS build-stage

WORKDIR /build
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
USER root

ENV OPA_VERSION=0.52.0
RUN curl -fsSL -o opa https://openpolicyagent.org/downloads/v${OPA_VERSION}/opa_linux_amd64_static \
    && chmod +x opa

FROM gitpod/workspace-java-17:latest

COPY --from=build-stage /build/* /usr/local/bin/