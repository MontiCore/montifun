// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.actions;

@FunctionalInterface
public interface Action4<Arg0, Argrg1, Argrg2, Argrg3> {
  void apply(Arg0 arg0, Argrg1 arg1, Argrg2 arg2, Argrg3 arg3);
}
