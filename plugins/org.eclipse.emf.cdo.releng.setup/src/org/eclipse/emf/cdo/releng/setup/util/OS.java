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
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.core.runtime.Platform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class OS
{
  public static final OS INSTANCE = create();

  public boolean isLineEndingConversionNeeded()
  {
    return false;
  }

  public List<String> readText(File file)
  {
    FileInputStream in = null;

    try
    {
      in = new FileInputStream(file);
      Reader reader = new InputStreamReader(in);
      BufferedReader bufferedReader = new BufferedReader(reader);

      List<String> lines = new ArrayList<String>();
      String line;
      while ((line = bufferedReader.readLine()) != null)
      {
        lines.add(line);
      }

      return lines;
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    finally
    {
      close(in);
    }
  }

  public void writeText(File file, List<String> lines)
  {
    String nl = System.getProperty("line.separator");
    FileOutputStream out = null;

    try
    {
      file.getParentFile().mkdirs();

      out = new FileOutputStream(file);
      Writer writer = new OutputStreamWriter(out);
      @SuppressWarnings("resource")
      BufferedWriter bufferedWriter = new BufferedWriter(writer);

      for (String line : lines)
      {
        bufferedWriter.write(line);
        bufferedWriter.write(nl);
      }

      bufferedWriter.flush();
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    finally
    {
      close(out);
    }
  }

  public abstract String getEclipseExecutable();

  public abstract String getEclipseIni();

  public abstract String getGitPrefix();

  public abstract String getJREsRoot();

  public static void close(Closeable closeable)
  {
    if (closeable != null)
    {
      try
      {
        closeable.close();
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }
  }

  private static OS create()
  {
    String os = Platform.getOS();

    if (Platform.OS_WIN32.equals(os))
    {
      return new Win();
    }

    if (Platform.OS_MACOSX.equals(os))
    {
      return new Mac();
    }

    if (Platform.OS_LINUX.equals(os))
    {
      return new Linux();
    }

    throw new IllegalStateException("Operating system not supported: " + os);
  }

  /**
   * @author Eike Stepper
   */
  private static final class Win extends OS
  {
    @Override
    public boolean isLineEndingConversionNeeded()
    {
      return true;
    }

    @Override
    public String getEclipseExecutable()
    {
      return "eclipse.exe";
    }

    @Override
    public String getEclipseIni()
    {
      return "eclipse.ini";
    }

    @Override
    public String getGitPrefix()
    {
      return "C:\\Program Files (x86)\\Git";
    }

    @Override
    public String getJREsRoot()
    {
      return Platform.getOSArch().endsWith("_64") ? "C:\\Program Files\\Java" : "C:\\Program Files (x86)\\Java";
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Mac extends OS
  {
    @Override
    public String getEclipseExecutable()
    {
      return "Eclipse.app/Contents/MacOS/eclipse";
    }

    @Override
    public String getEclipseIni()
    {
      return "Contents/MacOS/eclipse.ini";
    }

    @Override
    public String getGitPrefix()
    {
      return "/";
    }

    @Override
    public String getJREsRoot()
    {
      return "/";
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class Linux extends OS
  {
    @Override
    public String getEclipseExecutable()
    {
      return "eclipse";
    }

    @Override
    public String getEclipseIni()
    {
      return "eclipse.ini";
    }

    @Override
    public String getGitPrefix()
    {
      return "";
    }

    @Override
    public String getJREsRoot()
    {
      return "";
    }
  }
}
