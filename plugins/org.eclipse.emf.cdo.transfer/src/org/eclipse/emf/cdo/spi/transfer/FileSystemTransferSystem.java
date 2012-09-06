/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.transfer;

import org.eclipse.emf.cdo.transfer.CDOTransferElement;
import org.eclipse.emf.cdo.transfer.CDOTransferSystem;

import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IPath;
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
 * @author Eike Stepper
 * @since 4.2
 */
public class FileSystemTransferSystem extends CDOTransferSystem
{
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
  public void createFolder(IPath path) throws IOException
  {
    File file = getFile(path);
    mkDir(file);
  }

  @Override
  public void createBinary(IPath path, InputStream source) throws IOException
  {
    File file = getFile(path);
    mkParent(file);

    OutputStream target = new FileOutputStream(file);

    try
    {
      IOUtil.copy(source, target);
    }
    finally
    {
      IOUtil.close(target);
    }
  }

  @Override
  public void createText(IPath path, InputStream source, String encoding) throws IOException
  {
    File file = getFile(path);
    mkParent(file);

    Writer target = new FileWriter(file);

    try
    {
      IOUtil.copyCharacter(new InputStreamReader(source, encoding), target);
    }
    finally
    {
      IOUtil.close(target);
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

  protected void mkParent(File file) throws IOException
  {
    File parent = file.getParentFile();
    if (parent != null)
    {
      mkDir(parent);
    }
  }

  protected void mkDir(File file) throws IOException
  {
    if (file.exists())
    {
      if (!file.isDirectory())
      {
        throw new IOException("Not a folder " + file);
      }
    }
    else
    {
      if (!file.mkdirs())
      {
        throw new IOException("Could not create folder " + file);
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
    protected CDOTransferElement[] doGetChildren() throws IOException
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
    protected InputStream doOpenInputStream() throws IOException
    {
      return new FileInputStream(file);
    }
  }
}
