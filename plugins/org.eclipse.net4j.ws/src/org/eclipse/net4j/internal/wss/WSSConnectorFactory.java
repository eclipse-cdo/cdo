/*
 * Copyright (c) 2024 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.net4j.internal.wss;

import org.eclipse.net4j.internal.ws.WSClientConnector;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
import org.eclipse.net4j.wss.WSSUtil;

/**
 * @author mporhel
 */
public class WSSConnectorFactory extends WSConnectorFactory
{
  public WSSConnectorFactory()
  {
    super(WSSUtil.FACTORY_TYPE);
  }

  @Override
  protected WSClientConnector createConnector()
  {
    return new WSSClientConnector();
  }
}
