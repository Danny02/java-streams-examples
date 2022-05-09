package dev.nullzwo.enrich.experiment1;

import dev.nullzwo.enrich.experiment1.Domain.DealerChanged;
import dev.nullzwo.enrich.experiment1.Domain.PriceChanged;
import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;
import dev.nullzwo.enrich.experiment1.Domain.VehicleId;
import dev.nullzwo.enrich.experiment1.transformer.CountTransformerSupplier;
import dev.nullzwo.enrich.experiment1.transformer.DealerChangeTransformerSupplier;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.util.List;

public class Streams {

    public static final String VEHICLES_TOPIC = "VEHICLES";
    public static final List<String> TOPICS = List.of(VEHICLES_TOPIC);

    private static void countVehiclesPerCountry(StreamsBuilder builder) {
        builder.<VehicleId, VehicleEvent>stream(VEHICLES_TOPIC)
                .flatTransform(new CountTransformerSupplier())
                .groupByKey()
                .aggregate(() -> 0, (k, v, s) -> s + v)
                .toStream().to("country-counts");
    }

    private static void detectDealerChanges(StreamsBuilder builder) {
        builder.<VehicleId, VehicleEvent>stream(VEHICLES_TOPIC)
                .transform(new DealerChangeTransformerSupplier())
                .to(VEHICLES_TOPIC);
    }

    private static void resetPrice(StreamsBuilder builder) {
        builder.<VehicleId, VehicleEvent>stream(VEHICLES_TOPIC)
                .flatMapValues(e -> switch (e) {
                    case DealerChanged dc -> List.of(new PriceChanged(0));
                    default -> List.of();
                })
                .to(VEHICLES_TOPIC);
    }

    private static void countEvents(StreamsBuilder builder) {
        builder.<VehicleId, VehicleEvent>stream(VEHICLES_TOPIC)
                .groupByKey()
                .count()
                .toStream()
                .to("events-per-car");
    }

    public static Topology all() {
        var builder = new StreamsBuilder();

        countVehiclesPerCountry(builder);
        detectDealerChanges(builder);
        countEvents(builder);
        resetPrice(builder);

        var topology = builder.build();
        System.out.println(topology.describe());
        return topology;
    }
}
