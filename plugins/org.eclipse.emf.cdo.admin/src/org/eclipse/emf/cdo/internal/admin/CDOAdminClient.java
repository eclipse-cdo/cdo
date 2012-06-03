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
package org.eclipse.emf.cdo.internal.admin;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.internal.admin.protocol.CDOAdminClientProtocol;
import org.eclipse.emf.cdo.spi.common.admin.AbstractCDOAdmin;

import org.eclipse.net4j.connector.IConnector;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOAdminClient extends AbstractCDOAdmin
{
  private final IConnector connector;

  private CDOAdminClientProtocol protocol;

  public CDOAdminClient(IConnector connector, long timeout)
  {
    super(timeout);
    this.connector = connector;
    activate();
  }

  public final IConnector getConnector()
  {
    return connector;
  }

  public void repositoryTypeChanged(String name, Type oldType, Type newType)
  {
    CDOAdminClientRepository repository = (CDOAdminClientRepository)getRepository(name);
    if (repository != null)
    {
      repository.typeChanged(oldType, newType);
    }
  }

  public void repositoryStateChanged(String name, State oldState, State newState)
  {
    CDOAdminClientRepository repository = (CDOAdminClientRepository)getRepository(name);
    if (repository != null)
    {
      repository.stateChanged(oldState, newState);
    }
  }

  @Override
  protected boolean doCreateRepository(String name, String type, Map<String, Object> properties)
  {
    return protocol.createRepository(name, type, properties);
  }

  @Override
  protected boolean doDeleteRepository(String name, String type)
  {
    return protocol.deleteRepository(name, type);
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    protocol = new CDOAdminClientProtocol(this);
    protocol.queryRepositories(getSet());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    protocol.close();
    super.doDeactivate();
  }
}
