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
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.workspace.InternalCDOWorkspaceMemory;

/**
 * @author Eike Stepper
 */
public final class CDOWorkspaceUtil
{
  private CDOWorkspaceUtil()
  {
  }

  public static CDOWorkspace open(IStore local, CDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote)
  {
    return new CDOWorkspaceImpl(local, (InternalCDOWorkspaceMemory)memory, remote);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote)
  {
    return checkout(local, memory, remote, null);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote,
      String branchPath)
  {
    return checkout(local, memory, remote, branchPath, CDOBranchPoint.UNSPECIFIED_DATE);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote,
      long timeStamp)
  {
    return checkout(local, memory, remote, null, timeStamp);
  }

  public static CDOWorkspace checkout(IStore local, CDOWorkspaceMemory memory, CDOSessionConfigurationFactory remote,
      String branchPath, long timeStamp)
  {
    return new CDOWorkspaceImpl(local, (InternalCDOWorkspaceMemory)memory, remote, branchPath, timeStamp);
  }
}
