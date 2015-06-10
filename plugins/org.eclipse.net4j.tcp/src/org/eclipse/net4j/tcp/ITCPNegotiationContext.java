/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.util.security.INegotiationContext;

/**
 * A {@link INegotiationContext negotiation context} that is used in TCP transport.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPNegotiationContext extends INegotiationContext
{
  public ITCPConnector getConnector();
}
