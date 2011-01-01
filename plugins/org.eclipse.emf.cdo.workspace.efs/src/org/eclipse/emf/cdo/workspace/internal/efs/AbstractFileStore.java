/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace.internal.efs;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Eike Stepper
 */
public abstract class AbstractFileStore extends FileStore
{
  public AbstractFileStore()
  {
  }

  @Override
  public CDOWorkspaceFileSystem getFileSystem()
  {
    return CDOWorkspaceFileSystem.getInstance();
  }

  @Override
  public URI toURI()
  {
    try
    {
      return new URI(getParent().toURI().toString() + "/" + getName());
    }
    catch (URISyntaxException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Invalid extends AbstractFileStore
  {
    private IFileStore parent;

    private String name;

    public Invalid(IFileStore parent, String name)
    {
      this.parent = parent;
      this.name = name;
    }

    @Override
    public IFileStore getParent()
    {
      return parent;
    }

    @Override
    public String getName()
    {
      return name;
    }

    @Override
    public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
    {
      return CDOWorkspaceFileSystem.NO_CHILD_NAMES;
    }

    @Override
    public IFileStore getChild(String name)
    {
      return new Invalid(this, name);
    }

    @Override
    public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
    {
      throw new UnsupportedOperationException();
    }
  }
}
