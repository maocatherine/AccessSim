package edu.kit.ifv.mobitopp.util.collections;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtils {

  private StreamUtils() {
    super();
  }

  @SafeVarargs
  public static <T> Stream<T> concat(Stream<T>... streams) {
    return Stream.of(streams).reduce(Stream::concat).orElse(Stream.empty());

  }

  public static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key: %s and %s", u, v));
    };
  }

  /**
   * Analog method to {@link Collectors#toSet()}, but preserves the insertion order.
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T> Collector<T, ?, Set<T>> toLinkedSet() {
    return toSet(LinkedHashSet::new);
  }

  /**
   * Analog method to {@link Collectors#toSet()}, but allows other implementations of {@link Set}
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T, S extends Set<T>> Collector<T, ?, S> toSet(Supplier<S> supplier) {
    return new Collector<T, S, S>() {

      @Override
      public Supplier<S> supplier() {
        return supplier;
      }

      @Override
      public BiConsumer<S, T> accumulator() {
        return S::add;
      }

      @Override
      public BinaryOperator<S> combiner() {
        return (left, right) -> {
          left.addAll(right);
          return left;
        };
      }

      @Override
      public Function<S, S> finisher() {
        return i -> i;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
      }
    };
  }

  /**
   * Analog method to {@link Collectors#toMap(Function, Function)}, but preserves the insertion
   * order.
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
  }

  public static <T> Stream<T> streamOf(Optional<T> optional) {
    if (optional.isPresent()) {
      return Stream.of(optional.get());
    }
    return Stream.empty();
  }

  /**
   * This method creates a collector which collects elements in a {@link SortedMap}
   *
   * @see Collectors#toMap(Function, Function)
   */
  public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), TreeMap::new);
  }

	/**
	 * This method creates a collector which collects elements in a {@link SortedMap} using the given
	 * comparator.
	 * 
	 * @param comparator used to sort the elements in the map
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
			Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper,
			Comparator<K> comparator) {
		Supplier<SortedMap<K, U>> mapSupplier = () -> new TreeMap<>(comparator);
		return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
	}

  public static <T> Collector<T, ?, SortedSet<T>> toSortedSet() {
    return toSet(TreeSet::new);
  }
}
