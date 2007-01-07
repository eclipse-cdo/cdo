/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport.embedded;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class ServerEmbeddedConnectorImpl extends AbstractEmbeddedConnector
{
  public ServerEmbeddedConnectorImpl(ClientEmbeddedConnectorImpl clientPeer)
  {
    setPeer(clientPeer);
  }

  public Type getType()
  {
    return Type.SERVER;
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.onDeactivate();
  }
}
