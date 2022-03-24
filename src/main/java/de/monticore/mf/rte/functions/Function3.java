// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.functions;

@FunctionalInterface
public interface Function3<R, Arg0, Arg1, Arg2> {
  R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2);
}
