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

import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolConstants;
import org.eclipse.net4j.buddies.internal.common.protocol.ProtocolUtil;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class InitiateCollaborationRequest extends RequestWithConfirmation<Long>
{
  private Collection<IBuddy> buddies;

  public InitiateCollaborationRequest(BuddiesClientProtocol protocol, Collection<IBuddy> buddies)
  {
    super(protocol, ProtocolConstants.SIGNAL_INITIATE_COLLABORATION);
    this.buddies = buddies;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    ProtocolUtil.writeBuddies(out, buddies);
  }

  @Override
  protected Long confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readLong();
  }
}
