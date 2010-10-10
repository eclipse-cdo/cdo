/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
public class FolderCDOWorkspaceBaseline extends AbstractCDOWorkspaceBaseline
{
  private File folder;

  public FolderCDOWorkspaceBaseline(File folder)
  {
    this.folder = folder;
  }

  public File getFolder()
  {
    return folder;
  }

  public CDORevision getRevision(CDOID id)
  {
    File file = getFile(id);
    if (!file.exists())
    {
      throw new IllegalStateException("File not found: " + file.getAbsolutePath());
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

  public Set<CDOID> getIDs()
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
  protected boolean containsRevision(CDOID id)
  {
    File file = getFile(id);
    return file.exists();
  }

  @Override
  protected void removeRevision(CDOID id)
  {
    File file = getFile(id);
    file.delete();

    if (file.exists())
    {
      throw new IllegalStateException("Could not delete " + file.getAbsolutePath());
    }
  }

  @Override
  protected void addRevision(InternalCDORevision revision)
  {
    File file = getFile(revision.getID());
    if (file.exists())
    {
      throw new IllegalStateException("File already exists: " + file.getAbsolutePath());
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
}
