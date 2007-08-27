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

import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends SignalProtocol<IRepositoryProvider>
{
  private Session session;

  public CDOServerProtocol()
  {
  }

  public String getType()
  {
    return CDOProtocolConstants.PROTOCOL_NAME;
  }

  public Session getSession()
  {
    return session;
  }

  public void setSession(Session session)
  {
    this.session = session;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication();

    case CDOProtocolConstants.SIGNAL_VIEWS_CHANGED:
      return new ViewsChangedIndication();

    case CDOProtocolConstants.SIGNAL_RESOURCE_ID:
      return new ResourceIDIndication();

    case CDOProtocolConstants.SIGNAL_RESOURCE_PATH:
      return new ResourcePathIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_PACKAGE:
      return new LoadPackageIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION:
      return new LoadRevisionIndication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication();
    }

    return null;
  }
}
