package dev.nullzwo.enrich.experiment1.algebras;

import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Pipeline.Transf.Store;
import org.apache.kafka.streams.Topology;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.BiFunction;
import java.util.function.Function;

public class KafkaAlg implements StreamAlg<Topology> {

	interface SubscriberFactory {
		<K, V> Subscriber<Record<K, V>> createFor(String topic);
	}

	record Record<K, V>(K key, V value) {
	}

	private final class ReactivePipeline<K, V> implements Pipeline<K, V> {

		private final SubscriberFactory subscriberFactory;
		private final Publisher<Record<K, V>> publisher;

		private ReactivePipeline(SubscriberFactory subscriberFactory, Publisher<Record<K, V>> publisher) {
			this.subscriberFactory = subscriberFactory;
			this.publisher = publisher;
		}

		@Override
		public <O> Pipeline<K, O> flatMap(Function<V, List<O>> f) {
			return new ReactivePipeline<>(subscriberFactory, sub -> {
				publisher.subscribe(new Subscriber<>() {
					@Override
					public void onSubscribe(Subscription subscription) {
						sub.onSubscribe(subscription);
					}

					@Override
					public void onNext(Record<K, V> item) {
						for (O o : f.apply(item.value)) {
							sub.onNext(new Record<>(item.key, o));
						}
					}

					@Override
					public void onError(Throwable throwable) {
						sub.onError(throwable);
					}

					@Override
					public void onComplete() {
						sub.onComplete();
					}
				});
			});
		}

		@Override
		public <O> Pipeline<K, O> foldByKey(O initial, BiFunction<V, O, O> aggregator, ReWr<O> aggWriter) {
			return transform((store, key, value) -> {
				var state = store.get(key);
				if(state == null) {
					state = initial;
				}
				var next = aggregator.apply(value, state);
				store.set(key, next);
				return List.of(new Transf.Entry<>(key, next));
			}, null, aggWriter);
		}

		@Override
		public <SK, SV, EK, EV> Pipeline<EK, EV> transform(Transf<K, V, SK, SV, EK, EV> t, ReWr<SK> skReWr, ReWr<SV> svReWr) {
			return new ReactivePipeline<>(subscriberFactory, sub -> {
				var cache = new HashMap<SK, SV>();
				publisher.subscribe(new Subscriber<>() {
					@Override
					public void onSubscribe(Subscription subscription) {
						sub.onSubscribe(subscription);
					}

					@Override
					public void onNext(Record<K, V> item) {
						t.transform(new Store<>() {
							@Override
							public SV get(SK key) {
								return cache.get(key);
							}

							@Override
							public void set(SK key, SV value) {
								cache.put(key, value);
							}
						}, item.key, item.value).forEach(e -> sub.onNext(new Record<>(e.key(), e.value())));
					}

					@Override
					public void onError(Throwable throwable) {
						sub.onError(throwable);
					}

					@Override
					public void onComplete() {
						sub.onComplete();
					}
				});
			});
		}
	}

	@Override
	public <K, V> Pipeline<K, V> from(String topic, Reader<K> keyReader, Reader<V> valueReader) {
//		KafkaConsumer a;
//		a.po
//		return new <> (b -> b.stream(topic), publisher);
		throw new UnsupportedOperationException();
	}

	@Override
	public Topology compile(Workflow<?, ?>... workflows) {
//		var builder = new StreamsBuilder();
//		for (Workflow<?, ?> workflow : workflows) {
//			if (pipeline instanceof <?, ?>kstreem){
//				kstreem.buildOn(builder);
//			} else{
//				throw new IllegalArgumentException("wrong streem type: " + pipeline.getClass());
//			}
//		}
//		return builder.build();
		throw new UnsupportedOperationException();
	}
}
