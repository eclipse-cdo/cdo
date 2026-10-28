/*
 * Copyright (c) 2024-2026 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.ws.WSSUtil;

/**
 * Creates provider-neutral WSS connector configurations.
 *
 * @author Eike Stepper
 */
public class WSSConnectorFactory extends WSConnectorFactory
{
  public WSSConnectorFactory()
  {
    super(WSSUtil.FACTORY_TYPE);
  }
}
