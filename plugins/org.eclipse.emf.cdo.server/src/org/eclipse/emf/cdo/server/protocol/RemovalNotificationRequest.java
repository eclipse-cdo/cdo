/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.core.impl.AbstractRequest;

import org.eclipse.emf.cdo.core.CDOProtocol;

import java.util.Collection;


public class RemovalNotificationRequest extends AbstractRequest
{
  private Collection<Integer> rids;

  public RemovalNotificationRequest(Collection<Integer> rids)
  {
    this.rids = rids;
  }

  public short getSignalId()
  {
    return CDOProtocol.REMOVAL_NOTIFICATION;
  }

  public void request()
  {
    transmitInt(rids.size());
    for (Integer rid : rids)
    {
      transmitInt(rid);
    }
  }
}
