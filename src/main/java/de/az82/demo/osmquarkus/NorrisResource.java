package de.az82.demo.osmquarkus;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;
import static java.util.Objects.requireNonNull;

/**
 * Provides chuck norris facts.
 */
@Path("/api/norris")
public class NorrisResource {

    private final List<String> norrisFacts = loadLines("/norris-facts.txt");

    /**
     * Provides a random Chuck Norris fact.
     *
     * @return Chuck Norris fact
     */
    @GET
    @Path("/fact")
    public String fact() {
        return norrisFacts.get(ThreadLocalRandom.current().nextInt(norrisFacts.size()));
    }

    @SuppressWarnings("SameParameterValue")
    private static List<String> loadLines(String location) {
        try (Stream<String> lines = lines(Paths.get(requireNonNull(NorrisResource.class.getResource(location)).toURI()))) {
            return lines.toList();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
