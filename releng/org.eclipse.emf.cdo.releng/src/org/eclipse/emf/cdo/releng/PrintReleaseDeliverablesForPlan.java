/*
 * Copyright (c) 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class PrintReleaseDeliverablesForPlan
{
  public static void main(String[] args) throws IOException
  {
    List<String> lines = new ArrayList<>();
    for (File featureFolder : new File("../../features").listFiles())
    {
      String folderName = featureFolder.getName();
      if (folderName.equals("org.eclipse.emf.cdo.site-feature") || folderName.equals("org.eclipse.emf.cdo.license-feature"))
      {
        continue;
      }

      File propertiesFile = new File(featureFolder, "feature.properties");
      InputStream in = null;

      try
      {
        in = new FileInputStream(propertiesFile);

        Properties properties = new Properties();
        properties.load(in);

        String name = properties.getProperty("featureName");
        String description = properties.getProperty("description");

        lines.add("  <html:li><html:b>" + name + "</html:b> (" + description + ")</html:li>");
      }
      finally
      {
        if (in != null)
        {
          in.close();
        }
      }
    }

    Collections.sort(lines);

    System.out.println("<html:ul>");
    for (String line : lines)
    {
      System.out.println(line);
    }

    System.out.println("</html:ul>");
  }
}
