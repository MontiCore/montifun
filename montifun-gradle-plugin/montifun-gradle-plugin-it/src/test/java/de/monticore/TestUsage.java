/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import org.junit.jupiter.api.Test;

import static de.monticore.Constants.*;
import static de.monticore.Functions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

// simply uses the available symbols
public class TestUsage {

  public static double localPI = PI;

  public static int fourMinusTwo = minusTwo(4);

  @Test
  public void test() {
    assertEquals(2, fourMinusTwo);
    assertEquals(PI, localPI);
  }

}
