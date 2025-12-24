/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.transfer;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;

import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A {@link CDOTransferSystem transfer system} that abstracts the external file system.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class FileSystemTransferSystem extends CDOTransferSystem
{
  public static final FileSystemTransferSystem INSTANCE = new FileSystemTransferSystem();

  public static final String TYPE = "fs";

  public FileSystemTransferSystem()
  {
    super(false);
  }

  @Override
  public String getType()
  {
    return TYPE;
  }

  @Override
  public URI getURI(IPath path)
  {
    return URI.createFileURI(path.toOSString());
  }

  @Override
  public CDOTransferElement getElement(IPath path)
  {
    File file = getFile(path);
    if (file.exists())
    {
      return new Element(this, file);
    }

    return null;
  }

  @Override
  public CDOTransferElement getElement(URI uri)
  {
    if (uri.isFile())
    {
      return getElement(uri.path());
    }

    return null;
  }

  @Override
  public void createFolder(IPath path)
  {
    File file = getFile(path);
    mkDir(file);
  }

  @Override
  public void createBinary(IPath path, InputStream source, IProgressMonitor monitor)
  {
    OutputStream target = null;

    try
    {
      File file = getFile(path);
      long length = file.length();
      int totalWork = length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)length;
      monitor.beginTask("", totalWork);

      mkParent(file);

      target = new FileOutputStream(file);
      IOUtil.copy(source, target);
      monitor.worked(totalWork); // TODO Progress more smoothly
    }
    catch (IOException ex)
    {
      throw new IORuntimeException();
    }
    finally
    {
      IOUtil.close(target);
      monitor.done();
    }
  }

  @Override
  public void createText(IPath path, InputStream source, String encoding, IProgressMonitor monitor)
  {
    Writer target = null;

    try
    {
      File file = getFile(path);
      long length = file.length();
      int totalWork = length > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)length;
      monitor.beginTask("", totalWork);

      mkParent(file);

      target = new FileWriter(file);
      IOUtil.copyCharacter(new InputStreamReader(source, encoding), target);
      monitor.worked(totalWork); // TODO Progress more smoothly
    }
    catch (IOException ex)
    {
      throw new IORuntimeException();
    }
    finally
    {
      IOUtil.close(target);
      monitor.done();
    }
  }

  @Override
  public String toString()
  {
    return "File System";
  }

  protected File getFile(IPath path)
  {
    return new File(path.toOSString());
  }

  protected void mkParent(File file)
  {
    File parent = file.getParentFile();
    if (parent != null)
    {
      mkDir(parent);
    }
  }

  protected void mkDir(File file)
  {
    if (file.exists())
    {
      if (!file.isDirectory())
      {
        throw new IORuntimeException("Not a folder " + file);
      }
    }
    else
    {
      if (!file.mkdirs())
      {
        throw new IORuntimeException("Could not create folder " + file);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class Element extends CDOTransferElement
  {
    private File file;

    public Element(CDOTransferSystem system, File file)
    {
      super(system);
      this.file = file.getAbsoluteFile();
    }

    @Override
    public Object getNativeObject()
    {
      return file;
    }

    @Override
    public IPath getPath()
    {
      return new Path(file.getPath());
    }

    @Override
    public boolean isDirectory()
    {
      return file.isDirectory();
    }

    @Override
    protected CDOTransferElement[] doGetChildren()
    {
      File[] children = file.listFiles();
      CDOTransferElement[] result = new Element[children.length];

      for (int i = 0; i < children.length; i++)
      {
        File child = children[i];
        result[i] = new Element(getSystem(), child);
      }

      return result;
    }

    @Override
    protected InputStream doOpenInputStream()
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
  }
}
