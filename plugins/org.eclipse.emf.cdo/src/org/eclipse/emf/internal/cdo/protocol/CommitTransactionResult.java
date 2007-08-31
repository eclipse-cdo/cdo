/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.protocol.CDOID;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CommitTransactionResult
{
  private long timeStamp;

  private Map<CDOID, CDOID> idMappings = new HashMap();

  public CommitTransactionResult(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public Map<CDOID, CDOID> getIDMappings()
  {
    return idMappings;
  }

  void addIDMapping(CDOID oldID, CDOID newID)
  {
    idMappings.put(oldID, newID);
  }
}
