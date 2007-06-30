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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.server.SessionImpl;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends SignalProtocol
{
  private SessionImpl session;

  public CDOServerProtocol()
  {
  }

  public String getType()
  {
    return CDOProtocolConstants.PROTOCOL_NAME;
  }

  public SessionImpl getSession()
  {
    return session;
  }

  public void setSession(SessionImpl session)
  {
    this.session = session;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.OPEN_SESSION_SIGNAL:
      return new OpenSessionIndication();

    case CDOProtocolConstants.RESOURCE_ID_SIGNAL:
      return new ResourceIDIndication();

    case CDOProtocolConstants.RESOURCE_PATH_SIGNAL:
      return new ResourcePathIndication();

    case CDOProtocolConstants.LOAD_OBJECT_SIGNAL:
      return new LoadObjectIndication();

    case CDOProtocolConstants.COMMIT_TRANSACTION_SIGNAL:
      return new CommitTransactionIndication();
    }

    return null;
  }
}
