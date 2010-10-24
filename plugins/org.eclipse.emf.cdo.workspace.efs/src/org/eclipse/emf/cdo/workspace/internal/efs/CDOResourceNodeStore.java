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
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.internal.efs.bundle.OM;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOResourceNodeStore extends AbstractResourceNodeStore
{
  private CDOWorkspaceStore workspaceStore;

  private AbstractResourceNodeStore parent;

  private String name;

  public CDOResourceNodeStore(CDOWorkspaceStore workspaceStore, AbstractResourceNodeStore parent, String name)
  {
    this.workspaceStore = workspaceStore;
    this.parent = parent;
    this.name = name;
  }

  @Override
  public AbstractResourceNodeStore getParent()
  {
    return parent;
  }

  @Override
  public String getName()
  {
    return name;
  }

  public String getPath()
  {
    if (parent instanceof CDOResourceNodeStore)
    {
      return ((CDOResourceNodeStore)parent).getPath() + "/" + name;
    }

    return name;
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ResourceNodeRunnable<InputStream>()
    {
      @Override
      protected InputStream run(CDOResourceNode node)
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CDOResource resource = (CDOResource)node;
        // resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);

        try
        {
          resource.save(baos, null);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          throw WrappedException.wrap(ex);
        }

        return new ByteArrayInputStream(baos.toByteArray());
      }
    }.run();
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    new ResourceNodeRunnable<CDOResourceFolder>()
    {
      @Override
      protected CDOResourceFolder run(CDOView view)
      {
        return ((CDOTransaction)view).createResourceFolder(getPath());
      }
    }.run(true);

    return this;
  }

  @Override
  public CDOWorkspaceStore getWorkspaceStore()
  {
    return workspaceStore;
  }

  @Override
  protected CDOResourceNode getResourceNode(CDOView view)
  {
    return view.getResourceNode(getPath());
  }

  @Override
  protected boolean isDirectory(CDOResourceNode node)
  {
    return node instanceof CDOResourceFolder;
  }

  @Override
  protected void collectChildNames(CDOResourceNode node, List<String> childNames)
  {
    if (node instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)node;

      for (CDOResourceNode child : folder.getNodes())
      {
        childNames.add(child.getName());
      }
    }
  }
}
