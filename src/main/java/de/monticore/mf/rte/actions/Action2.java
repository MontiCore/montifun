// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.actions;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface Action2<Arg0, Arg1>
    extends BiConsumer<Arg0, Arg1> {
  void apply(Arg0 arg0, Arg1 arg1);

  default void accept(Arg0 arg0, Arg1 arg1) {
    apply(arg0, arg1);
  }
}
