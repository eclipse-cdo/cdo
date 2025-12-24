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
package org.eclipse.net4j.internal.buddies.protocol;

import org.eclipse.net4j.buddies.internal.common.protocol.CollaborationLeftIndication;
import org.eclipse.net4j.internal.buddies.Self;

/**
 * @author Eike Stepper
 */
public class ClientCollaborationLeftIndication extends CollaborationLeftIndication
{
  public ClientCollaborationLeftIndication(BuddiesClientProtocol protocol, Self self)
  {
    super(protocol, self.getSession(), self);
  }
}
