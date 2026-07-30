/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.gradleplugin;

import de.monticore.gradle.common.GradleLog;
import de.monticore.mf.montifun.MontiFunTool;
import de.se_rwth.commons.logging.Log;
import java.util.Arrays;

public class MontiFunToolInvoker {
  public static void run(String[] args) {
    GradleLog.init();
    Log.info("Starting MontiFunTool:\n\t  java -jar MontiFunTool.jar " + Arrays.toString(args),
        MontiFunToolInvoker.class.getName());
    MontiFunTool.gradleMain(args);
  }
}
