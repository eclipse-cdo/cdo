/*
 * Copyright (c) 2009-2012, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalView;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionIndication extends CDOServerReadIndication
{
  public ChangeSubscriptionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    boolean subscribeMode = true;

    int viewID = in.readXInt();
    boolean clear = in.readBoolean();
    int size = in.readXInt();
    if (size <= 0)
    {
      subscribeMode = false;
      size = -size;
    }

    InternalView view = getView(viewID);
    if (clear)
    {
      view.clearChangeSubscription();
    }

    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      if (subscribeMode)
      {
        view.subscribe(id);
      }
      else
      {
        view.unsubscribe(id);
      }
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
  }
}
