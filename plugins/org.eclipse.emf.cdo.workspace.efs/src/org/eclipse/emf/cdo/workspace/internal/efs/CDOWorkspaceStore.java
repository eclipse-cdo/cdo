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
import org.eclipse.emf.cdo.workspace.efs.CDOWorkspaceFSUtil;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOWorkspaceStore extends AbstractResourceNodeStore
{
  private static final AbstractResourceNodeStore NO_PARENT = null;

  private String name;

  private File location;

  private CDOWorkspace workspace;

  private CDOView view;

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

  private CDOWorkspace openWorkspace()
  {
    try
    {
      return CDOWorkspaceFSUtil.open(name, location);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public AbstractResourceNodeStore getParent()
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
  public IFileStore getChild(String name)
  {
    if (CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME.equals(name))
    {
      return new CDOProjectDescriptionStore(this);
    }

    return super.getChild(name);
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
    if (view != null)
    {
      IOUtil.close(view);
      view = null;
    }
  }

  @Override
  public CDOWorkspaceStore getWorkspaceStore()
  {
    return this;
  }

  @Override
  protected synchronized CDOView getView()
  {
    if (view == null)
    {
      view = workspace.openView();
    }

    return view;
  }

  @Override
  protected CDOResourceNode getResourceNode(CDOView view)
  {
    return view.getRootResource();
  }

  @Override
  protected boolean isDirectory(CDOResourceNode node)
  {
    return true;
  }

  @Override
  protected void collectChildNames(CDOResourceNode node, List<String> childNames)
  {
    childNames.add(CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME);

    CDOResource rootResource = (CDOResource)node;
    for (EObject content : rootResource.getContents())
    {
      if (content instanceof CDOResourceNode)
      {
        CDOResourceNode child = (CDOResourceNode)content;
        childNames.add(child.getName());
      }
    }
  }
}
