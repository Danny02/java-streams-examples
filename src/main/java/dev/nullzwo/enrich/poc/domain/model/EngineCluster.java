package dev.nullzwo.enrich.poc.domain.model;

/**
 * Enumeration of all of Clusters for Engine Derivatives.
 * These are mainly used for deriving marketEngine names derived from derivative engine name.
 *
 * @author Shuko Ndhlovu
 */
public enum EngineCluster {
    STANDARD_ENGINE("Standard Engine"),
    EFFICIENT_DYNAMICS("Efficient Dynamics"),
    TI_MODEL("TI Model"),
    ALL_WHEEL_DRIVE_PLUS_HYBRID("All-wheel Drive + Hybrid"),
    PLUGIN_HYBRID("Plugin Hybrid"),
    LONG_VERSION("Long version"),
    IX_MODEL("IX Model"),
    M_PERFORMANCE("M Performance"),
    M_MODEL("M Model"),
    OLD_X1_E84("Old X1 (E84)"),
    OLD_X3_MODEL("Old X3 Model"),
    OLD_X5_MODEL("Old X5 Model"),
    OLD_X6_MODEL("Old X6 Model"),
    X_MODEL_NEW("X Model (new)"),
    X_MODEL_OLD("X Model (old)"),
    Z_MODEL("Z Model"),
    NONE("");

    private String clusterName;

    EngineCluster(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }
}

