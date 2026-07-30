/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.gradleplugin;

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.io.CleanupMode.ON_SUCCESS;

class MontiFunGradlePluginTest {

  @TempDir(cleanup = ON_SUCCESS)
  File projectDir;

  @ParameterizedTest
  @ValueSource(strings = { "8.14", "9.5.1" })
  void includesMainAndTestMontiFunSources(String gradleVersion) throws IOException {
    FileUtils.copyDirectory(
        new File(".."),
        projectDir,
        file -> !file.getName().equals(".gradle") &&
            !file.getName().equals(".git") &&
            !file.getName().equals("build") &&
            !file.getName().equals("target")
    );
    File includingDir = new File(projectDir,
        "montifun-gradle-plugin/montifun-gradle-plugin-it"
    );

    BuildResult result = GradleRunner.create()
        .withPluginClasspath()
        .withGradleVersion(gradleVersion)
        .withProjectDir(includingDir)
        .withArguments(getGradleArguments())
        .build();

    System.out.println(result.getOutput());

    assertEquals(
        TaskOutcome.SUCCESS,
        result.task(":generateMontiFun").getOutcome()
    );
    assertEquals(
        TaskOutcome.SUCCESS,
        result.task(":compileJava").getOutcome()
    );
    assertEquals(
        TaskOutcome.SUCCESS,
        result.task(":compileJava").getOutcome()
    );

  }

  protected List<String> getGradleArguments() {
    List<String> ret = new ArrayList<>();
    ret.add(":generateMontiFun");
    ret.add(":build");
    ret.add("--info");
    ret.add("--stacktrace");

    @Nullable String mavenRepo = System.getProperty("maven.repo.local");
    if (mavenRepo != null && !mavenRepo.isEmpty()) {
      ret.add("-Dmaven.repo.local=" + mavenRepo);
    }

    @Nullable String useLocalRepo = System.getProperty("useLocalRepo");
    if (useLocalRepo != null && !useLocalRepo.isEmpty()) {
      ret.add("-PuseLocalRepo=" + useLocalRepo);
    }

    String projVersion = loadProperties().getProperty("version");
    File langLibs = new File("../target/libs");
    File montiFunJarFile =
        new File(langLibs, "montifun-" + projVersion + ".jar")
            .getAbsoluteFile();
    assertTrue(montiFunJarFile.exists());

    ret.add("-Pversion=" + projVersion);
    ret.add("-PmontifunJarFile=" + montiFunJarFile);

    return ret;
  }

  protected Properties loadProperties() {
    Properties properties = new Properties();
    try {
      properties.load(this.getClass().getClassLoader().getResourceAsStream("buildInfo.properties"));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

}
