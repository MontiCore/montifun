package de.monticore.mf.mf2cd;

import de.monticore.mf.montifun.types3.MontiFunTypeCheck3;

public class MF2CDMill extends MF2CDMillTOP {

  /**
   * additionally inits the MontiFun TypeCheck
   */
  public static void init() {
    MF2CDMillTOP.init();
    MontiFunTypeCheck3.init();
  }

  public static void reset() {
    MF2CDMillTOP.reset();
    MontiFunTypeCheck3.reset();
  }

}
