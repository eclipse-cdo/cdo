/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.3
 */
public interface CDOPermissionUpdater
{
  public static final CDOPermissionUpdater SERVER = new CDOPermissionUpdater()
  {
    public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session,
        Set<InternalCDORevision> revisions)
    {
      InternalCDORevision[] revisionArray = revisions.toArray(new InternalCDORevision[revisions.size()]);
      CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
      return sessionProtocol.loadPermissions(revisionArray);
    }
  };

  public Map<CDORevision, CDOPermission> updatePermissions(InternalCDOSession session,
      Set<InternalCDORevision> revisions);
}
