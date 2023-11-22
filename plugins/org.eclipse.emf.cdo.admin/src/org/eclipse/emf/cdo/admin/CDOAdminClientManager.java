/*
 * Copyright (c) 2012, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.admin;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;

import java.util.Collection;
import java.util.List;

/**
 * Manages multiple {@link CDOAdminClient remote administration connections}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAdminClientManager extends IContainer<CDOAdminClient>, IManagedContainerProvider
{
  public CDOAdminClient[] getConnections();

  public List<String> getConnectionURLs();

  public CDOAdminClient getConnection(String url);

  /**
   * @since 4.4
   */
  public int setConnections(Collection<String> urls);

  /**
   * @since 4.4
   */
  public boolean setConnection(String url);

  public int addConnections(Collection<String> urls);

  public boolean addConnection(String url);

  public boolean removeConnection(CDOAdminClient connection);
}
