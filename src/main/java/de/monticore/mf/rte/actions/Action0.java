// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.actions;

@FunctionalInterface
public interface Action0
    extends Runnable {
  void apply();

  default void run() {
    apply();
  }
}
