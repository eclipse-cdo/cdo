/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
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
    super(protocol, CDOProtocolConstants.SIGNAL_OPENED_SESSION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    CDOServerProtocol protocol = (CDOServerProtocol)getProtocol();
    InternalSession session = protocol.getSession();

    if (in.readBoolean())
    {
      session.setOpenOnClientSide();
    }
    else
    {
      // Can't possibly happen.
      session.close();
    }
  }
}
