/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOAudit;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.emf.internal.cdo.protocol.SetAuditRequest;

import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.WrappedException;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOAuditImpl extends CDOViewImpl implements CDOAudit
{
  private long timeStamp;

  public CDOAuditImpl(int id, CDOSessionImpl session, long timeStamp)
  {
    super(session, id);
    this.timeStamp = timeStamp;
  }

  @Override
  public Type getViewType()
  {
    return Type.AUDIT;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  /**
   * @since 2.0
   */
  public void setTimeStamp(long timeStamp)
  {
    if (this.timeStamp != timeStamp)
    {
      List<InternalCDOObject> invalidObjects = getInvalidObjects(timeStamp);
      boolean[] existanceFlags = sendSetAuditRequest(timeStamp, invalidObjects);
      this.timeStamp = timeStamp;

      int i = 0;
      for (InternalCDOObject invalidObject : invalidObjects)
      {
        boolean existanceFlag = existanceFlags[i++];
        if (existanceFlag)
        {
          // --> PROXY
          CDOStateMachine.INSTANCE.invalidate(invalidObject, UNSPECIFIED_DATE);
        }
        else
        {
          // --> TANSIENT
          CDOStateMachine.INSTANCE.invalidate(invalidObject);
        }
      }
    }
  }

  private boolean[] sendSetAuditRequest(long timeStamp, List<InternalCDOObject> invalidObjects)
  {
    try
    {
      CDOSessionImpl session = getSession();
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();
      SetAuditRequest request = new SetAuditRequest(session.getChannel(), getViewID(), timeStamp, invalidObjects);
      return failOverStrategy.send(request);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    CDOSessionImpl session = getSession();
    CDORevisionResolver revisionManager = session.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevisionByTime(id, session.getCollectionLoadingPolicy()
        .getInitialChunkSize(), timeStamp, loadOnDemand);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOAudit({0})", getViewID());
  }
}
