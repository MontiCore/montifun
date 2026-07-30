/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.internal.isolation.CachedIsolation;

public abstract class MontiFunAction extends AToolAction {

  protected static final CachedIsolation.WithClassPath isolator =
      new CachedIsolation.WithClassPath();

  @Override
  protected void doRun(String[] args) {
    final String prefix = "[" + getParameters().getProgressName().get() + "] ";
    isolator.executeInClassloader(
        MontiFunToolInvoker.class.getName(),
        "run",
        args,
        prefix,
        getParameters().getExtraClasspathElements())
    ;
  }

}
