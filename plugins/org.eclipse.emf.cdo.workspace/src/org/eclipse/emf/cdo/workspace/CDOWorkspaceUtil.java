/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.workspace.FolderCDOWorkspaceBase;
import org.eclipse.emf.cdo.internal.workspace.WorkspaceProperties;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.properties.IProperties;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.util.Set;

/**
 * Various static helper methods for dealing with {@link CDOWorkspace workspaces}.
 * 
 * @author Eike Stepper
 */
public final class CDOWorkspaceUtil
{
  private CDOWorkspaceUtil()
  {
  }

  /**
   * @since 4.1
   */
  public static IProperties<CDOWorkspace> getProperties()
  {
    return WorkspaceProperties.INSTANCE;
  }

  /**
   * @since 4.1
   */
  public static CDOWorkspace getWorkspace(EObject object)
  {
    CDOView view = CDOUtil.getCDOObject(object).cdoView();
    if (view == null || view.isClosed())
    {
      return null;
    }

    IListener[] listeners = view.getListeners();
    if (listeners != null)
    {
      for (int i = 0; i < listeners.length; i++)
      {
        IListener listener = listeners[i];
        if (listener instanceof org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl.ViewAdapter)
        {
          org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl.ViewAdapter adapter = (org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl.ViewAdapter)listener;
          return adapter.getWorkspace();
        }
      }
    }

    return null;
  }

  /**
   * @since 4.1
   */
  public static boolean isDirty(EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    if (object == null)
    {
      return false;
    }

    CDOWorkspace workspace = getWorkspace(cdoObject);
    CDOWorkspaceBase2 base = getWorkspaceBase2(workspace.getBase());
    return base.containsID(cdoObject.cdoID());
  }

  /**
   * @since 4.1
   */
  public static CDOWorkspaceBase2 getWorkspaceBase2(final CDOWorkspaceBase base)
  {
    if (base instanceof CDOWorkspaceBase2)
    {
      return (CDOWorkspaceBase2)base;
    }

    return new DelegatingWorkspaceBase2(base);
  }

  public static CDOWorkspaceBase createFolderWorkspaceBase(File folder)
  {
    return new FolderCDOWorkspaceBase(folder);
  }

  /**
   * Returns a new configuration that can be used to checkout a new {@link CDOWorkspace} or open an existing one.
   * 
   * @since 4.1
   */
  public static CDOWorkspaceConfiguration createWorkspaceConfiguration()
  {
    return new org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceConfigurationImpl();
  }

  /**
   * @deprecated Use {@link #createWorkspaceConfiguration()} and {@link CDOWorkspaceConfiguration#open()}
   */
  @Deprecated
  public static CDOWorkspace open(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    CDOWorkspaceConfiguration config = createWorkspaceConfiguration();
    config.setStore(local);
    config.setBase(base);
    config.setRemote(remote);
    return config.open();
  }

  /**
   * @deprecated Use {@link #createWorkspaceConfiguration()} and {@link CDOWorkspaceConfiguration#checkout()}
   */
  @Deprecated
  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    return checkout(local, base, remote, null);
  }

  /**
   * @deprecated Use {@link #createWorkspaceConfiguration()} and {@link CDOWorkspaceConfiguration#checkout()}
   */
  @Deprecated
  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      String branchPath)
  {
    return checkout(local, base, remote, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  /**
   * @deprecated Use {@link #createWorkspaceConfiguration()} and {@link CDOWorkspaceConfiguration#checkout()}
   */
  @Deprecated
  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      long timeStamp)
  {
    return checkout(local, base, remote, null, timeStamp);
  }

  /**
   * @deprecated Use {@link #createWorkspaceConfiguration()} and {@link CDOWorkspaceConfiguration#checkout()}
   */
  @Deprecated
  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      String branchPath, long timeStamp)
  {
    CDOWorkspaceConfiguration config = createWorkspaceConfiguration();
    config.setStore(local);
    config.setBase(base);
    config.setRemote(remote);
    config.setBranchPath(branchPath);
    config.setTimeStamp(timeStamp);
    return config.checkout();
  }

  /**
   * @author Eike Stepper
   */
  private static final class DelegatingWorkspaceBase2 implements CDOWorkspaceBase2
  {
    private final CDOWorkspaceBase delegate;

    private DelegatingWorkspaceBase2(CDOWorkspaceBase base)
    {
      delegate = base;
    }

    public CDOWorkspace getWorkspace()
    {
      return delegate.getWorkspace();
    }

    public Set<CDOID> getIDs()
    {
      return delegate.getIDs();
    }

    public CDORevision getRevision(CDOID id)
    {
      return delegate.getRevision(id);
    }

    public boolean isEmpty()
    {
      return getIDs().isEmpty();
    }

    public boolean containsID(CDOID id)
    {
      return getIDs().contains(id);
    }

    public boolean isAddedObject(CDOID id)
    {
      return containsID(id) && getRevision(id) == null;
    }
  }
}
