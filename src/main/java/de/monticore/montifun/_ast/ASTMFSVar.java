/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFSVar extends ASTMFSVarTOP{
    @Override
    public String getName(){
        return getMF_var().getName();
    }
}
