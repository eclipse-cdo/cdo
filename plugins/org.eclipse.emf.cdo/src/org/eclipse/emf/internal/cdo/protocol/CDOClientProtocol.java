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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;

/**
 * @author Eike Stepper
 */
public class CDOClientProtocol extends SignalProtocol
{
  private CDOSessionImpl session;

  public CDOClientProtocol()
  {
  }

  public String getType()
  {
    return CDOProtocolConstants.PROTOCOL_NAME;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  public void setSession(CDOSessionImpl session)
  {
    this.session = session;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_INVALIDATION:
      return new InvalidationIndication();
    }

    return null;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (session == null)
    {
      throw new IllegalStateException("session == null");
    }
  }
}
