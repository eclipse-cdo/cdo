/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDORemoteSessionManager extends CDORemoteSessionManager, ILifecycle.Introspection
{
  public CDORemoteSession createRemoteSession(int sessionID, String userID, boolean subscribed);

  public void handleRemoteSessionOpened(int sessionID, String userID);

  public void handleRemoteSessionClosed(int sessionID);

  public void handleRemoteSessionSubscribed(int sessionID, boolean subscribed);
}
