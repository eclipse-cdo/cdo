/*
 * Copyright (c) 2009, 2011, 2012, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

/**
 * Internal representation of a client session connected to the local session through remote-session notifications.
 * Its subscription state is maintained by the remote-session manager as topics are subscribed to or left.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDORemoteSession extends CDORemoteSession
{
  @Override
  public InternalCDORemoteSessionManager getManager();

  public void setSubscribed(boolean subscribed);
}
