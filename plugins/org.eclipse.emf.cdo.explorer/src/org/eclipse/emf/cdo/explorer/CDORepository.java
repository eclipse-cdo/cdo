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
package org.eclipse.emf.cdo.explorer;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.session.CDOSessionProvider;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.core.runtime.IAdaptable;

/**
 * A CDO server independent representation of a repository.
 *
 * @author Eike Stepper
 * @since 4.4
 * @apiviz.landmark
 */
public interface CDORepository extends IContainer<CDOBranch>, CDOSessionProvider, IAdaptable
{
  public String getLabel();

  public void setLabel(String label);

  public CDORepositoryManager getRepositoryManager();

  public String getConnectorType();

  public String getConnectorDescription();

  public String getRepositoryName();

  public State getState();

  public boolean isConnected();

  public void connect();

  public void disconnect();

  public CDOCheckout[] getCheckouts();

  /**
   * @author Eike Stepper
   */
  public enum State
  {
    Connecting, Connected, Disconnecting, Disconnected
  }
}
