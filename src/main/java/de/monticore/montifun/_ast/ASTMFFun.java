/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFFun extends ASTMFFunTOP{

    @Override
    public String getName(){
       return getSymbol().getName();
    }
}
