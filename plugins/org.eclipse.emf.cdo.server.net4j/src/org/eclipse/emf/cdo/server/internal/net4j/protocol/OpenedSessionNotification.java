/*
 * Copyright (c) 2009-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.server.Session;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class OpenedSessionNotification extends Indication
{
  public OpenedSessionNotification(CDOServerProtocol protocol)
  {
    super(protocol, OM.SIGNAL_OPENED_SESSION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    CDOServerProtocol protocol = (CDOServerProtocol)getProtocol();
    InternalSession session = protocol.getSession();

    if (in.readBoolean())
    {
      ((Session)session).setOpenOnClientSide();
    }
    else
    {
      // Can't possibly happen.
      session.close();
    }
  }
}
