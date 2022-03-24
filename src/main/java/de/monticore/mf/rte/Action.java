package de.monticore.mf.rte;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Action {

  @FunctionalInterface
  public interface A0 {
    void apply();
  }

  @FunctionalInterface
  public interface A1<Arg0>
      extends Consumer<Arg0> {
    void apply(Arg0 arg0);

    default void accept(Arg0 arg0) {
      apply(arg0);
    }
  }

  @FunctionalInterface
  public interface A2<Arg0, Arg1>
      extends BiConsumer<Arg0, Arg1> {
    void apply(Arg0 arg0, Arg1 arg1);

    default void accept(Arg0 arg0, Arg1 arg1) {
      apply(arg0, arg1);
    }
  }

  @FunctionalInterface
  public interface A3<Arg0, Arg1, Arg2> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2);
  }

  @FunctionalInterface
  public interface A4<Arg0, Argrg1, Argrg2, Argrg3> {
    void apply(Arg0 arg0, Argrg1 arg1, Argrg2 arg2, Argrg3 arg3);
  }

  @FunctionalInterface
  public interface A5<Arg0, Arg1, Arg2, Arg3, Arg4> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4);
  }

  @FunctionalInterface
  public interface A6<Arg0, Arg1, Arg2, Arg3, Arg4, Arg5> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5);
  }

  @FunctionalInterface
  public interface A7<Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6);
  }

  @FunctionalInterface
  public interface A8<Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6,
        Arg7 arg7);
  }

  @FunctionalInterface
  public interface A9<Arg0, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8> {
    void apply(Arg0 arg0, Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4, Arg5 arg5, Arg6 arg6,
        Arg7 arg7, Arg8 arg8);
  }

}
