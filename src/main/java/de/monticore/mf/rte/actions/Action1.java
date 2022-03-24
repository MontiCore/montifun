// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.actions;

import java.util.function.Consumer;

@FunctionalInterface
public interface Action1<Arg0>
    extends Consumer<Arg0> {
  void apply(Arg0 arg0);

  default void accept(Arg0 arg0) {
    apply(arg0);
  }
}
