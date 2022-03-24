// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.actions;

@FunctionalInterface
public interface Action3<Arg0, Arg1, Arg2> {
  void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2);
}
