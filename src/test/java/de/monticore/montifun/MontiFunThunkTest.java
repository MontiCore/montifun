/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun;

import de.monticore.montifun.rte.Thunk;
import org.junit.Test;

import static org.junit.Assert.assertSame;

public class MontiFunThunkTest {

  @Test
  public void testLazinessUnevaluated() {
    // shall not throw
    Thunk.from(() -> {
      throw new RuntimeException();
    });
  }

  @Test
  public void testLazinessEvaluate() {
    Thunk<Integer> thunk = Thunk.from(() -> 1);
    assertSame(thunk.call(), 1);
  }

  @Test
  public void testNestedThunk() {
    Thunk<Integer> thunk = Thunk.fromNested(
        () -> Thunk.from(() -> 1)
    );
    assertSame(thunk.call(), 1);
  }

  @Test
  public void testThunkEvaluatesOnlyOnce() {
    //use array to allow passing non-final variable to lambda
    Integer val[] = {0};
    Thunk<Integer> thunk = Thunk.from(() -> {
      val[0]++; // side effect should happen once only
      return 1;
    });
    assertSame(val[0], 0);
    thunk.call();
    assertSame(val[0], 1);
    thunk.call();
    assertSame(val[0], 1);
  }


}
