/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.rte;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Thunks supply lazily calculated values and cache the values ones calculated
 *
 * @param <T> the type to be supplied
 */
public class Thunk<T> implements Lazy<T> {

  //exactly one of these is not empty at any given time
  protected Optional<T> value = Optional.empty();

  protected Optional<Lazy<T>> lazyValue = Optional.empty();

  protected Optional<Lazy<Lazy<T>>> nestedLazyValue = Optional.empty();
  //note: using Optional<Lazy<Object>> could reduce the memory amount
  //in general Frege has more efficient code
  //which does however seem to circumvent type safety measures

  private Thunk() {
  }

  public static <T> Thunk<T> from(T value) {
    Thunk<T> thunk = new Thunk<>();
    thunk.value = Optional.of(value);
    return thunk;
  }

  public static <T> Thunk<T> from(Lazy<T> lazyValue) {
    if (lazyValue instanceof Thunk) {
      return (Thunk<T>) lazyValue;
    }
    Thunk<T> thunk = new Thunk<>();
    thunk.lazyValue = Optional.of(lazyValue);
    return thunk;
  }

  /**
   * removes a Callable layer
   * E.g. foo x = bar x
   * here, bar returns a {@link Lazy<T>}
   * but foo has to return a {@link Lazy<T>} without evaluating bar
   * as such foo returns fromNested(()->bar())
   *
   * @param nestedLazyValue the {@link Lazy} that is nested
   * @param <T>             the final return type
   * @return a {@link Thunk} which has one less layer of {@link Callable}
   */
  public static <T> Thunk<T> fromNested(Lazy<Lazy<T>> nestedLazyValue) {
    Thunk<T> thunk = new Thunk<>();
    thunk.nestedLazyValue = Optional.of(nestedLazyValue);
    return thunk;
  }

  public T apply() {
    while (!value.isPresent()) {
      if (nestedLazyValue.isPresent()) {
        lazyValue = Optional.of(nestedLazyValue.get().call());
        nestedLazyValue = Optional.empty();
      }
      if (lazyValue.isPresent()) {
        if (lazyValue.get() instanceof Thunk<?>) {
          Thunk<T> thunk = (Thunk<T>) lazyValue.get();
          // tco (Tail Call Optimization)
          // E.g.: subTo1 x = if (x == 1) (1) (subTo1 (x-1))
          // subTo1 may use a lot of stack space without tco
          // -> we do NOT call() on Thunks
          value = thunk.value;
          lazyValue = thunk.lazyValue;
          nestedLazyValue = thunk.nestedLazyValue;
        }
        else {
          //just evaluate
          value = Optional.of(lazyValue.get().call());
          lazyValue = Optional.empty();
        }
      }
    }
    return value.get();
  }

}
