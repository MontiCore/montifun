// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.rte.functions;

@FunctionalInterface
public interface Function4<R, Arg0, Arg1, Arg2, Arg3> {
  R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3);
}
