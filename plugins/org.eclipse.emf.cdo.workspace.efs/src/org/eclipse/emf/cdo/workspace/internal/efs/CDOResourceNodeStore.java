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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.internal.efs.CDOWorkspaceStore.SaveContext;
import org.eclipse.emf.cdo.workspace.internal.efs.bundle.OM;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

  @Override
  public String getPath()
  {
    return parent.getPath() + "/" + name;
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
  public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ByteArrayOutputStream()
    {
      @Override
      public void close() throws IOException
      {
        byte[] bytes = toByteArray();
        InputStream in = new ByteArrayInputStream(bytes);

        XMIResource xmiResource = new XMIResourceImpl();
        xmiResource.load(in, null);

        String path = getPath();
        getWorkspaceStore().setLastModified(path, System.currentTimeMillis());

        SaveContext saveContext = getWorkspaceStore().getSaveContext();
        saveContext.save(xmiResource, path);
      }
    };
  }

  @Override
  public void delete(int options, IProgressMonitor monitor) throws CoreException
  {
    // Options can only contain EFS.NONE
    new ResourceNodeRunnable<Boolean>()
    {
      @Override
      protected Boolean run(CDOResourceNode node)
      {
        try
        {
          node.delete(null);
          return true;
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }.run(true);
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    // TODO Respect the SHALLOW option
    new ResourceNodeRunnable<CDOResourceFolder>()
    {
      @Override
      protected CDOResourceFolder run(CDOView view)
      {
        String path = getPath();
        return ((CDOTransaction)view).getOrCreateResourceFolder(path);
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
