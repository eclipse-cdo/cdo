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

import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.io.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.server.Session;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerRequest extends Request
{
  public CDOServerRequest(IChannel channel, short signalID)
  {
    super(extractProtocol(channel), signalID);
  }

  @Override
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected Session getSession()
  {
    return (Session)getProtocol().getSession();
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out) throws Exception
  {
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      public CDOIDProvider getIDProvider()
      {
        return getSession();
      }
    });
  }

  protected abstract void requesting(CDODataOutput out) throws IOException;

  private static CDOServerProtocol extractProtocol(IChannel channel)
  {
    if (LifecycleUtil.isActive(channel))
    {
      return (CDOServerProtocol)channel.getReceiveHandler();
    }

    throw new IllegalStateException("Channel is inactive: " + channel);
  }
}
