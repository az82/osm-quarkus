package de.az82.demo.osmquarkus;

import java.util.Map;

/**
 * Represents a request to OPA.
 * <p>
 * An OPA request contains an arbitrarily structured input document.
 *
 * @param input input document
 */
record OpaRequest(Input input) {

    /**
     * OPA input document.
     *
     * @param method    HTTP method
     * @param path      HTTP request path
     * @param headers   HTTP headers
     * @param principal Spring Security authentication principal (e.g. user name)
     */
    record Input(String method, String path, Map<String, String> headers, String principal) {
    }

}