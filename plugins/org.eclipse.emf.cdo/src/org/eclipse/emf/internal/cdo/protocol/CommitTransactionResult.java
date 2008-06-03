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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;

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

  public CommitTransactionResult(String rollbackMessage)
  {
    this.rollbackMessage = rollbackMessage;
  }

  public CommitTransactionResult(long timeStamp)
  {
    this.timeStamp = timeStamp;
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
