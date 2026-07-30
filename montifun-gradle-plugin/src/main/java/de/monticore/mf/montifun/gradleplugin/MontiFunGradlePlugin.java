/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static de.se_rwth.commons.StringTransformations.capitalize;

public class MontiFunGradlePlugin implements Plugin<Project> {

  public static final String CONFIG_TOOL = "montifunTool";

  public static final String GROUP_NAME = "montifun";

  @Override
  public void apply(Project project) {
    // collect data
    String version = loadVersion();
    Directory projectDir = project.getLayout().getProjectDirectory();
    DirectoryProperty buildDir = project.getLayout().getBuildDirectory();

    // assure the Java Plugin is applied
    project.getPluginManager().apply(JavaLibraryPlugin.class);

    Configuration toolConfig =
        project.getConfigurations().maybeCreate(CONFIG_TOOL);
    toolConfig.setCanBeResolved(true);

    toolConfig.defaultDependencies(dependencies ->
        dependencies.add(project.getDependencies().create(
            "de.monticore.lang:montifun:" + version
        ))
    );

    SourceSetContainer javaSourceSets = project.getExtensions()
        .getByType(JavaPluginExtension.class)
        .getSourceSets();

    javaSourceSets.configureEach(sourceSet -> {
      String taskName = getTaskName(sourceSet);

      TaskProvider<MontiFunTask> generateTask = project.getTasks().register(
          taskName, MontiFunTask.class, genTask -> {
            genTask.setGroup(GROUP_NAME);
            String inputDirRelative =
                "src/" + sourceSet.getName() + "/montifun";
            String buildDirRelative =
                getMontiFunBuildDir(sourceSet);
            genTask.setDescription(
                "Generates Java code and symbol tables from MontiFun models"
                    + " in " + inputDirRelative
            );
            // directories
            genTask.getInput().from(projectDir.dir(inputDirRelative));
            genTask.getSymbolOutputDir().set(
                buildDir.dir(buildDirRelative + "/symbols")
            );
            genTask.getJavaOutputDir().set(
                buildDir.dir(buildDirRelative + "/java")
            );
            genTask.getOutputDir().set(genTask.getJavaOutputDir());

            genTask.getGenerateSymbolTables().convention(true);
            genTask.getGenerateJava().convention(true);
            genTask.getExtraClasspathElements().from(toolConfig);
          });

      // link this task to Java
      sourceSet.getJava().srcDir(
          generateTask.flatMap(MontiFunTask::getJavaOutputDir)
      );
      project.getTasks().named(sourceSet.getCompileJavaTaskName()).configure(
          compileTask -> compileTask.dependsOn(generateTask)
      );
    });

    // test depends on main
    String mainTaskName =
        getTaskName(javaSourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME));
    TaskProvider<MontiFunTask> mainTask =
        project.getTasks().named(mainTaskName, MontiFunTask.class);
    String testTaskName =
        getTaskName(javaSourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME));
    TaskProvider<MontiFunTask> testTask =
        project.getTasks().named(testTaskName, MontiFunTask.class);
    testTask.configure(task -> {
      task.dependsOn(mainTask);
      task.getSymbolInputDirs().from(mainTask.flatMap(MontiFunTask::getSymbolOutputDir));
    });

  }

  protected String loadVersion() {
    Properties properties = new Properties();
    try {
      InputStream resources = getClass().getClassLoader()
          .getResourceAsStream("buildInfo.properties");
      properties.load(resources);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties.getProperty("version");
  }

  protected String getTaskName(SourceSet sourceSet) {
    return sourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME) ?
        "generateMontiFun" :
        "generate" + capitalize(sourceSet.getName()) + "MontiFun";
  }

  protected String getMontiFunBuildDir(SourceSet sourceSet) {
    String genDir =
        sourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME) ?
            "generated-sources" :
            "generated-" + sourceSet.getName() + "-sources";
    return genDir + "/montifun";
  }

}
