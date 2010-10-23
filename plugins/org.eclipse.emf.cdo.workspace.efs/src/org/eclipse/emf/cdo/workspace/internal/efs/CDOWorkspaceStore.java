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
package org.eclipse.emf.cdo.workspace.internal.efs;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOWorkspaceStore extends AbstractFileStore
{
  private static final IFileStore NO_PARENT = null;

  private String name;

  private File location;

  private CDOWorkspace workspace;

  public CDOWorkspaceStore(String name, File location)
  {
    this.name = name;
    this.location = location;
  }

  public File getLocation()
  {
    return location;
  }

  public synchronized CDOWorkspace getWorkspace()
  {
    if (workspace == null)
    {
      workspace = openWorkspace();
    }

    return workspace;
  }

  public synchronized void setWorkspace(CDOWorkspace workspace)
  {
    this.workspace = workspace;
  }

  @Override
  public IFileStore getParent()
  {
    return NO_PARENT;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public URI toURI()
  {
    try
    {
      return new URI(CDOWorkspaceFileSystem.SCHEME + "://" + name);
    }
    catch (URISyntaxException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    List<String> childNames = new ArrayList<String>();
    childNames.add(CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME);

    CDOView view = null;

    try
    {
      view = getWorkspace().openView();
      CDOResource rootResource = view.getRootResource();
      for (EObject content : rootResource.getContents())
      {
        if (content instanceof CDOResourceNode)
        {
          CDOResourceNode node = (CDOResourceNode)content;
          childNames.add(node.getName());
        }
      }
    }
    finally
    {
      IOUtil.close(view);
    }

    return childNames.toArray(new String[childNames.size()]);
  }

  @Override
  public IFileStore getChild(String name)
  {
    if (CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME.equals(name))
    {
      return new CDOProjectDescriptionStore(this);
    }

    return new CDOResourceNodeStore(this, this, name);
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    FileInfo info = new FileInfo(getName());
    info.setExists(true);
    info.setLength(EFS.NONE);
    info.setLastModified(EFS.NONE);
    info.setDirectory(true);
    info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
    info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
    return info;
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    return this;
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    throw new UnsupportedOperationException();
  }

  public void dispose()
  {
    // TODO: implement CDOWorkspaceStore.dispose()
    throw new UnsupportedOperationException();
  }

  private CDOWorkspace openWorkspace()
  {
    // TODO: implement CDOWorkspaceStore.openWorkspace()
    throw new UnsupportedOperationException();
  }
}
