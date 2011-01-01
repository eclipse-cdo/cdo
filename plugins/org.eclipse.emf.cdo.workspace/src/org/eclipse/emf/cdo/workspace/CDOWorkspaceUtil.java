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
import org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl;
import org.eclipse.emf.cdo.internal.workspace.FolderCDOWorkspaceBase;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceBase;

import java.io.File;

/**
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

  public static CDOWorkspace open(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    return new CDOWorkspaceImpl(local, (InternalCDOWorkspaceBase)base, remote);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote)
  {
    return checkout(local, base, remote, null);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      String branchPath)
  {
    return checkout(local, base, remote, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      long timeStamp)
  {
    return checkout(local, base, remote, null, timeStamp);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceBase base, CDOSessionConfigurationFactory remote,
      String branchPath, long timeStamp)
  {
    return new CDOWorkspaceImpl(local, (InternalCDOWorkspaceBase)base, remote, branchPath, timeStamp);
  }
}
