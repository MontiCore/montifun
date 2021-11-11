/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_module extends ASTMF_moduleTOP {

    @Override
    public String getName() {
        if (mF_modid.isPresent()) {
            mF_modid.get().getName();
        }
        return "Main";
    }

}
