// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.functions;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<R>
    extends Callable<R>,
    Supplier<R> {
  R apply();

  default R call() {
    return apply();
  }

  default R get() {
    return apply();
  }
}
