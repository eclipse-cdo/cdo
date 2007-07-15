/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j;

import org.eclipse.net4j.IProtocol;
import org.eclipse.net4j.internal.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public abstract class ClientProtocolFactory<PRODUCT extends IProtocol> extends Factory<PRODUCT>
{
  public static final String CLIENT_PROTOCOL_GROUP = "org.eclipse.net4j.clientProtocols";

  public ClientProtocolFactory(String type)
  {
    super(CLIENT_PROTOCOL_GROUP, type);
  }
}
