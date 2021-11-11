/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.Arrays;
import java.util.List;

import static de.monticore.montifun._ast.ASTMFPREDEFINEDCONSTRUCTOR.FUNCTION;

public class MF_typeInfixFunc2PrefixVisitor implements MontiFunVisitor2 {

  @Override
  public void endVisit(ASTMF_type node) {
    //From: A -> B
    //To:   (->) A B
    //"B" is in this case the MF_type
    //"A" is the MF_atype
    if (node.isPresentMF_type()) {
      //as we are in endVisit, the MFType does not contain another MFType,
      //as we have visited it earlier
      ASTMF_atype rightType = MF_atypeListToSingle(node.getMF_type().getMF_atypeList());
      node.setMF_typeAbsent();
      //type application? -> wrap in a new MF_type
      //e.g. "A B -> C" to "(->) (A B) C"
      ASTMF_atype leftType =
          MF_atypeListToSingle(node.getMF_atypeList());
      // "(->) A B"
      node.setMF_atypeList(Arrays.asList(
          new ASTMF_atypeBuilder()
              .setMF_gtycon(new ASTMF_gtyconBuilder().setPredefinedConstructor(FUNCTION).build())
              .build(),
          leftType,
          rightType
      ));
    }
  }


  public static ASTMF_atype MF_atypeListToSingle(List<ASTMF_atype> list) {
    //type application? -> wrap in a new MF_type
    //e.g. "A B" to "(A B)"
    return list.size() == 1 ?
        list.get(0) :
        new ASTMF_atypeBuilder()
            .setMF_type(
                new ASTMF_typeBuilder()
                    .setMF_atypesList(list)
                    .build()
            )
            .build();
  }

}
