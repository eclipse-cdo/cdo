/*
 * Copyright (c) 2010-2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspace;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceBase;
import org.eclipse.emf.cdo.workspace.CDOWorkspaceUtil;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.Monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class FolderCDOWorkspaceBase extends AbstractCDOWorkspaceBase
{
  private final File folder;

  private final File addedAndDetachedFile;

  public FolderCDOWorkspaceBase(File folder)
  {
    this.folder = folder.getAbsoluteFile();
    addedAndDetachedFile = new File(folder, "_addedAndDetached.log");
  }

  @Override
  public void init(InternalCDOWorkspace workspace)
  {
    super.init(workspace);
    createFolder();
  }

  public final File getFolder()
  {
    return folder;
  }

  @Override
  public final synchronized InternalCDORevision getRevision(CDOID id)
  {
    File file = getFile(id);
    return readRevision(file);
  }

  @Override
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
  public void deleteAddedAndDetachedObjects(final IStoreAccessor.Raw accessor, final CDOBranch branch)
  {
    handleAddedAndDetachedObjects(new AddedAndDetachedHandler()
    {
      @Override
      public void handleAddedAndDetachedHandler(CDOID id, int detachedVersion)
      {
        for (int v = 1; v <= detachedVersion; v++)
        {
          accessor.rawDelete(id, v, branch, null, new Monitor());
        }
      }
    });
  }

  @Override
  public String toString()
  {
    return "FolderBase[" + folder + "]";
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
    return doGetIDs(folder);
  }

  @Override
  protected void doRegisterChangedOrDetachedObject(InternalCDORevision revision)
  {
    File file = getFile(revision.getID());
    if (file.exists())
    {
      return;
    }

    writeRevision(revision, file);
  }

  @Override
  protected void doRegisterAddedAndDetachedObject(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    int detachedVersion = revision.getVersion();

    String key = getKey(id);

    FileWriter writer = null;

    try
    {
      writer = new FileWriter(addedAndDetachedFile, true);
      writer.write(key);
      writer.write("\t");
      writer.write(Integer.toString(detachedVersion));
      writer.write("\n");
    }
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not write to " + addedAndDetachedFile, ex);
    }
    finally
    {
      IOUtil.close(writer);
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
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not write to " + file, ex);
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

  protected final void handleAddedAndDetachedObjects(AddedAndDetachedHandler handler)
  {
    if (!addedAndDetachedFile.exists())
    {
      return;
    }

    FileReader fileReader = null;

    try
    {
      fileReader = new FileReader(addedAndDetachedFile);
      BufferedReader lineReader = new BufferedReader(fileReader);

      String line;
      while ((line = lineReader.readLine()) != null)
      {
        String[] tokens = line.split("\t");

        CDOID id = CDOIDUtil.read(tokens[0]);
        int detachedVersion = Integer.parseInt(tokens[1]);

        handler.handleAddedAndDetachedHandler(id, detachedVersion);
      }
    }
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not read from " + addedAndDetachedFile, ex);
    }
    finally
    {
      IOUtil.close(fileReader);
    }
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
        throw new IllegalStateException("File does not exist: " + file);
      }
    }
    else
    {
      if (file.exists())
      {
        throw new IllegalStateException("File does still exist: " + file);
      }
    }
  }

  private void writeRevision(InternalCDORevision revision, File file)
  {
    FileOutputStream fos = null;

    try
    {
      fos = new FileOutputStream(file);
      ExtendedDataOutputStream edos = new ExtendedDataOutputStream(fos);
      CDODataOutput out = createCDODataOutput(edos);
      out.writeCDORevision(revision, CDORevision.UNCHUNKED);
      edos.flush();
    }
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not write to " + file, ex);
    }
    finally
    {
      IOUtil.close(fos);
    }
  }

  private InternalCDORevision readRevision(File file)
  {
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
      return (InternalCDORevision)in.readCDORevision();
    }
    catch (IOException ex)
    {
      throw new IllegalStateException("Could not read from " + file, ex);
    }
    finally
    {
      IOUtil.close(fis);
    }
  }

  private Set<CDOID> doGetIDs(File folder)
  {
    Set<CDOID> ids = new HashSet<>();

    if (folder.isDirectory())
    {
      File[] files = folder.listFiles();
      if (files != null)
      {
        for (File file : files)
        {
          if (!ObjectUtil.equals(file, addedAndDetachedFile))
          {
            CDOID id = getCDOID(file.getName());
            ids.add(id);
          }
        }
      }
    }

    return ids;
  }

  /**
   * @author Eike Stepper
   */
  public interface AddedAndDetachedHandler
  {
    public void handleAddedAndDetachedHandler(CDOID id, int detachedVersion);
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

    @Override
    public CDOWorkspaceBase create(String description) throws ProductCreationException
    {
      return CDOWorkspaceUtil.createFolderWorkspaceBase(new File(description));
    }
  }
}
