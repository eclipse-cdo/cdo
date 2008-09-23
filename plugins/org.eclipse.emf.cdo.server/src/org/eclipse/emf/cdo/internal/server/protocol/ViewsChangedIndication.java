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

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.Session;

import org.eclipse.net4j.util.ImplementationError;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ViewsChangedIndication extends CDOServerIndication // Indication
{
  public ViewsChangedIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_VIEWS_CHANGED;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    byte kind = in.readByte();
    CDOServerProtocol protocol = getProtocol();
    Session session = protocol.getSession();

    switch (kind)
    {
    case CDOProtocolConstants.VIEW_CLOSED:
      session.closeView(viewID);
      break;

    case CDOProtocolConstants.VIEW_TRANSACTION:
      session.openTransaction(viewID);
      break;

    case CDOProtocolConstants.VIEW_READONLY:
      session.openView(viewID);
      break;

    case CDOProtocolConstants.VIEW_AUDIT:
      long timeStamp = in.readLong();
      session.openAudit(viewID, timeStamp);
      break;

    default:
      throw new ImplementationError("Invalid kind: " + kind);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
