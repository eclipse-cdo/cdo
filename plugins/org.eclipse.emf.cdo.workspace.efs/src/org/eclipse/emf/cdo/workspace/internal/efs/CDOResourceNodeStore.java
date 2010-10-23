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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.internal.efs.bundle.OM;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
public final class CDOResourceNodeStore extends AbstractFileStore
{
  private CDOWorkspaceStore workspaceStore;

  private IFileStore parent;

  private String name;

  private AtomicReference<CDOID> resourceNodeID = new AtomicReference<CDOID>();

  public CDOResourceNodeStore(CDOWorkspaceStore workspaceStore, IFileStore parent, String name)
  {
    this.workspaceStore = workspaceStore;
    this.parent = parent;
    this.name = name;
  }

  public CDOWorkspaceStore getWorkspaceStore()
  {
    return workspaceStore;
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

  public String getPath()
  {
    if (parent instanceof CDOResourceNodeStore)
    {
      return ((CDOResourceNodeStore)parent).getPath() + "/" + name;
    }

    return name;
  }

  @Override
  public String[] childNames(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ResourceNodeRunnable<String[]>()
    {
      @Override
      protected String[] run(CDOResourceNode node)
      {
        List<String> childNames = new ArrayList<String>();
        if (node instanceof CDOResourceFolder)
        {
          CDOResourceFolder folder = (CDOResourceFolder)node;

          for (CDOResourceNode child : folder.getNodes())
          {
            childNames.add(child.getName());
          }
        }

        return childNames.toArray(new String[childNames.size()]);
      }
    }.run();
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new CDOResourceNodeStore(workspaceStore, this, name);
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ResourceNodeRunnable<IFileInfo>()
    {
      @Override
      protected IFileInfo run(CDOResourceNode node)
      {
        FileInfo info = new FileInfo(getName());
        info.setExists(true);
        info.setLength(EFS.NONE);
        info.setLastModified(EFS.NONE);
        info.setDirectory(node instanceof CDOResourceFolder);
        info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
        info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
        return info;
      }
    }.run();
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

  /**
   * @author Eike Stepper
   */
  private abstract class ResourceNodeRunnable<RESULT>
  {
    public ResourceNodeRunnable()
    {
    }

    public RESULT run()
    {
      CDOView view = null;

      try
      {
        view = workspaceStore.getWorkspace().openView();
        CDOResourceNode node = view.getResourceNode(getPath());
        resourceNodeID.compareAndSet(null, node.cdoID());
        return run(node);
      }
      finally
      {
        IOUtil.close(view);
      }
    }

    protected abstract RESULT run(CDOResourceNode node);
  }
}
