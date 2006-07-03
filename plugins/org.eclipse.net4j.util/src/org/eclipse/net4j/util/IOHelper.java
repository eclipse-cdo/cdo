/***************************************************************************
 * Copyright (c) 2004-2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


import java.util.Date;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;


public class IOHelper
{
  public static String currentDirectory()
  {
    String path = new File(".").getAbsolutePath();
    return path.substring(0, path.length() - 2);
  }

  /**
   * Random number generator to make unique file name
   */
  private static final Random RANDOM_GEN = new Random(System.currentTimeMillis());

  /**
   * Get a <code>BufferedInputStream</code>.
   */
  public static BufferedInputStream getBufferedInputStream(InputStream in)
  {
    BufferedInputStream bin = null;
    if (in instanceof java.io.BufferedInputStream)
    {
      bin = (BufferedInputStream) in;
    }
    else
    {
      bin = new BufferedInputStream(in);
    }
    return bin;
  }

  /**
   * Get a <code>BufferedOutputStream</code>.
   */
  public static BufferedOutputStream getBufferedOutputStream(OutputStream out)
  {
    BufferedOutputStream bout = null;
    if (out instanceof java.io.BufferedOutputStream)
    {
      bout = (BufferedOutputStream) out;
    }
    else
    {
      bout = new BufferedOutputStream(out);
    }
    return bout;
  }

  /**
   * Get <code>BufferedReader</code>.
   */
  public static BufferedReader getBufferedReader(Reader rd)
  {
    if (rd instanceof java.io.BufferedReader)
    {
      return (BufferedReader) rd;
    }
    else
    {
      return new BufferedReader(rd);
    }
  }

  /**
   * Get <code>BufferedWriter</code>.
   */
  public static BufferedWriter getBufferedWriter(Writer wr)
  {
    BufferedWriter bw = null;
    if (wr instanceof java.io.BufferedWriter)
    {
      bw = (BufferedWriter) wr;
    }
    else
    {
      bw = new BufferedWriter(wr);
    }
    return bw;
  }

  /**
   * Get unique file object.
   */
  public static File getUniqueFile(File oldFile)
  {
    File newFile = oldFile;

    while (true)
    {
      if (!newFile.exists())
      {
        break;
      }

      newFile = new File(oldFile.getAbsolutePath() + '.' + Math.abs(RANDOM_GEN.nextLong()));
    }
    return newFile;
  }

  /**
   * No exception <code>InputStream</code> close method.
   */
  public static void close(InputStream is)
  {
    if (is != null)
    {
      try
      {
        is.close();
      }
      catch (Exception ignore)
      {
        ignore.printStackTrace();
      }
    }
  }

  /**
   * No exception <code>OutputStream</code> close method.
   */
  public static void close(OutputStream os)
  {
    if (os != null)
    {
      try
      {
        os.close();
      }
      catch (Exception ignore)
      {
        ignore.printStackTrace();
      }
    }
  }

  /**
   * No exception <code>java.io.Reader</code> close method.
   */
  public static void close(Reader rd)
  {
    if (rd != null)
    {
      try
      {
        rd.close();
      }
      catch (Exception ignore)
      {
        ignore.printStackTrace();
      }
    }
  }

  /**
   * No exception <code>java.io.Writer</code> close method.
   */
  public static void close(Writer wr)
  {
    if (wr != null)
    {
      try
      {
        wr.close();
      }
      catch (Exception ignore)
      {
        ignore.printStackTrace();
      }
    }
  }

  /**
   * Get exception stack trace.
   */
  public static String getStackTrace(Throwable ex)
  {
    String result = "";
    try
    {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      ex.printStackTrace(pw);
      pw.close();
      sw.close();
      result = sw.toString();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
   * 
   * @param bufferSize
   *          Size of internal buffer to use.
   */
  public static void copy(Reader input, Writer output, int bufferSize) throws IOException
  {
    char buffer[] = new char[bufferSize];
    int n = 0;

    while ((n = input.read(buffer)) != -1)
    {
      output.write(buffer, 0, n);
    }
  }

  public static void copy(InputStream input, OutputStream output, byte buffer[]) throws IOException
  {
    int n = 0;

    while ((n = input.read(buffer)) != -1)
    {
      output.write(buffer, 0, n);
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize)
      throws IOException
  {
    copy(input, output, new byte[bufferSize]);
  }

  public static void copy(InputStream input, OutputStream output) throws IOException
  {
    copy(input, output, 4096);
  }

  public static void copy(File input, File output) throws IOException
  {
    FileInputStream fis = null;
    FileOutputStream fos = null;

    try
    {
      fis = new FileInputStream(input);
      fos = new FileOutputStream(output);

      copy(fis, fos);
    }
    finally
    {
      close(fis);
      close(fos);
    }
  }

  /**
   * Read fully from reader
   */
  public static String readFully(Reader reader) throws IOException
  {
    StringWriter writer = new StringWriter();
    copy(reader, writer, 1024);
    return writer.toString();
  }

  /**
   * Read fully from stream
   */
  public static String readFully(InputStream input) throws IOException
  {
    InputStreamReader reader = new InputStreamReader(input);
    return readFully(reader);
  }

  /**
   * Read fully from file
   */
  public static String readFully(File file) throws IOException
  {
    FileInputStream stream = null;

    try
    {
      stream = new FileInputStream(file);
      return readFully(stream);
    }
    finally
    {
      close(stream);
    }
  }

  public static void log(String message)
  {
    log(message, null);
  }

  public static void log(String message, Throwable t)
  {
    PrintWriter writer = null;

    try
    {
      writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("log.txt", true)));
      writer.println(new SimpleDateFormat().format(new Date()) + " " + message);

      if (t != null)
      {
        t.printStackTrace(writer);
      }
    }
    catch (Exception ignore)
    {
    }
    finally
    {
      if (writer != null)
      {
        try
        {
          writer.close();
        }
        catch (Exception ignore)
        {
        }
      }
    }
  }

  public static File createTempFolder() throws IOException
  {
    return createTempFolder("tmp");
  }

  public static File createTempFolder(String prefix) throws IOException
  {
    return createTempFolder(prefix, "");
  }

  public static File createTempFolder(String prefix, String suffix) throws IOException
  {
    return createTempFolder(prefix, suffix, null);
  }

  public static File createTempFolder(String prefix, String suffix, File directory)
      throws IOException
  {
    File tmp = File.createTempFile(prefix, suffix, directory);
    String tmpPath = tmp.getAbsolutePath();
    tmp.delete();
    tmp = new File(tmpPath);
    tmp.mkdirs();
    return tmp;
  }

  public static void zip(File source, File target)
  {
    zip(source, target, false);
  }

  public static void zip(File source, File target, boolean excludeRoot)
  {
    ZipOutputStream zos = null;

    try
    {
      File root = excludeRoot ? source : source.getParentFile();
      int prefixLength = root.getAbsolutePath().length() + (excludeRoot ? 1 : 0);

      zos = new ZipOutputStream(new FileOutputStream(target));
      zipRecurse(source, prefixLength, zos);
      zos.flush();
    }
    catch (IOException ex)
    {
      throw new UnderlyingIOException("Error while jaring " + source + " to " + target, ex);
    }
    finally
    {
      close(zos);
    }
  }

  public static void unzip(String zipFilePath, String targetPath)
  {
    final int BUFFER = 4096;
    byte data[] = new byte[BUFFER];
    int count;

    BufferedOutputStream dest = null;
    ZipInputStream zis = null;

    try
    {
      new File(targetPath).mkdirs();

      FileInputStream fis = new FileInputStream(zipFilePath);
      zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;

      while ((entry = zis.getNextEntry()) != null)
      {
        File path = new File(targetPath, entry.getName());
        if (entry.isDirectory())
        {
          path.mkdirs();
        }
        else
        {
          path.getParentFile().mkdirs();
          FileOutputStream fos = new FileOutputStream(path);
          dest = new BufferedOutputStream(fos, BUFFER);

          while ((count = zis.read(data, 0, BUFFER)) != -1)
          {
            dest.write(data, 0, count);
          }

          dest.flush();
          dest.close();
          dest = null;
        }
      }
    }
    catch (IOException ex)
    {
      throw new UnderlyingIOException("Error while unzipping " + zipFilePath + " to " + targetPath,
          ex);
    }
    finally
    {
      IOHelper.close(dest);
      IOHelper.close(zis);
    }
  }

  public static void jar(File source, File target)
  {
    jar(source, target, false);
  }

  public static void jar(File source, File target, boolean excludeRoot)
  {
    FileOutputStream fos = null;
    JarOutputStream jos = null;

    try
    {
      Manifest manifest = null;
      File metainfFolder = new File(source, "META-INF");
      File manifestFile = metainfFolder.exists() ? new File(metainfFolder, "MANIFEST.MF") : null;

      if (manifestFile == null)
      {
        manifest = new Manifest();
      }
      else
      {
        FileInputStream fis = null;

        try
        {
          fis = new FileInputStream(manifestFile);
          manifest = new Manifest(fis);
        }
        catch (IOException ex)
        {
          throw ex;
        }
        finally
        {
          close(fis);
        }
      }

      File root = excludeRoot ? source : source.getParentFile();
      int prefixLength = root.getAbsolutePath().length() + 1;

      fos = new FileOutputStream(target);
      jos = new JarOutputStream(fos, manifest);
      jarRecurse(source, prefixLength, jos);
      jos.flush();
    }
    catch (IOException ex)
    {
      throw new UnderlyingIOException("Error while jaring " + source + " to " + target, ex);
    }
    finally
    {
      close(jos);
      close(fos);
    }
  }

  /**
   * 
   * @param file
   * @param impliedPrefix
   * @param jos
   * @throws IOException 
   */

  public static long diskUsage(File file)
  {
    if (file.isFile())
    {
      return file.length();
    }
    else if (file.isDirectory())
    {
      long usage = 0;
      File[] children = file.listFiles();

      for (int i = 0; i < children.length; i++)
      {
        File child = children[i];
        usage += diskUsage(child);
      }

      return usage;
    }

    return 0;
  }

  public static int deleteFile(File file)
  {
    if (file == null)
    {
      return 0;
    }

    int deleted = 0;

    if (file.isDirectory())
    {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++)
      {
        File child = children[i];
        deleted += deleteFile(child);
      }
    }

    return deleted + (file.delete() ? 1 : 0);
  }

  private static void zipRecurse(File file, int prefixLength, ZipOutputStream zos)
      throws IOException
  {
    ZipEntry entry = null;

    try
    {
      String name = file.getAbsolutePath() + (file.isDirectory() ? "/" : "");
      name = name.substring(prefixLength);

      if (name.length() > 0)
      {
        entry = new ZipEntry(name);
        zos.putNextEntry(entry);

        if (file.isFile())
        {
          FileInputStream fis = null;

          try
          {
            fis = new FileInputStream(file);
            copy(fis, zos, 4096);
          }
          finally
          {
            close(fis);
          }
        }
      }
    }
    finally
    {
      if (entry != null)
      {
        zos.closeEntry();
      }
    }

    if (file.isDirectory())
    {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++)
      {
        File child = children[i];
        zipRecurse(child, prefixLength, zos);
      }
    }
  }

  private static void jarRecurse(File file, int prefixLength, JarOutputStream jos)
      throws IOException
  {
    JarEntry entry = null;

    try
    {
      String name = file.getAbsolutePath() + (file.isDirectory() ? "/" : "");
      name = name.substring(prefixLength);

      if (name.length() > 0)
      {
        if (file.isFile() && !name.replace('\\', '/').equals("META-INF/MANIFEST.MF"))
        {
          entry = new JarEntry(name);
          jos.putNextEntry(entry);

          FileInputStream fis = null;

          try
          {
            fis = new FileInputStream(file);
            copy(fis, jos, 4096);
          }
          finally
          {
            close(fis);
          }
        }
      }
    }
    finally
    {
      if (entry != null)
      {
        jos.closeEntry();
      }
    }

    if (file.isDirectory())
    {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++)
      {
        File child = children[i];
        jarRecurse(child, prefixLength, jos);
      }
    }
  }
}
