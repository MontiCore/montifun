/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._symboltable;

import de.monticore.montifun.MontiFunMill;
import de.monticore.montifun._ast.*;
import de.monticore.montifun.prettyprinter.CorePrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MFFunSymbolDeSer extends MFFunSymbolDeSerTOP {

  @Override
  protected void serializeTypeSignature(ASTMFTypeSignatureGenDecl typeSignature, MontiFunSymbols2Json s2j) {
    String serialized = CorePrinter.toString(typeSignature);
    s2j.printer.member("signature", serialized);
    //todo when we have types, this should use them instead of one text field
  }

  @Override
  protected ASTMFTypeSignatureGenDecl deserializeTypeSignature(JsonObject symbolJson) {
    try {
      return MontiFunMill.parser()
          .parse_StringMFTypeSignatureGenDecl(symbolJson.getStringMember("signature"))
          .get();
    } catch (IOException | NoSuchElementException e) {
      Log.error("0xFF010 unable do deserialize the function signature \""
          + symbolJson.getStringMember("signature")
          + "\": "
          + e
      );
      return null;
    }
  }

  @Override
  protected void serializeFixity (Optional<ASTMFFixity> fixity, MontiFunSymbols2Json s2j) {
    if(fixity.isPresent()){
      s2j.printer.beginObject("fixity");
      s2j.printer.member("associativity", fixity.get().getAssociativity().name());
      s2j.printer.member("precedence", fixity.get().getPrecedence().getMFInteger());
      s2j.printer.endObject();
    }
  }

  @Override
  protected Optional<ASTMFFixity> deserializeFixity (JsonObject symbolJson){
    if(!symbolJson.hasObjectMember("fixity")){
      return Optional.empty();
    }
    symbolJson = symbolJson.getObjectMember("fixity");
    ASTMFFixityBuilder builder = new ASTMFFixityBuilder();
    builder.setAssociativity(ASTOP_ASSOCIATIVITY.valueOf(
        symbolJson.getStringMember("associativity"))
    );
    builder.setPrecedence(
        new ASTMF_integerBuilder().setMFInteger(
            symbolJson.getIntegerMember("precedence")
        ).build()
    );
    return Optional.of(builder.build());
  }



}
