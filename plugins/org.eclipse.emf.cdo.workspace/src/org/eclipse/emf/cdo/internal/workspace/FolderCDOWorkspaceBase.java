/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class FolderCDOWorkspaceBase extends AbstractCDOWorkspaceBase
{
  private File folder;

  public FolderCDOWorkspaceBase(File folder)
  {
    this.folder = folder;
  }

  public File getFolder()
  {
    return folder;
  }

  public final synchronized CDORevision getRevision(CDOID id)
  {
    File file = getFile(id);
    if (!file.exists() || file.length() == 0)
    {
      return null;
    }

    FileInputStream fis = null;

    try
    {
      fis = new FileInputStream(file);
      ExtendedDataInputStream edis = new ExtendedDataInputStream(fis);
      CDODataInput in = createCDODataInput(edis);
      return in.readCDORevision();
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not read from " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(fis);
    }
  }

  public boolean isAddedObject(CDOID id)
  {
    File file = getFile(id);
    if (!file.exists())
    {
      return false;
    }

    return file.length() == 0;
  }

  @Override
  public String toString()
  {
    return "FolderBase[" + folder.getAbsolutePath() + "]";
  }

  @Override
  protected void doClear()
  {
    IOUtil.delete(folder);
    checkExists(folder, false);
    createFolder();
  }

  @Override
  protected Set<CDOID> doGetIDs()
  {
    Set<CDOID> ids = new HashSet<CDOID>();
    for (String key : folder.list())
    {
      CDOID id = getCDOID(key);
      ids.add(id);
    }

    return ids;
  }

  @Override
  protected void doRegisterChangedOrDetachedObject(InternalCDORevision revision)
  {
    File file = getFile(revision.getID());
    if (file.exists())
    {
      return;
    }

    FileOutputStream fos = null;

    try
    {
      fos = new FileOutputStream(file);
      ExtendedDataOutputStream edos = new ExtendedDataOutputStream(fos);
      CDODataOutput out = createCDODataOutput(edos);
      out.writeCDORevision(revision, CDORevision.UNCHUNKED);
      edos.flush();
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not create " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(fos);
    }
  }

  @Override
  protected void doRegisterAddedObject(CDOID id)
  {
    File file = getFile(id);
    if (file.exists())
    {
      return;
    }

    FileOutputStream fos = null;

    try
    {
      fos = new FileOutputStream(file); // Just create an empty marker file
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not create " + file.getAbsolutePath(), ex);
    }
    finally
    {
      IOUtil.close(fos);
    }
  }

  @Override
  protected void doDeregisterObject(CDOID id)
  {
    File file = getFile(id);
    file.delete();
    checkExists(file, false);
  }

  protected CDOID getCDOID(String filename)
  {
    return CDOIDUtil.read(filename);
  }

  protected String getKey(CDOID id)
  {
    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, id);
    return builder.toString();
  }

  protected File getFile(CDOID id)
  {
    String key = getKey(id);
    return new File(folder, key);
  }

  private void createFolder()
  {
    IOUtil.mkdirs(folder);
    checkExists(folder, true);
  }

  private void checkExists(File file, boolean exists)
  {
    if (exists)
    {
      if (!file.exists())
      {
        throw new IllegalStateException("File does not exist: " + file.getAbsolutePath());
      }
    }
    else
    {
      if (file.exists())
      {
        throw new IllegalStateException("File does still exist: " + file.getAbsolutePath());
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "folder";

    public Factory()
    {
      super(InternalCDOWorkspaceBase.PRODUCT_GROUP, TYPE);
    }

    public Factory(String productGroup, String type)
    {
      super(productGroup, type);
    }

    public CDOWorkspaceBase create(String description) throws ProductCreationException
    {
      return CDOWorkspaceUtil.createFolderWorkspaceBase(new File(description));
    }
  }
}
