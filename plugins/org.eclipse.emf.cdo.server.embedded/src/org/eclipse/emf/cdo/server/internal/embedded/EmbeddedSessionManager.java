/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;

/**
 * Enforces the security capabilities supported by an embedded repository.
 *
 * @author Eike Stepper
 */
public final class EmbeddedSessionManager extends SessionManager
{
  public EmbeddedSessionManager()
  {
  }

  @Override
  public void setPermissionManager(IPermissionManager permissionManager)
  {
    if (permissionManager != null)
    {
      throw new UnsupportedOperationException("Permission managers are unsupported in Embedded CDO; use authentication without authorization."); //$NON-NLS-1$
    }

    super.setPermissionManager(null);
  }

  @Override
  public void sendBranchNotification(InternalSession sender, ChangeKind changeKind, CDOBranch... branches)
  {
    // Embedded clients share the canonical server branch manager.
  }

  @Override
  public void sendTagNotification(InternalSession sender, int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    // Embedded clients share the canonical server tag list.
  }
}
