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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.protocol.ProtocolConstants;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class ClientProtocolFactory extends org.eclipse.internal.net4j.protocol.ClientProtocolFactory
{
  public static final String TYPE = ProtocolConstants.PROTOCOL_NAME;

  public ClientProtocolFactory()
  {
    super(TYPE);
  }

  public ClientProtocol create(String description)
  {
    return new ClientProtocol();
  }

  public static ClientProtocol get(IManagedContainer container, String description)
  {
    return (ClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
