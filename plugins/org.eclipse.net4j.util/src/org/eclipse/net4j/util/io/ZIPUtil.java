/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Eike Stepper
 */
public final class ZIPUtil
{
  public static final int DEFALULT_BUFFER_SIZE = 4096;

  private ZIPUtil()
  {
  }

  public static void zip(File zipFile, ZipEntryHandler handler) throws IORuntimeException
  {
    final byte[] buffer = new byte[DEFALULT_BUFFER_SIZE];
    final EntryContext context = new EntryContext();

    FileOutputStream fos = IOUtil.openOutputStream(zipFile);
    ZipOutputStream zos = null;
    InputStream input = null;

    try
    {
      zos = new ZipOutputStream(new BufferedOutputStream(fos, DEFALULT_BUFFER_SIZE));
      for (;;)
      {
        handler.handleEntry(context);
        if (context.isEmpty())
        {
          break;
        }

        String name = context.getName();
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);

        if (!context.isDirectory())
        {
          try
          {
            input = context.getInputStream();
            IOUtil.copy(input, zos, buffer);
          }
          finally
          {
            IOUtil.closeSilent(input);
          }
        }

        context.reset();
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(zos);
      IOUtil.closeSilent(fos);
    }
  }

  public static void zip(File zipFile, File sourceFolder, boolean excludeRoot)
  {
    zip(zipFile, new FileSystemZipHandler(sourceFolder, excludeRoot));
  }

  public static void main(String[] args) throws Exception
  {
    File zipFile = new File("C:\\org.eclipse.emf.ecore_2.3.1.v200707242120.zip");
    File targetFolder = new File("C:\\_weaver\\org.eclipse.emf.ecore_2.3.1.unzipped");
    unzip(zipFile, targetFolder);

    zip(new File("C:\\_weaver\\org.eclipse.emf.ecore_2.3.1.jar"), targetFolder, true);
  }

  public static void unzip(File zipFile, UnzipHandler handler) throws IORuntimeException
  {
    FileInputStream fis = IOUtil.openInputStream(zipFile);
    ZipInputStream zis = null;

    try
    {
      zis = new ZipInputStream(new BufferedInputStream(fis, DEFALULT_BUFFER_SIZE));

      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null)
      {
        if (entry.isDirectory())
        {
          handler.unzipDirectory(entry.getName());
        }
        else
        {
          // TODO Provide delegating InputStream that ignores close()
          handler.unzipFile(entry.getName(), zis);
        }
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(zis);
      IOUtil.closeSilent(fis);
    }
  }

  public static void unzip(File zipFile, File targetFolder) throws IORuntimeException
  {
    unzip(zipFile, new FileSystemUnzipHandler(targetFolder, DEFALULT_BUFFER_SIZE));
  }

  /**
   * @author Eike Stepper
   */
  public interface ZipEntryHandler
  {
    public void handleEntry(EntryContext context) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  public interface UnzipHandler
  {
    public void unzipDirectory(String name) throws IOException;

    public void unzipFile(String name, InputStream zipStream) throws IOException;
  }

  /**
   * @author Eike Stepper
   */
  public static final class EntryContext
  {
    private static final String EMPTY = new String();

    private String name = EMPTY;

    private InputStream inputStream;

    EntryContext()
    {
    }

    void reset()
    {
      name = null;
      inputStream = null;
    }

    boolean isEmpty()
    {
      return name == null;
    }

    boolean isDirectory()
    {
      return inputStream == null;
    }

    String getName()
    {
      if (isDirectory())
      {
        return name;
      }

      return name + "/";
    }

    InputStream getInputStream()
    {
      return inputStream;
    }

    public void setName(String name)
    {
      this.name = name;
    }

    public void setInputStream(InputStream inputStream)
    {
      this.inputStream = inputStream;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FileSystemZipHandler implements ZipEntryHandler
  {
    private int sourceFolderLength;

    private transient Iterator<File> files;

    public FileSystemZipHandler(File sourceFolder, boolean excludeRoot)
    {
      sourceFolderLength = sourceFolder.getAbsolutePath().length();
      files = IOUtil.listBreadthFirst(sourceFolder).iterator();
      if (excludeRoot)
      {
        files.next();
      }
    }

    public void handleEntry(EntryContext context) throws IOException
    {
      if (files.hasNext())
      {
        File file = files.next();
        context.setName(getName(file));

        if (file.isFile())
        {
          context.setInputStream(IOUtil.openInputStream(file));
        }
      }
    }

    protected String getName(File file)
    {
      return file.getAbsolutePath().substring(sourceFolderLength);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FileSystemUnzipHandler implements UnzipHandler
  {
    private File targetFolder;

    private transient byte[] buffer;

    public FileSystemUnzipHandler(File targetFolder, int bufferSize)
    {
      this.targetFolder = targetFolder;
      buffer = new byte[bufferSize];
    }

    public File getTargetFolder()
    {
      return targetFolder;
    }

    public void unzipDirectory(String name)
    {
      File directory = new File(targetFolder, name);
      if (!directory.exists())
      {
        directory.mkdirs();
      }
    }

    public void unzipFile(String name, InputStream zipStream)
    {
      File targetFile = new File(targetFolder, name);
      if (!targetFile.getParentFile().exists())
      {
        targetFile.getParentFile().mkdirs();
      }

      FileOutputStream out = IOUtil.openOutputStream(targetFile);

      try
      {
        IOUtil.copy(zipStream, out, buffer);
      }
      finally
      {
        IOUtil.closeSilent(out);
      }
    }
  }
}
