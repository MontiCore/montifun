/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.rte;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A Supplier interface for lazily calculated values
 * This Supplier may be used like a callable as well
 *
 * @param <T> the type to be calculated
 */
public interface Lazy<T> extends Callable<T>, Supplier<T> {

  T call();

  default T get() {
    return call();
  }

}
