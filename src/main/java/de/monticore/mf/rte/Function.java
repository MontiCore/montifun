package de.monticore.mf.rte;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Function {

  @FunctionalInterface
  public interface A0<R>
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

  @FunctionalInterface
  public interface A1<R, Arg0>
      extends java.util.function.Function<Arg0, R> {
    //R apply(Arg0 arg0);
  }

  @FunctionalInterface
  public interface A2<R, Arg0, Arg1>
      extends java.util.function.BiFunction<Arg0, Arg1, R> {
   // R apply(Arg0 arg0, Arg1 arg1);
  }

  @FunctionalInterface
  public interface A3<R, Arg0, Arg1, Arg2> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2);
  }

  @FunctionalInterface
  public interface A4<R, Arg0, Arg1, Arg2, Arg3> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3);
  }

  @FunctionalInterface
  public interface A5<R, Arg0, Arg1, Arg2, Arg3, Arg4> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
  }

  @FunctionalInterface
  public interface A6<R, Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
  }

  @FunctionalInterface
  public interface A7<R, Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
  }

  @FunctionalInterface
  public interface A8<R, Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7);
  }

  @FunctionalInterface
  public interface A9<R, Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> {
    R apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6, Arg7 arg7, Arg8 arg8);
  }

}
