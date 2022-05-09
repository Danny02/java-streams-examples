package dev.nullzwo.enrich.experiment1.transformer;

import dev.nullzwo.enrich.experiment1.Domain.*;
import dev.nullzwo.enrich.JsonSerde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.List;
import java.util.Set;

public class CountTransformerSupplier implements TransformerSupplier<VehicleId, VehicleEvent, Iterable<KeyValue<Country, Integer>>> {

    public static final String LAST_VEHICLE_COUNTRY = "last-vehicle-country";

    @Override
    public Transformer<VehicleId, VehicleEvent, Iterable<KeyValue<Country, Integer>>> get() {
        return new Transformer<>() {
            private KeyValueStore<VehicleId, Country> store;

            @Override
            public void init(ProcessorContext context) {
                store = context.getStateStore(LAST_VEHICLE_COUNTRY);
            }

            @Override
            public Iterable<KeyValue<Country, Integer>> transform(VehicleId key, VehicleEvent value) {
                if(value instanceof BaseDateChanged bdc) {
                    var last = store.get(key);
                    var next = bdc.country();
                    if(!next.equals(last)) {
                        store.put(key, next);
                        return List.of(new KeyValue<>(last, -1), new KeyValue<>(next, 1));
                    }
                }
                return List.of();
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public Set<StoreBuilder<?>> stores() {
        var builder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(LAST_VEHICLE_COUNTRY),
                new JsonSerde(), new JsonSerde()
        );
        return Set.of(builder);
    }
}
