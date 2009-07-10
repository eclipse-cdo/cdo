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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalSession;

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
    byte viewType = in.readByte();
    InternalSession session = getSession();

    if (viewType == CDOProtocolConstants.VIEW_CLOSED)
    {
      IView view = session.getView(viewID);
      if (view != null)
      {
        view.close();
      }
    }
    else
    {
      switch (CDOCommonView.Type.values()[viewType])
      {
      case TRANSACTION:
        session.openTransaction(viewID);
        break;

      case READONLY:
        session.openView(viewID);
        break;

      case AUDIT:
        long timeStamp = in.readLong();
        session.openAudit(viewID, timeStamp);
        break;
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
