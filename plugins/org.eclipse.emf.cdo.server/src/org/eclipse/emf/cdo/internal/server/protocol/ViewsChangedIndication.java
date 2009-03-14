/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.ImplementationError;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ViewsChangedIndication extends CDOServerIndication
{
  public ViewsChangedIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_VIEWS_CHANGED);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    byte kind = in.readByte();
    Session session = getSession();

    switch (kind)
    {
    case CDOProtocolConstants.VIEW_CLOSED:
      IView view = session.getView(viewID);
      if (view != null)
      {
        view.close();
      }
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
