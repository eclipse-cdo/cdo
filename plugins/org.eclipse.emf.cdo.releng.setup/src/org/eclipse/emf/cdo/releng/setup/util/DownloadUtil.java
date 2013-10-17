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

import org.eclipse.emf.cdo.releng.setup.util.log.ProgressLog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Eike Stepper
 */
public final class DownloadUtil
{
  private static final int BUFFER_SIZE = 4096;

  public static File downloadURL(String url, ProgressLog progress)
  {
    if (url.endsWith("/"))
    {
      url = url.substring(0, url.length() - 1);
    }

    try
    {
      String name = encodeFilename(url);
      File tmp = File.createTempFile(name + "-", ".part");
      File file = new File(tmp.getParentFile(), name + ".zip");
      if (!file.exists())
      {
        downloadURL(url, tmp, progress);
        tmp.renameTo(file);
      }

      return file;
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private static void downloadURL(String url, File file, ProgressLog progress)
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
          progress.log("Downloading " + fileName + " (" + percent + "%)");
        }
      }
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
    finally
    {
      OS.close(out);
      OS.close(in);
    }
  }

  private static String encodeFilename(String url)
  {
    StringBuilder builder = new StringBuilder(url);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      if (!(Character.isLetter(c) || Character.isDigit(c)))
      {
        builder.setCharAt(i, '_');
      }
    }

    return builder.toString();
  }
}
