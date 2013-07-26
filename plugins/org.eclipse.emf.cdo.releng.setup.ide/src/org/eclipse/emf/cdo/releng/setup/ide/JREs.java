/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.JRE;
import org.eclipse.emf.cdo.releng.setup.helper.OS;

import java.io.File;

/**
 * @author Eike Stepper
 */
public final class JREs
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  public static void init() throws Exception
  {
    File root = new File(OS.INSTANCE.getJREsRoot());
    if (!root.isDirectory())
    {
      return;
    }

    JRE javaVersion = CONTEXT.getSetup().getBranch().getJavaVersion();
    if (javaVersion == null)
    {
      return;
    }

    String version = "jdk" + getVersion(javaVersion);
    File highestVersion = getHighestVersion(root, version);
    if (highestVersion != null)
    {
      Preferences.set("instance/org.eclipse.jdt.core/org.eclipse.jdt.core.classpathVariable.JRE_LIB", new File(
          highestVersion, "jre/lib/rt.jar").getAbsolutePath().replace('\\', '/'));

      Preferences.set("instance/org.eclipse.jdt.core/org.eclipse.jdt.core.classpathVariable.JRE_SRC", new File(
          highestVersion, "src.zip").getAbsolutePath().replace('\\', '/'));
    }
  }

  private static String getVersion(JRE javaVersion)
  {
    switch (javaVersion)
    {
    case JRE_13:
      return "1.3";

    case JRE_14:
      return "1.4";

    case JRE_15:
      return "1.5";

    case JRE_16:
      return "1.6";

    case JRE_17:
      return "1.7";

    case JRE_18:
      return "1.8";

    default:
      throw new IllegalArgumentException("Illegal java version: " + javaVersion);
    }
  }

  private static File getHighestVersion(File root, String version)
  {
    File highestVersion = null;
    for (File file : root.listFiles())
    {
      if (file.isDirectory())
      {
        String name = file.getName();
        if (name.startsWith(version))
        {
          if (highestVersion == null || highestVersion.getName().compareTo(name) < 0)
          {
            highestVersion = file;
          }
        }
      }
    }
    return highestVersion;
  }
}
