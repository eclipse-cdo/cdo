/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.INotificationManager;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class NotificationManager implements INotificationManager 
{
  private Repository repository = null;

  public NotificationManager(Repository repository)
  {
    this.repository = repository;
  }

  public void notifyCommit(Session session, CommitContext commitContext)
  {
    CDORevisionDelta[] dirtyID = commitContext.getDirtyObjectDeltas();

    int modifications = dirtyID == null ? 0 : dirtyID.length;
    
    if (modifications > 0)
    {
      List<CDOIDAndVersion> dirtyIDs = new ArrayList<CDOIDAndVersion>(modifications);
      List<CDORevisionDelta> deltas = new ArrayList<CDORevisionDelta>(modifications);

      for (int i = 0; i < modifications; i++)
      {
        CDORevisionDelta delta = dirtyID[i];
        CDOIDAndVersion dirtyIDAndVersion = CDOIDUtil.createIDAndVersion(delta.getID(), delta.getOriginVersion());
        dirtyIDs.add(dirtyIDAndVersion);
        deltas.add(delta);
      }

      SessionManager sessionManager = (SessionManager)repository.getSessionManager();
      
      sessionManager.handleCommitNotification(commitContext.getTimeStamp(), dirtyIDs, deltas, session);
    }
    
  }
}
