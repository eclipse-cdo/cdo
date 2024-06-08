/*
 * Copyright (c) 2017, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class PrintUnpublishedComponents
{
  public static final String DROP = "I20120914-0410";

  private static final Set<String> features = new HashSet<String>()
  {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString()
    {
      return " (feature)";
    }
  };

  private static final Set<String> plugins = new HashSet<String>()
  {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString()
    {
      return " (plugin)";
    }
  };

  public static void main(String[] args) throws IOException
  {
    initDrop();

    checkComponents("../../features", features);
    checkComponents("../../plugins", plugins);
  }

  private static void initDrop() throws MalformedURLException, IOException
  {
    URL url = URI.create("http://download.eclipse.org/modeling/emf/cdo/drops/" + DROP + "/index.xml").toURL();
    InputStream stream = url.openStream();

    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

      String line;
      while ((line = reader.readLine()) != null)
      {
        String prefix = "<element name=\"";
        int start = line.indexOf(prefix);
        if (start != -1)
        {
          line = line.substring(start + prefix.length());
          if (line.endsWith("type=\"org.eclipse.update.feature\"/>"))
          {
            features.add(line.substring(0, line.indexOf('"')));
          }
          else if (line.endsWith("type=\"osgi.bundle\"/>"))
          {
            plugins.add(line.substring(0, line.indexOf('"')));
          }
        }
      }
    }
    finally
    {
      if (stream != null)
      {
        stream.close();
      }
    }
  }

  private static void checkComponents(String folder, Set<String> publishedComponents)
  {
    for (File file : new File(folder).listFiles())
    {
      String id = file.getName();
      if (id.equals("org.eclipse.emf.cdo.site-feature") || id.equals("org.eclipse.emf.cdo.license-feature"))
      {
        continue;
      }

      final String FEATURE_SUFFIX = "-feature";
      if (id.endsWith(FEATURE_SUFFIX))
      {
        id = id.substring(0, id.length() - FEATURE_SUFFIX.length());
      }

      if (!publishedComponents.contains(id))
      {
        System.out.println(id + publishedComponents);
      }
    }
  }
}
