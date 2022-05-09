package dev.nullzwo.enrich.experiment1.algebras;

import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Pipeline.Transf.Store;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class KafkaStreamsAlg implements StreamAlg<Topology> {

	private final class KafkaPipeline<K, V> implements Pipeline<K, V> {

		private final Function<StreamsBuilder, KStream<K, V>> kStream;

		private KafkaPipeline(Function<StreamsBuilder, KStream<K, V>> kStream) {
			this.kStream = kStream;
		}

		public KStream<K, V> buildOn(StreamsBuilder builder) {
			return kStream.apply(builder);
		}

		private <T, O> KafkaPipeline<T, O> transform(Function<KStream<K, V>, KStream<T, O>> f) {
			return new KafkaPipeline<>(b -> f.apply(kStream.apply(b)));
		}

		@Override
		public <O> Pipeline<K, O> flatMap(Function<V, List<O>> f) {
			return transform(s -> s.flatMapValues(v -> f.apply(v)));
		}

		@Override
		public <O> Pipeline<K, O> foldByKey(O initial, BiFunction<V, O, O> aggregator, ReWr<O> aggWriter) {
			return transform(s -> s.groupByKey().aggregate(() -> initial, (k, v, o) -> aggregator.apply(v, o)).toStream());
		}

		@Override
		public <SK, SV, EK, EV> Pipeline<EK, EV> transform(Transf<K, V, SK, SV, EK, EV> t, ReWr<SK> skReWr, ReWr<SV> svReWr) {
			return transform(s -> {
				return s.flatTransform(new TransformerSupplier<>() {

					private final String storeName = UUID.randomUUID().toString();

					@Override
					public Transformer<K, V, Iterable<KeyValue<EK, EV>>> get() {
						return new Transformer<>() {
							private KeyValueStore<SK, SV> store;

							@Override
							public void init(ProcessorContext context) {
								store = context.getStateStore(storeName);
							}

							@Override
							public Iterable<KeyValue<EK, EV>> transform(K key, V value) {
								return t.transform(new Store<SK, SV>() {
									@Override
									public SV get(SK key) {
										return store.get(key);
									}

									@Override
									public void set(SK key, SV value) {
										store.put(key, value);
									}
								}, key, value).stream().map(e -> new KeyValue<>(e.key(), e.value())).collect(toList());
							}

							@Override
							public void close() {
							}
						};
					}

					@Override
					public Set<StoreBuilder<?>> stores() {
						var builder = Stores.keyValueStoreBuilder(
								Stores.persistentKeyValueStore(storeName),
								toSerde(skReWr), toSerde(svReWr)
						);
						return Set.of(builder);
					}
				});
			});
		}
	}

	static <T> Serde<T> toSerde(ReWr<T> reWr) {
		return null;
	}

	@Override
	public <K, V> Pipeline<K, V> from(String topic, Reader<K> keyReader, Reader<V> valueReader) {
		return new KafkaPipeline<>(b -> b.stream(topic));
	}

	@Override
	public Topology compile(Workflow<?, ?>... workflows) {
		var builder = new StreamsBuilder();
		for (Workflow<?, ?> workflow : workflows) {
			if (workflow.pipeline() instanceof KafkaPipeline<?, ?> kstreem) {
				kstreem.buildOn(builder).to(workflow.target().topic());
			} else {
				throw new IllegalArgumentException("wrong streem type: " + workflow.pipeline().getClass());
			}
		}
		return builder.build();
	}
}
