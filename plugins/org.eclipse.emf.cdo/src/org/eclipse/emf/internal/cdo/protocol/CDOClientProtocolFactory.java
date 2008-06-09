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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;

import org.eclipse.net4j.protocol.ClientProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 */
public final class CDOClientProtocolFactory extends ClientProtocolFactory
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  public CDOClientProtocolFactory()
  {
    super(TYPE);
  }

  public CDOClientProtocol create(String description)
  {
    return new CDOClientProtocol();
  }

  public static CDOClientProtocol get(IManagedContainer container, String description)
  {
    return (CDOClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
