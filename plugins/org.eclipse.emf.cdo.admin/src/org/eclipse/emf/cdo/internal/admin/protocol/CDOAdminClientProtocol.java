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
package org.eclipse.emf.cdo.internal.admin.protocol;

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.admin.CDOAdminClientImpl;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminClientProtocol extends SignalProtocol<CDOAdminClientImpl>
{
  public CDOAdminClientProtocol(CDOAdminClientImpl admin)
  {
    super(CDOAdminProtocolConstants.PROTOCOL_NAME);
    setInfraStructure(admin);
  }

  public Set<CDOAdminRepository> queryRepositories()
  {
    return send(new QueryRepositoriesRequest(this));
  }

  public boolean createRepository(String name, String type, Map<String, Object> properties)
  {
    return send(new CreateRepositoryRequest(this, name, type, properties));
  }

  public boolean deleteRepository(String name, String type)
  {
    return send(new DeleteRepositoryRequest(this, name, type));
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case CDOAdminProtocolConstants.SIGNAL_REPOSITORY_ADDED:
      return new RepositoryAddedIndication(this);

    case CDOAdminProtocolConstants.SIGNAL_REPOSITORY_REMOVED:
      return new RepositoryRemovedIndication(this);

    case CDOAdminProtocolConstants.SIGNAL_REPOSITORY_TYPE_CHANGED:
      return new RepositoryTypeChangedIndication(this);

    case CDOAdminProtocolConstants.SIGNAL_REPOSITORY_STATE_CHANGED:
      return new RepositoryStateChangedIndication(this);

    case CDOAdminProtocolConstants.SIGNAL_REPOSITORY_REPLICATION_PROGRESSED:
      return new RepositoryReplicationProgressedIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  private <RESULT> RESULT send(RequestWithConfirmation<RESULT> request)
  {
    try
    {
      return request.send();
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }
}
