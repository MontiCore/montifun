/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.gradleplugin;

import de.monticore.gradle.common.AToolAction;
import de.monticore.gradle.common.MCAllFilesTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@CacheableTask
public abstract class MontiFunTask extends MCAllFilesTask {

  static public final String TASK_NAME = MontiFunTask.class.getName();

  public MontiFunTask() {
    super(TASK_NAME, null);
    getMainClass().convention("MontiFunTool");
  }

  @Optional
  @OutputDirectory
  public abstract DirectoryProperty getSymbolOutputDir();

  @Optional
  @OutputDirectory
  public abstract DirectoryProperty getJavaOutputDir();

  @Optional
  @Input
  public abstract Property<Boolean> getGenerateSymbolTables();

  @Optional
  @Input
  public abstract Property<Boolean> getGenerateJava();

  @Optional
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getSymbolInputDirs();

  @Override
  protected List<String> createArgList(Function<Path, String> handlePath) {
    // not using super.createArgList() here,
    // as that adds several options unknown
    var list = new ArrayList<String>();

    // input
    for (var f : getInput().getAsFileTree()) {
      if (f.isFile()) {
        list.add("-i");
        list.add(handlePath.apply(f.toPath()));
      }
    }
    for (var f : getSymbolInputDirs().getFiles()) {
      if (f.isDirectory()) {
        list.add("-p");
        list.add(f.getAbsolutePath());
      }
    }

    // output
    if (getGenerateSymbolTables().getOrElse(true) &&
        getSymbolOutputDir().isPresent()
    ) {
      list.add("-s");
      list.add(getSymbolOutputDir().get().getAsFile().getAbsolutePath());
    }
    if (getGenerateJava().getOrElse(true) &&
        getJavaOutputDir().isPresent()
    ) {
      list.add("-gen");
      list.add(getJavaOutputDir().get().getAsFile().getAbsolutePath());
    }
    return list;
  }

  @Override
  public void startGeneration(List<String> args, String progressName) {
    if (this.getInput().getAsFileTree().getFiles().isEmpty())
      return;
    super.startGeneration(args, progressName);
  }

  @Override
  protected Class<? extends AToolAction> getToolAction() {
    return MontiFunAction.class;
  }

  @Override
  protected Consumer<String[]> getRunMethod() {
    return MontiFunToolInvoker::run;
  }

}
