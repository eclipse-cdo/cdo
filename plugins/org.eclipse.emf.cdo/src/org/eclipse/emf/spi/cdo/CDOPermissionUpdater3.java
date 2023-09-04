/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.util.Map;
import java.util.Set;

/**
 * A {@link CDOPermissionUpdater permission updater} that takes the {@link CDOBranchPoint security context} into account.
 *
 * @author Eike Stepper
 * @since 4.22
 */
public interface CDOPermissionUpdater3 extends CDOPermissionUpdater
{
  public static final CDOPermissionUpdater3 SERVER = new CDOPermissionUpdater3()
  {
    @Override
    public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Map<CDOBranchPoint, Set<InternalCDORevision>> revisions,
        CDOCommitInfo commitInfo)
    {
      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      return sessionProtocol.loadPermissions3(revisions);
    }
  };

  /**
   * @deprecated As of 4.22 use {@link #updatePermissions(InternalCDOSession, Map, CDOCommitInfo)}.
   */
  @Deprecated
  @Override
  public default Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Set<InternalCDORevision> revisions)
  {
    throw new UnsupportedOperationException();
  }

  public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Map<CDOBranchPoint, Set<InternalCDORevision>> revisions,
      CDOCommitInfo commitInfo);
}
