/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.repositories;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDONameProvider;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionProvider;

import org.eclipse.net4j.util.container.IContainer;

/**
 * A CDO server independent representation of a repository.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDORepository extends CDOExplorerElement, IContainer<CDOBranch>, CDONameProvider, CDOSessionProvider
{
  public static final String TYPE_REMOTE = "remote";

  public static final String TYPE_CLONE = "clone";

  public static final String TYPE_LOCAL = "local";

  public String getConnectorType();

  public String getConnectorDescription();

  public String getName();

  public String getURI();

  public State getState();

  public boolean isConnected();

  public void connect();

  public void disconnect();

  public CDOCheckout[] getCheckouts();

  public CDOSession getSession();

  public CDOSession acquireSession();

  public void releaseSession();

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    Connecting, Connected, Disconnecting, Disconnected
  }
}
