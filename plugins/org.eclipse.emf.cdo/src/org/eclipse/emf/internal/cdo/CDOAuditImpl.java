/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevisionResolver;

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

  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public boolean isHistorical()
  {
    return true;
  }

  @Override
  public CDORevisionImpl lookupRevision(CDOID id)
  {
    CDORevisionResolver revisionManager = getSession().getRevisionManager();
    return (CDORevisionImpl)revisionManager.getRevision(id, timeStamp);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOAudit({0})", getID());
  }
}
