/*
 * Copyright (c) 2012, 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.admin.protocol;

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;
import org.eclipse.emf.cdo.spi.server.AuthenticationUtil;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.security.NotAuthenticatedException;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Eike Stepper
 */
public class CreateRepositoryIndication extends IndicationWithResponse
{
  private String name;

  private String type;

  private Map<String, Object> properties;

  public CreateRepositoryIndication(CDOAdminServerProtocol protocol)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_CREATE_REPOSITORY);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    name = in.readString();
    type = in.readString();
    properties = (Map<String, Object>)in.readObject();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    try
    {
      CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)getProtocol();
      out.writeBoolean(createRepository(protocol));
    }
    catch (NotAuthenticatedException ex)
    {
      // The user has canceled the authentication
      out.writeBoolean(false);
      return;
    }
  }

  protected boolean createRepository(CDOAdminServerProtocol protocol) throws Exception
  {
    final CDOAdminServer admin = protocol.getInfraStructure();

    return AuthenticationUtil.authenticatingOperation(protocol, new Callable<Boolean>()
    {
      @Override
      public Boolean call() throws Exception
      {
        CDOAdminRepository repository = admin.createRepository(name, type, properties);
        return repository != null;
      }
    }).call();
  }
}
