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
    // TODO: implement CDOWorkspaceStore.dispose()
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOWorkspaceStore getWorkspaceStore()
  {
    return this;
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

  private CDOWorkspace openWorkspace()
  {
    // TODO: implement CDOWorkspaceStore.openWorkspace()
    throw new UnsupportedOperationException();
  }
}
