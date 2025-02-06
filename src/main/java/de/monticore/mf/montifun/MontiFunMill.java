package de.monticore.mf.montifun;

import de.monticore.mf.montifun.types3.MontiFunTypeCheck3;

public class MontiFunMill extends MontiFunMillTOP {

  /**
   * additionally inits the MontiFun TypeCheck
   */
  public static void init() {
    MontiFunMillTOP.init();
    MontiFunTypeCheck3.init();
  }

  public static void reset() {
    MontiFunMillTOP.reset();
    MontiFunTypeCheck3.reset();
  }

}
