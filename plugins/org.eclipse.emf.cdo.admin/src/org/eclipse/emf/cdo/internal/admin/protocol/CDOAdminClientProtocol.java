/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.internal.admin.protocol;

import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.internal.admin.CDOAdminClientImpl;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.signal.security.AuthenticatingSignalProtocol;
import org.eclipse.net4j.signal.security.AuthenticationIndication;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CDOAdminClientProtocol extends AuthenticatingSignalProtocol<CDOAdminClientImpl>
{
  public CDOAdminClientProtocol(CDOAdminClientImpl admin)
  {
    super(CDOAdminProtocolConstants.PROTOCOL_NAME);
    setInfraStructure(admin);
  }

  public Set<CDOAdminClientRepository> queryRepositories()
  {
    return send(new QueryRepositoriesRequest(this));
  }

  public boolean createRepository(String name, String type, Map<String, Object> properties)
  {
    // Do not apply a timeout to this request because it requires Administrator
    // authentication to authorize, which is interactive with the user
    return send(new CreateRepositoryRequest(this, name, type, properties), NO_TIMEOUT);
  }

  public boolean deleteRepository(String name, String type)
  {
    // Do not apply a timeout to this request because it requires Administrator
    // authentication to authorize, which is interactive with the user
    return send(new DeleteRepositoryRequest(this, name, type), NO_TIMEOUT);
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

    case CDOAdminProtocolConstants.SIGNAL_AUTHENTICATION:
      return new AuthenticationIndication(this, CDOAdminProtocolConstants.SIGNAL_AUTHENTICATION);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  private <RESULT> RESULT send(RequestWithConfirmation<RESULT> request)
  {
    return send(request, getTimeout());
  }

  private <RESULT> RESULT send(RequestWithConfirmation<RESULT> request, long timeout)
  {
    try
    {
      return request.send(timeout);
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
