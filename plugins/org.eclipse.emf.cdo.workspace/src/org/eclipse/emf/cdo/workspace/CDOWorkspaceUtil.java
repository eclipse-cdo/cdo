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

import org.eclipse.emf.cdo.internal.workspace.CDOWorkspaceImpl;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

/**
 * @author Eike Stepper
 */
public final class CDOWorkspaceUtil
{
  private CDOWorkspaceUtil()
  {
  }

  public static CDOWorkspace checkout(IStore local, CDOSessionConfigurationFactory remote, String branchPath,
      long timeStamp)
  {
    return new CDOWorkspaceImpl(local, remote, branchPath, timeStamp);
  }

  public static CDOWorkspace open(IStore local, CDOSessionConfigurationFactory remote)
  {
    return new CDOWorkspaceImpl(local, remote);
  }
}
