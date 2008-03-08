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
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.CDOProtocolImpl;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocol extends CDOProtocolImpl
{
  public CDOServerProtocol()
  {
  }

  @Override
  public Session getSession()
  {
    return (Session)super.getSession();
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOProtocolConstants.SIGNAL_OPEN_SESSION:
      return new OpenSessionIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_LIBRARIES:
      return new LoadLibrariesIndication();

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

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_TIME:
      return new LoadRevisionByTimeIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_REVISION_BY_VERSION:
      return new LoadRevisionByVersionIndication();

    case CDOProtocolConstants.SIGNAL_LOAD_CHUNK:
      return new LoadChunkIndication();

    case CDOProtocolConstants.SIGNAL_VERIFY_REVISION:
      return new VerifyRevisionIndication();

    case CDOProtocolConstants.SIGNAL_QUERY_OBJECT_TYPES:
      return new QueryObjectTypesIndication();

    case CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION:
      return new CommitTransactionIndication();
    }

    return null;
  }
}
