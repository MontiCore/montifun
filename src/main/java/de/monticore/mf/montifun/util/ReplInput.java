// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import java.util.Scanner;

/**
 * Handles IO for a Repl.
 */
public class ReplInput implements AutoCloseable {

  protected Scanner scanner;

  public ReplInput() {
    scanner = new Scanner(System.in);
  }

  /**
   * reads one non-empty user input
   *
   * @return the input of the user (may be multi-line)
   */
  public String readUserInput() {
    while (true) {
      StringBuilder currentCommandBuilder = new StringBuilder();
      boolean lastLine = false;
      do {
        System.out.print(currentCommandBuilder.isEmpty() ? "> " : "  ");
        System.out.flush();

        String line = scanner.nextLine();
        lastLine = !line.endsWith("\\");
        if (!lastLine) {
          line = line.substring(0, line.length() - 1)
              .trim()
              + System.lineSeparator();
        }
        currentCommandBuilder.append(line);
      } while (!lastLine);

      // check if someone was just pressing enter
      String currentCommand = currentCommandBuilder.toString();
      if (!currentCommand.trim().isEmpty()) {
        return currentCommand;
      }
    }
  }

  @Override
  public void close() {
    if (scanner != null) {
      scanner.close();
    }
  }

}
