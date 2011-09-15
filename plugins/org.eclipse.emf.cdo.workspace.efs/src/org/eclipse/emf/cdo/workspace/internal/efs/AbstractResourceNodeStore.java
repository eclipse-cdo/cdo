/*
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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class AbstractResourceNodeStore extends AbstractFileStore
{
  private CDOID resourceNodeID;

  public AbstractResourceNodeStore()
  {
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
        collectChildNames(node, childNames);
        return childNames.toArray(new String[childNames.size()]);
      }
    }.run();
  }

  @Override
  public IFileStore getChild(String name)
  {
    return new CDOResourceNodeStore(getWorkspaceStore(), this, name);
  }

  @Override
  public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ResourceNodeRunnable<IFileInfo>()
    {
      @Override
      protected IFileInfo run(CDOResourceNode node)
      {
        long lastModified = EFS.NONE;
        boolean exists = node != null;
        boolean directory = exists && isDirectory(node);
        if (!directory)
        {
          lastModified = getWorkspaceStore().getLastModified(getPath());
        }

        FileInfo info = new FileInfo(getName());
        info.setLastModified(lastModified);
        info.setExists(exists);
        info.setDirectory(directory);
        info.setLength(EFS.NONE);
        info.setAttribute(EFS.ATTRIBUTE_READ_ONLY, false);
        info.setAttribute(EFS.ATTRIBUTE_HIDDEN, false);
        return info;
      }
    }.run();
  }

  public abstract CDOWorkspaceStore getWorkspaceStore();

  @Override
  public abstract AbstractResourceNodeStore getParent();

  public abstract String getPath();

  protected CDOView getView()
  {
    return getWorkspaceStore().getView();
  }

  protected abstract CDOResourceNode getResourceNode(CDOView view);

  protected abstract boolean isDirectory(CDOResourceNode node);

  protected abstract void collectChildNames(CDOResourceNode node, List<String> childNames);

  /**
   * @author Eike Stepper
   */
  protected abstract class ResourceNodeRunnable<RESULT>
  {
    public ResourceNodeRunnable()
    {
    }

    public RESULT run()
    {
      return run(false);
    }

    public RESULT run(boolean transactional)
    {
      CDOView view = null;

      try
      {
        view = openView(transactional);
        RESULT result = run(view);
        if (transactional)
        {
          CDOTransaction transaction = (CDOTransaction)view;
          transaction.commit();
        }

        return result;
      }
      catch (CommitException ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        if (transactional)
        {
          IOUtil.close(view);
        }
      }
    }

    protected RESULT run(CDOView view)
    {
      CDOResourceNode node = getResourceNode(view);
      RESULT result = run(node);
      return result;
    }

    protected RESULT run(CDOResourceNode node)
    {
      return null;
    }

    private CDOView openView(boolean transactional)
    {
      CDOWorkspace workspace = getWorkspaceStore().getWorkspace();
      return transactional ? workspace.openTransaction() : getView();
    }

    private CDOResourceNode getResourceNode(CDOView view)
    {
      CDOResourceNode node = null;

      try
      {
        if (resourceNodeID == null)
        {
          node = AbstractResourceNodeStore.this.getResourceNode(view);
          resourceNodeID = node.cdoID();
        }
        else
        {
          node = (CDOResourceNode)view.getObject(resourceNodeID);
        }
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }

      return node;
    }
  }
}
