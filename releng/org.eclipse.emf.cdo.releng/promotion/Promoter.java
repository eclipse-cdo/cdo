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
package org.eclipse.emf.cdo.releng.promotion;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Promoter
{
  private static String hudsonJobsDir;

  private static String jobName;

  public static void main(String[] args) throws IOException
  {
    if (args.length != 4)
    {
      System.err.println("Specify exactly four arguments, "
          + "e.g. Promoter /path/to/hudson/jobs hudson-job-name last-build-number next-build-number");
      System.exit(2);
    }

    hudsonJobsDir = args[0];
    jobName = args[1];

    int lastBuildNumber = Integer.parseInt(args[2]);
    int nextBuildNumber = Integer.parseInt(args[3]);

    File hudsonJob = new File(hudsonJobsDir, jobName);
    File builds = new File(hudsonJob, "builds");
    for (int buildNumber = lastBuildNumber; buildNumber < nextBuildNumber; buildNumber++)
    {
      File build = new File(builds, String.valueOf(buildNumber));
      if (build.exists())
      {
        promote(build);
      }
    }

    // FileOutputStream out = null;
    //
    // try
    // {
    // out = new FileOutputStream(fileName);
    // PrintStream stream = new PrintStream(out);
    // stream.println("build.qualifier=" + buildQualifier);
    // stream.flush();
    // }
    // finally
    // {
    // if (out != null)
    // {
    // out.close();
    // }
    // }
  }

  private static void promote(File build)
  {
    System.out.println("Promoting " + jobName + "#" + build.getName());
  }
}
