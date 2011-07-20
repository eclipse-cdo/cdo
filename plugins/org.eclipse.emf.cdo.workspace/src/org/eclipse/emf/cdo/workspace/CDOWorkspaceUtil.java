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
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceConfigurationImpl;
import org.eclipse.emf.cdo.internal.workspace.FolderCDOWorkspaceBase;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

import java.io.File;

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

  public static CDOWorkspaceBase createFolderWorkspaceBase(File folder)
  {
    return new FolderCDOWorkspaceBase(folder);
  }

  /**
   * @since 4.1
   */
  public static CDOWorkspaceConfiguration createWorkspaceConfiguration()
  {
    return new CDOWorkspaceConfigurationImpl();
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
}
