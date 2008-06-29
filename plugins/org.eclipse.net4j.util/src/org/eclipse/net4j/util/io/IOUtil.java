/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.internal.util.bundle.OM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class IOUtil
{
  public static final int DEFAULT_BUFFER_SIZE = 8192;

  private IOUtil()
  {
  }

  public static InputStream IN()
  {
    return System.in;
  }

  public static PrintStream OUT()
  {
    return System.out;
  }

  public static PrintStream ERR()
  {
    return System.err;
  }

  public static void print(Throwable t, PrintStream stream)
  {
    t.printStackTrace(stream);
  }

  public static void print(Throwable t)
  {
    print(t, System.err);
  }

  public static FileInputStream openInputStream(String fileName) throws IORuntimeException
  {
    return openInputStream(new File(fileName));
  }

  public static FileInputStream openInputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileInputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileOutputStream openOutputStream(String fileName) throws IORuntimeException
  {
    return openOutputStream(new File(fileName));
  }

  public static FileOutputStream openOutputStream(File file) throws IORuntimeException
  {
    try
    {
      return new FileOutputStream(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileReader openReader(String fileName) throws IORuntimeException
  {
    return openReader(new File(fileName));
  }

  public static FileReader openReader(File file) throws IORuntimeException
  {
    try
    {
      return new FileReader(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static FileWriter openWriter(String fileName) throws IORuntimeException
  {
    return openWriter(new File(fileName));
  }

  public static FileWriter openWriter(File file) throws IORuntimeException
  {
    try
    {
      return new FileWriter(file);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static void close(Closeable closeable) throws IORuntimeException
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static Exception closeSilent(org.eclipse.net4j.util.collection.Closeable closeable)
  {
    try
    {
      if (closeable != null)
      {
        closeable.close();
      }

      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static void close(org.eclipse.net4j.util.collection.Closeable closeable) throws IORuntimeException
  {
    if (closeable != null)
    {
      closeable.close();
    }
  }

  public static void mkdirs(File folder)
  {
    if (!folder.exists())
    {
      if (!folder.mkdirs())
      {
        throw new IORuntimeException("Unable to create directory " + folder.getAbsolutePath());
      }
    }
  }

  public static int delete(File file)
  {
    if (file == null)
    {
      return 0;
    }

    int deleted = 0;
    if (file.isDirectory())
    {
      for (File child : file.listFiles())
      {
        deleted += delete(child);
      }
    }

    if (file.delete())
    {
      return deleted + 1;
    }

    file.deleteOnExit();
    return deleted;
  }

  public static void copyTree(File source, File target) throws IORuntimeException
  {
    if (source.isDirectory())
    {
      mkdirs(target);
      File[] files = source.listFiles();
      for (File file : files)
      {
        String name = file.getName();
        copyTree(new File(source, name), new File(target, name));
      }
    }
    else
    {
      copyFile(source, target);
    }
  }

  public static void copyTrees(Collection<File> sources, File target) throws IORuntimeException
  {
    for (File source : sources)
    {
      copyTree(source, target);
    }
  }

  public static void copyText(File source, File target, IOFilter<String>... lineFilters) throws IORuntimeException
  {
    BufferedReader reader = null;
    BufferedWriter writer = null;

    try
    {
      reader = new BufferedReader(openReader(source));
      writer = new BufferedWriter(openWriter(target));
      copyText(reader, writer, lineFilters);
    }
    finally
    {
      closeSilent(reader);
      closeSilent(writer);
    }
  }

  public static void copyText(BufferedReader reader, BufferedWriter writer, IOFilter<String>... lineFilters)
  {
    try
    {
      String line;
      while ((line = reader.readLine()) != null)
      {
        for (IOFilter<String> lineFilter : lineFilters)
        {
          line = lineFilter.filter(line);
        }

        writer.write(line);
        writer.newLine();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static int copy(InputStream input, OutputStream output, int size, byte buffer[]) throws IORuntimeException
  {
    try
    {
      int written = 0;
      int bufferSize = buffer.length;
      int n = Math.min(size, bufferSize);
      while (n > 0 && (n = input.read(buffer, 0, n)) != -1)
      {
        output.write(buffer, 0, n);
        written += n;
        size -= n;
        n = Math.min(size, bufferSize);
      }

      return written;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, byte buffer[]) throws IORuntimeException
  {
    try
    {
      int n;
      while ((n = input.read(buffer)) != -1)
      {
        output.write(buffer, 0, n);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize) throws IORuntimeException
  {
    copy(input, output, new byte[bufferSize]);
  }

  public static void copy(InputStream input, OutputStream output) throws IORuntimeException
  {
    copy(input, output, DEFAULT_BUFFER_SIZE);
  }

  /**
   * @see NIOUtil#copyFile(File, File)
   */
  public static void copyFile(File source, File target) throws IORuntimeException
  {
    mkdirs(target.getParentFile());
    FileInputStream input = null;
    FileOutputStream output = null;

    try
    {
      input = openInputStream(source);
      output = openOutputStream(target);
      copy(input, output);
    }
    finally
    {
      closeSilent(input);
      closeSilent(output);
    }
  }

  public static byte[] readFile(File file) throws IORuntimeException
  {
    if (file.length() > Integer.MAX_VALUE)
    {
      throw new IllegalArgumentException("File too long: " + file.length());
    }

    int size = (int)file.length();
    FileInputStream input = openInputStream(file);

    try
    {
      ByteArrayOutputStream output = new ByteArrayOutputStream(size);
      copy(input, output);
      return output.toByteArray();
    }
    finally
    {
      closeSilent(input);
    }
  }

  public static void writeFile(File file, byte[] bytes) throws IORuntimeException
  {
    FileOutputStream output = openOutputStream(file);

    try
    {
      ByteArrayInputStream input = new ByteArrayInputStream(bytes);
      copy(input, output);
    }
    finally
    {
      closeSilent(output);
    }
  }

  public static List<File> listDepthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitDepthFirst(file, collector);
    return collector.getFiles();
  }

  public static List<File> listBreadthFirst(File file)
  {
    FileCollector collector = new FileCollector();
    visitBreadthFirst(file, collector);
    return collector.getFiles();
  }

  public static void visitDepthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean recurse = visitor.visit(file);
      if (recurse && file.isDirectory())
      {
        visitDepthFirst(file.listFiles(), visitor);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static void visitDepthFirst(File[] files, IOVisitor visitor)
  {
    for (File file : files)
    {
      visitDepthFirst(file, visitor);
    }
  }

  public static void visitBreadthFirst(File file, IOVisitor visitor) throws IORuntimeException
  {
    File[] files = { file };
    visitBreadthFirst(files, visitor);
  }

  public static void visitBreadthFirst(File[] files, IOVisitor visitor) throws IORuntimeException
  {
    try
    {
      boolean[] recurse = new boolean[files.length];
      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        recurse[i] = visitor.visit(file);
      }

      for (int i = 0; i < files.length; i++)
      {
        File file = files[i];
        if (file.isDirectory() && recurse[i])
        {
          File[] children = file.listFiles();
          for (File child : children)
          {
            visitBreadthFirst(child, visitor);
          }
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static <IO extends Closeable> void safeRun(IO io, IORunnable<IO> runnable) throws IORuntimeException
  {
    try
    {
      runnable.run(io);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      close(io);
    }
  }

  public static void safeInput(File file, IORunnable<FileInputStream> runnable) throws IORuntimeException
  {
    safeRun(openInputStream(file), runnable);
  }

  public static void safeOutput(File file, IORunnable<FileOutputStream> runnable) throws IORuntimeException
  {
    safeRun(openOutputStream(file), runnable);
  }

  public static void safeRead(File file, IORunnable<FileReader> runnable) throws IORuntimeException
  {
    safeRun(openReader(file), runnable);
  }

  public static void safeWrite(File file, IORunnable<FileWriter> runnable) throws IORuntimeException
  {
    safeRun(openWriter(file), runnable);
  }

  public static boolean equals(InputStream stream1, InputStream stream2) throws IORuntimeException
  {
    try
    {
      for (;;)
      {
        int byte1 = stream1.read();
        int byte2 = stream2.read();

        if (byte1 != byte2)
        {
          return false;
        }

        if (byte1 == -1)// Implies byte2 == -1
        {
          return true;
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static boolean equals(File file1, File file2) throws IORuntimeException
  {
    if (file1.length() != file2.length())
    {
      return false;
    }

    FileInputStream stream1 = null;
    FileInputStream stream2 = null;

    try
    {
      stream1 = new FileInputStream(file1);
      stream2 = new FileInputStream(file2);
      return equals(stream1, stream2);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      closeSilent(stream1);
      closeSilent(stream2);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FileCollector implements IOVisitor
  {
    private List<File> files = new ArrayList<File>();

    public FileCollector()
    {
    }

    public List<File> getFiles()
    {
      return files;
    }

    public boolean visit(File file) throws IOException
    {
      files.add(file);
      return true;
    }
  }
}
