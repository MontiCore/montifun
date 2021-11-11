/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import java.util.stream.Collectors;

public class ASTMF_modid extends ASTMF_modidTOP {

    public String getName() {
        if (sizeMFLargeNames() > 0) {
            return getMFLargeName(sizeMFLargeNames() - 1);
        }
        return "";
    }

    public String getFullName() {
        return streamMFLargeNames().collect(Collectors.joining("."));
    }

}
