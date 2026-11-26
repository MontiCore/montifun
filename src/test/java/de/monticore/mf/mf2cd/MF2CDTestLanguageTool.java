// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf2cd;

import de.monticore.mf.montifun.MontiFunTestLanguageTool;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;

/**
 * {@link MontiFunTestLanguageTool},
 * but parses/symbol-completes with MF2CDMill instead of MontiFunMill
 */
public class MF2CDTestLanguageTool extends MontiFunTestLanguageTool {

  @Override
  public void initLanguage() {
    MFSymbolTableUtil.initAndPrepareMill(true);
  }

}
