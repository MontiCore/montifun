/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._symboltable;

import de.monticore.montifun._ast.ASTMFFun;
import de.monticore.montifun._ast.ASTMFFunBuilder;
import de.monticore.symboltable.serialization.json.JsonElement;

import java.util.List;
import java.util.stream.Collectors;

public class MF_moduleSymbolDeSer extends MF_moduleSymbolDeSerTOP {

  @Override
  protected void serializeFunctions(List<ASTMFFun> functions, MontiFunSymbols2Json s2j) {
    s2j.getJsonPrinter().beginArray();
    for (ASTMFFun function : functions) {
      s2j.getJsonPrinter().member("name", function.getName());
      //todo type signature
      //todo fixity
    }
    s2j.getJsonPrinter().endArray();
  }

  protected List<de.monticore.montifun._ast.ASTMFFun> deserializeFunctions(de.monticore.symboltable.serialization.json.JsonObject symbolJson) {
    return symbolJson.getAsJsonArray().getValues().stream()
        .map(JsonElement::getAsJsonObject)
        .map(
       jsonFunction -> new ASTMFFunBuilder()
           .setName(jsonFunction.getMember("name").getAsJsonString().toString())
           //todo type signature
           //todo fixity
           .build()
    ).collect(Collectors.toList());
  }

}
