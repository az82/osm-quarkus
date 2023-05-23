package de.az82.demo.osmquarkus;


/**
 * Response from OPA.
 * <p>
 * Here, only a simple allow/deny boolean is used.
 *
 * @param result whether access is granted
 */
record OpaResponse(boolean result) {

}