/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.InternalNotificationManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class NotificationManager extends Lifecycle implements InternalNotificationManager
{
  private InternalRepository repository;

  public NotificationManager()
  {
  }

  public InternalRepository getRepository()
  {
    return repository;
  }

  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;
  }

  public void notifyCommit(InternalSession session, IStoreAccessor.CommitContext commitContext)
  {
    CDORevisionDelta[] arrayOfDeltas = commitContext.getDirtyObjectDeltas();
    CDOID[] arrayOfDetachedObjects = commitContext.getDetachedObjects();
    InternalCDOPackageUnit[] arrayOfNewPackageUnit = commitContext.getNewPackageUnits();
    int dirtyIDSize = arrayOfDeltas == null ? 0 : arrayOfDeltas.length;
    int detachedObjectsSize = arrayOfDetachedObjects == null ? 0 : arrayOfDetachedObjects.length;

    InternalSessionManager sessionManager = repository.getSessionManager();
    List<CDOIDAndVersion> dirtyIDs = new ArrayList<CDOIDAndVersion>(dirtyIDSize);
    List<CDOID> detachedObjects = new ArrayList<CDOID>(detachedObjectsSize);
    List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>(dirtyIDSize);
    if (dirtyIDSize > 0 || detachedObjectsSize > 0)
    {
      for (int i = 0; i < dirtyIDSize; i++)
      {
        CDORevisionDelta delta = arrayOfDeltas[i];
        CDOIDAndVersion dirtyIDAndVersion = CDOIDUtil.createIDAndVersion(delta.getID(), delta.getVersion());
        dirtyIDs.add(dirtyIDAndVersion);

        // TODO Avoid creating a temp list
        deltas.add(delta);
      }

      for (int i = 0; i < detachedObjectsSize; i++)
      {
        // TODO Avoid creating a temp list
        detachedObjects.add(arrayOfDetachedObjects[i]);
      }
    }

    sessionManager.handleCommitNotification(commitContext.getBranchPoint(), arrayOfNewPackageUnit, dirtyIDs,
        detachedObjects, deltas, session);
  }
}
