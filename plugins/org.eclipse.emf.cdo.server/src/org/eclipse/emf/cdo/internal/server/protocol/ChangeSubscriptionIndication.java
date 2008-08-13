/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.internal.server.View;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ChangeSubscriptionIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL,
      ChangeSubscriptionIndication.class);

  public ChangeSubscriptionIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_CHANGE_SUBSCRIPTION;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    CDOIDObjectFactory factory = getStore().getCDOIDObjectFactory();
    boolean subscribeMode = true;

    int viewID = in.readInt();
    boolean clear = in.readBoolean();
    int size = in.readInt();
    if (size <= 0)
    {
      subscribeMode = false;
      size = -size;
    }

    View view = (View)getSession().getView(viewID);
    if (clear)
    {
      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.trace("Clear subscription");
      }

      view.clearChangeSubscription();
    }

    for (int i = 0; i < size; i++)
    {
      CDOID id = CDOIDUtil.read(in, factory);
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
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeBoolean(true);
  }
}
