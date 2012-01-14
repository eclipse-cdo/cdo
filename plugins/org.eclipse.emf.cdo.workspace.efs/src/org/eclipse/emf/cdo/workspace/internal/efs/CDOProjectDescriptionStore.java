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
package org.eclipse.emf.cdo.workspace.internal.efs;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public final class CDOProjectDescriptionStore extends AbstractFileStore
{
  public static final String DESCRIPTION_FILE_NAME = ".project";

  private CDOWorkspaceStore workspaceStore;

  public CDOProjectDescriptionStore(CDOWorkspaceStore workspaceStore)
  {
    this.workspaceStore = workspaceStore;
  }

  public CDOWorkspaceStore getWorkspaceStore()
  {
    return workspaceStore;
  }

  @Override
  public IFileStore getParent()
  {
    return workspaceStore;
  }

  @Override
  public String getName()
  {
    return DESCRIPTION_FILE_NAME;
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    return CDOWorkspaceFileSystem.NO_CHILD_NAMES;
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new AbstractFileStore.Invalid(this, name);
  }

  @Override
  public void copy(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException
  {
    getLocalFile().copy(destination, options, monitor);
  }

  @Override
  public void delete(int options, IProgressMonitor monitor) throws CoreException
  {
    getLocalFile().delete(options, monitor);
  }

  @Override
  public IFileInfo fetchInfo()
  {
    return getLocalFile().fetchInfo();
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    return getLocalFile().fetchInfo(options, monitor);
  }

  @Override
  public IFileStore getFileStore(IPath path)
  {
    return getLocalFile().getFileStore(path);
  }

  @Override
  public boolean isParentOf(IFileStore other)
  {
    return getLocalFile().isParentOf(other);
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    return getLocalFile().mkdir(options, monitor);
  }

  @Override
  public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException
  {
    getLocalFile().move(destination, options, monitor);
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return getLocalFile().openInputStream(options, monitor);
  }

  @Override
  public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return getLocalFile().openOutputStream(options, monitor);
  }

  @Override
  public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException
  {
    getLocalFile().putInfo(info, options, monitor);
  }

  @Override
  public File toLocalFile(int options, IProgressMonitor monitor) throws CoreException
  {
    return getLocalFile().toLocalFile(options, monitor);
  }

  private IFileStore getLocalFile()
  {
    File file = new File(workspaceStore.getLocation(), DESCRIPTION_FILE_NAME);
    return EFS.getLocalFileSystem().fromLocalFile(file);
  }
}
