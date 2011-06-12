/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Eike Stepper
 */
public class GenerateBuildQualifier
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 3)
    {
      System.err
          .println("Specify exactly three arguments, e.g. GenerateBuildQualifier R 2011-06-11_09-55-11 /path/to/build-qualifier.properties");
      System.exit(2);
    }

    String buildType = args[0];
    String buildTimestamp = args[1];
    String buildQualifier = generateBuildQualifier(buildType, buildTimestamp);

    String fileName = args[2];
    FileOutputStream out = null;

    try
    {
      out = new FileOutputStream(fileName);
      PrintStream stream = new PrintStream(out);
      stream.println("build.qualifier=" + buildQualifier);
      stream.flush();
    }
    finally
    {
      if (out != null)
      {
        out.close();
      }
    }
  }

  private static String generateBuildQualifier(String buildType, String buildTimestamp)
  {
    return buildType + buildTimestamp.substring(0, 4) + buildTimestamp.substring(5, 7)
        + buildTimestamp.substring(8, 10) + "-" + buildTimestamp.substring(11, 13) + buildTimestamp.substring(14, 16);
  }
}
