/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.Collection;
import java.util.List;

/**
 * Manages multiple {@link CDOAdminClient remote administration connections}.
 *
 * @author Eike Stepper
 */
public interface CDOAdminClientManager extends IContainer<CDOAdminClient>
{
  public IManagedContainer getContainer();

  public CDOAdminClient[] getConnections();

  public List<String> getConnectionURLs();

  public CDOAdminClient getConnection(String url);

  public int addConnections(Collection<String> urls);

  public boolean addConnection(String url);

  public boolean removeConnection(CDOAdminClient connection);
}
