package dev.nullzwo.enrich.experiment1.algebras;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface StreamAlg<P> {

	interface Reader<T> extends Function<byte[], T> {
	}

	interface Writer<T> extends Function<T, byte[]> {
	}

	interface ReWr<T> {
		Reader<T> reader();
		Writer<T> writer();
	}

	ReWr<Long> longReWr = new ReWr<>() {
		@Override
		public Reader<Long> reader() {
			return bytes -> {
				ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
				buffer.put(bytes);
				buffer.flip();//need flip
				return buffer.getLong();
			};
		}

		@Override
		public Writer<Long> writer() {
			return data -> {
				ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
				buffer.putLong(data);
				return buffer.array();
			};
		}
	};

	interface Pipeline<K, V> {

		interface Transf<K, V, SK, SV, EK, EV> {
			record Entry<K, V>(K key, V value){}
			interface Store<K, V> {
				V get(K key);
				void set(K key, V value);
			}

			List<Entry<EK, EV>> transform(Store<SK, SV> state, K key, V value);
		}

		default <O> Pipeline<K, O> map(Function<V, O> f) {
			return flatMap(v -> List.of(f.apply(v)));
		}

		<O> Pipeline<K, O> flatMap(Function<V, List<O>> f);

		<O> Pipeline<K, O> foldByKey(O initial, BiFunction<V, O, O> aggregator, ReWr<O> aggWriter);

		default Pipeline<K, Long> countByKey() {
			return foldByKey(0L, (v, count) -> count + 1L, longReWr);
		}

		<SK, SV, EK, EV> Pipeline<EK, EV> transform(Transf<K, V, SK, SV, EK, EV> t, ReWr<SK> skReWr, ReWr<SV> svReWr);
	}

	<K, V> Pipeline<K, V> from(String topic, Reader<K> keyReader, Reader<V> valueReader);

	record Target<K, V>(String topic, Writer<K> keyWriter, Writer<V> valueWriter){}
	record Workflow<K, V>(Pipeline<K, V> pipeline, Target<K, V> target) {}

	P compile(Workflow<?, ?>... processes);
}
