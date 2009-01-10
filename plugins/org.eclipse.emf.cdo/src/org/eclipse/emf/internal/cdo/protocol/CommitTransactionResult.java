/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.internal.common.revision.CDOIDMapper;

import org.eclipse.emf.internal.cdo.revision.CDOPostCommitReferenceAdjuster;

import org.eclipse.emf.spi.cdo.InternalCDOCommitContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionResult
{
  private String rollbackMessage;

  private long timeStamp;

  private Map<CDOIDTemp, CDOID> idMappings = new HashMap<CDOIDTemp, CDOID>();

  private CDOReferenceAdjuster referenceAdjuster;

  private InternalCDOCommitContext commitContext;

  public CommitTransactionResult(InternalCDOCommitContext commitContext, String rollbackMessage)
  {
    this.rollbackMessage = rollbackMessage;
    this.commitContext = commitContext;
  }

  public CommitTransactionResult(InternalCDOCommitContext commitContext, long timeStamp)
  {
    this.timeStamp = timeStamp;
    this.commitContext = commitContext;
  }

  public CDOReferenceAdjuster getReferenceAdjuster()
  {
    if (referenceAdjuster == null)
    {
      referenceAdjuster = new CDOPostCommitReferenceAdjuster(commitContext.getTransaction(),
          new CDOIDMapper(idMappings));
    }

    return referenceAdjuster;
  }

  public void setReferenceAdjuster(CDOReferenceAdjuster referenceAdjuster)
  {
    this.referenceAdjuster = referenceAdjuster;
  }

  public InternalCDOCommitContext getCommitContext()
  {
    return commitContext;
  }

  public String getRollbackMessage()
  {
    return rollbackMessage;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public Map<CDOIDTemp, CDOID> getIDMappings()
  {
    return idMappings;
  }

  void addIDMapping(CDOIDTemp oldID, CDOID newID)
  {
    idMappings.put(oldID, newID);
  }
}
