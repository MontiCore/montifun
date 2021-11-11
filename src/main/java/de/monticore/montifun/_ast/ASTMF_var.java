/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_var extends ASTMF_varTOP{

    public String getName(){
        if(isPresentMF_varid()){
            return getMF_varid().getMFSmallName();
        }
        if(isPresentMF_varsym()){
            return getMF_varsym().getMFTVarsym();
        }
        Log.error("0xMF001 Unexpected ASTMF_var");
        return "ERROR NAME ASTMF_var";
    }
}
