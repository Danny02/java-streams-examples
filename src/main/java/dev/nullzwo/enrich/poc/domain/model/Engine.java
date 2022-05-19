package dev.nullzwo.enrich.poc.domain.model;

/**
 * Pojo to manage VehicleModel derivative attributes.
 * These are mainly used for deriving marketEngine names derived from derivative engine name.
 *
 * @author Shuko Ndhlovu
 */

public record Engine(String derivedName,
    String marketingEngineName,
    boolean clusterMatch,
    boolean nameMatch,
    EngineCluster engineCluster){
}


