/*
 * Copyright (c) 2012, 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.emf.cdo.server.internal.admin.protocol;

import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServerRepository;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;
import org.eclipse.emf.cdo.spi.server.AuthenticationUtil;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.security.NotAuthenticatedException;

import java.util.concurrent.Callable;

/**
 * @author Eike Stepper
 */
public class DeleteRepositoryIndication extends IndicationWithResponse
{
  private String name;

  private String type;

  public DeleteRepositoryIndication(CDOAdminServerProtocol protocol)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_DELETE_REPOSITORY);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    name = in.readString();
    type = in.readString();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    try
    {
      CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)getProtocol();
      out.writeBoolean(deleteRepository(protocol));
    }
    catch (NotAuthenticatedException ex)
    {
      // The user has canceled the authentication
      out.writeBoolean(false);
      return;
    }
  }

  protected boolean deleteRepository(CDOAdminServerProtocol protocol) throws Exception
  {
    final CDOAdminServer admin = protocol.getInfraStructure();

    return AuthenticationUtil.authenticatingOperation(protocol, new Callable<Boolean>()
    {
      @Override
      public Boolean call() throws Exception
      {
        CDOAdminServerRepository repository = (CDOAdminServerRepository)admin.getRepository(name);
        if (repository != null)
        {
          return admin.deleteRepository(repository, type);
        }

        return false;
      }
    }).call();
  }
}
