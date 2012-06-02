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
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class QueryRepositoriesIndication extends IndicationWithResponse
{
  public QueryRepositoriesIndication(CDOAdminServerProtocol protocol)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_QUERY_REPOSITORIES);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    in.readBoolean();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)getProtocol();
    CDOAdminServer admin = protocol.getInfraStructure();

    CDOAdminServerRepository[] repositories = (CDOAdminServerRepository[])admin.getRepositories();
    out.writeInt(repositories.length);

    for (int i = 0; i < repositories.length; i++)
    {
      CDOAdminServerRepository repository = repositories[i];
      repository.write(out);
    }
  }
}
