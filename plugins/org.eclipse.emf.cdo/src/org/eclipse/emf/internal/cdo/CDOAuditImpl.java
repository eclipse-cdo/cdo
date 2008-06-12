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

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOAuditImpl extends CDOViewImpl implements CDOAudit
{
  private long timeStamp;

  public CDOAuditImpl(int id, CDOSessionImpl session, long timeStamp)
  {
    super(id, session);
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

  @Override
  public InternalCDORevision getRevision(CDOID id, boolean loadOnDemand)
  {
    CDOSessionImpl session = getSession();
    CDORevisionResolver revisionManager = session.getRevisionManager();
    return (InternalCDORevision)revisionManager.getRevisionByTime(id, session.getReferenceChunkSize(), timeStamp,
        loadOnDemand);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOAudit({0})", getViewID());
  }
}
