/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies;

import org.eclipse.net4j.buddies.spi.common.ClientFacilityFactory;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.buddies.protocol.BuddiesClientProtocol;
import org.eclipse.net4j.internal.buddies.protocol.OpenSessionRequest;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class BuddiesUtil
{
  private BuddiesUtil()
  {
  }

  public static Set<String> getFacilityTypes()
  {
    return IPluginContainer.INSTANCE.getFactoryTypes(ClientFacilityFactory.PRODUCT_GROUP);
  }

  public static IBuddySession openSession(IConnector connector, String userID, String password, long timeout)
  {
    try
    {
      BuddiesClientProtocol protocol = new BuddiesClientProtocol(connector);
      OpenSessionRequest request = new OpenSessionRequest(protocol, userID, password, getFacilityTypes());
      return request.send(timeout);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static IBuddySession openSession(IConnector connector, String userID, String password)
  {
    return openSession(connector, userID, password, RequestWithConfirmation.NO_TIMEOUT);
  }
}
