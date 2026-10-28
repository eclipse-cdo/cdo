/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.util.security.INegotiationContext;

/**
 * A negotiation context associated with a WebSocket connector.
 * <p>
 * The context exposes the connector involved in Net4j negotiation; it does not represent the provider's HTTP handshake.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSNegotiationContext extends INegotiationContext
{
  /**
   * Returns the connector whose Net4j negotiation is represented by this context.
   *
   * @return the WebSocket connector associated with this negotiation
   */
  public IWSConnector getConnector();
}
