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

import java.util.Set;

public class DealerChangeTransformerSupplier implements TransformerSupplier<VehicleId, VehicleEvent, KeyValue<VehicleId, VehicleEvent>> {

    public static final String CURRENT_DEALER = "current-dealer";

    @Override
    public Transformer<VehicleId, VehicleEvent, KeyValue<VehicleId, VehicleEvent>> get() {
        return new Transformer<>() {

            private KeyValueStore<VehicleId, DealerId> store;

            @Override
            public void init(ProcessorContext context) {
                store = context.getStateStore(CURRENT_DEALER);
            }

            @Override
            public KeyValue<VehicleId, VehicleEvent> transform(VehicleId key, VehicleEvent value) {
                KeyValue<VehicleId, VehicleEvent> event = null;
                if (value instanceof BaseDateChanged bdc) {
                    var old = store.get(key);
                    var next = bdc.dealerId();
                    if (!next.equals(old)) {
                        store.put(key, next);
                        event = new KeyValue<>(key, new DealerChanged(old, next));
                    }
                }
                return event;
            }

            @Override
            public void close() {

            }
        };
    }

    @Override
    public Set<StoreBuilder<?>> stores() {
        var builder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(CURRENT_DEALER),
                new JsonSerde(), new JsonSerde()
        );
        return Set.of(builder);
    }
}
