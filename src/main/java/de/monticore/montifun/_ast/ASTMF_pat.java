/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_pat extends ASTMF_patTOP {

  /**
   * converts this to an {@link de.monticore.montifun._ast.ASTMF_apat}
   * E.g. "42" to "42"
   * E.g. "Just 42" to "(Just 42)"
   *
   * @return the converted value
   */
  public ASTMF_apat asASTMF_apat() {
    if (isPresentMF_apat()) {
      return getMF_apat();
    }
    return new ASTMF_apatBuilder()
        .setMF_pat(this)
        .build();
  }

}
