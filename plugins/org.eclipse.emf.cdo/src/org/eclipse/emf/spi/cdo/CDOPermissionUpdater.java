/*
 * Copyright (c) 2013, 2014, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A {@link org.eclipse.emf.cdo.session.CDOSession.Options#setPermissionUpdater(CDOPermissionUpdater) pluggable}
 * strategy for updating the permissions of a set of {@link CDORevision revisions}.
 *
 * @author Eike Stepper
 * @since 4.3
 */
public interface CDOPermissionUpdater
{
  public static final CDOPermissionUpdater SERVER = new CDOPermissionUpdater()
  {
    @Override
    public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Set<InternalCDORevision> revisions)
    {
      CDOBranchPoint head = session.getBranchManager().getMainBranch().getHead();
      Map<CDOBranchPoint, Set<InternalCDORevision>> map = Collections.singletonMap(head, revisions);

      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      return sessionProtocol.loadPermissions3(map);
    }
  };

  public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session, Set<InternalCDORevision> revisions);
}
