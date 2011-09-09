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
package org.eclipse.emf.cdo.releng.doc.article.util;

import com.sun.javadoc.RootDoc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class JavaDoc
{
  public static final String OPTION_BASE_FOLDER = "-basefolder";

  public static final String OPTION_OUTPUT_PATH = "-outputpath";

  private final Class<?> doclet;

  private final List<String> sourcePaths = new ArrayList<String>();

  private final List<String> subPackages = new ArrayList<String>();

  private final List<String> classPaths = new ArrayList<String>();

  public JavaDoc(Class<?> doclet)
  {
    this.doclet = doclet;
  }

  public final JavaDoc sourcePath(String sourcePath)
  {
    sourcePaths.add(sourcePath);
    return this;
  }

  public final JavaDoc subPackage(String subPackage)
  {
    subPackages.add(subPackage);
    return this;
  }

  public final JavaDoc classPath(String classPath)
  {
    classPaths.add(classPath);
    return this;
  }

  public JavaDoc start(String baseFolder, String outputPath)
  {
    List<String> arguments = new ArrayList<String>();
    arguments.add("-private");

    if (!classPaths.isEmpty())
    {
      arguments.add("-classpath");
      arguments.add(addListArgument(classPaths, baseFolder));
    }

    if (!sourcePaths.isEmpty())
    {
      arguments.add("-sourcepath");
      arguments.add(addListArgument(sourcePaths, baseFolder));
    }

    for (String subPackage : subPackages)
    {
      arguments.add("-subpackages");
      arguments.add(subPackage);
    }

    arguments.add(OPTION_BASE_FOLDER);
    arguments.add(baseFolder);

    arguments.add(OPTION_OUTPUT_PATH);
    arguments.add(outputPath);

    String[] array = arguments.toArray(new String[arguments.size()]);
    com.sun.tools.javadoc.Main.execute(doclet.getSimpleName(), doclet.getName(), array);
    return this;
  }

  private String addListArgument(List<String> elements, String baseFolder)
  {
    StringBuilder builder = new StringBuilder();
    for (String element : elements)
    {
      if (builder.length() != 0)
      {
        builder.append(';');
      }

      try
      {
        File file = new File(element);
        if (file.isAbsolute())
        {
          element = file.getCanonicalPath();
        }
        else
        {
          element = new File(baseFolder, element).getCanonicalPath();
        }
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }

      builder.append(element);
    }

    return builder.toString();
  }

  public static int optionLength(String option)
  {
    if (OPTION_BASE_FOLDER.equals(option))
    {
      return 2;
    }

    if (OPTION_OUTPUT_PATH.equals(option))
    {
      return 2;
    }

    // Indicate we don't know about it
    return -1;
  }

  public static String getOption(RootDoc root, String optionName)
  {
    String[][] options = root.options();
    for (String[] option : options)
    {
      if (optionName.equalsIgnoreCase(option[0]))
      {
        // TODO Multi-argument options?
        return option[1];
      }
    }

    return "";
  }
}
