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
package org.eclipse.emf.cdo.releng.setup.helper;

import org.eclipse.core.runtime.Platform;

import java.io.BufferedInputStream;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class OS
{
  private static final int BUFFER_SIZE = 4096;

  public static final OS INSTANCE = create();

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
      out = new FileOutputStream(file);
      Writer writer = new OutputStreamWriter(out);
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

  public File downloadURL(String url)
  {
    if (url.endsWith("/"))
    {
      url = url.substring(0, url.length() - 1);
    }

    StringBuilder builder = new StringBuilder(url);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      if (!(Character.isLetter(c) || Character.isDigit(c)))
      {
        builder.setCharAt(i, '_');
      }
    }

    String name = builder.toString();

    try
    {
      File tmp = File.createTempFile(name + "-", ".part");
      File file = new File(tmp.getParentFile(), name);
      if (!file.exists())
      {
        downloadURL(url, tmp);
        tmp.renameTo(file);
      }

      return file;
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public void downloadURL(String url, File file)
  {
    final byte data[] = new byte[BUFFER_SIZE];
    BufferedInputStream in = null;
    FileOutputStream out = null;

    try
    {
      URLConnection connection = new URL(url).openConnection();
      if (connection instanceof HttpURLConnection)
      {
        connection.connect();

        int result = ((HttpURLConnection)connection).getResponseCode();
        if (result >= 400)
        {
          throw new IOException("HTTP error " + result);
        }
      }

      int length = connection.getContentLength();
      float factor = 100f / length;
      String fileName = new File(connection.getURL().getFile()).getName();

      in = new BufferedInputStream(connection.getInputStream());
      out = new FileOutputStream(file);

      int lastPercent = 0;
      int read = 0;
      int n;
      while ((n = in.read(data, 0, BUFFER_SIZE)) != -1)
      {
        out.write(data, 0, n);
        read += n;

        int percent = Math.round(factor * read);
        if (percent != lastPercent)
        {
          Progress.log().addLine("Downloading " + fileName + " (" + percent + "%)");
        }
      }
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    finally
    {
      close(out);
      close(in);
    }
  }

  public abstract String getEclipseExecutable();

  public abstract String getGitPrefix();

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

  private static void close(Closeable closeable)
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

  /**
   * @author Eike Stepper
   */
  private static final class Win extends OS
  {
    @Override
    public String getEclipseExecutable()
    {
      return "eclipse.exe";
    }

    @Override
    public String getGitPrefix()
    {
      return "C:\\Program Files (x86)\\Git";
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
    public String getGitPrefix()
    {
      return "C:\\Program Files (x86)\\Git";
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
    public String getGitPrefix()
    {
      return "";
    }
  }
}
