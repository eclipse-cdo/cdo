/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.protocol;

import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServerRepository;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

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
    CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)getProtocol();
    CDOAdminServer admin = protocol.getInfraStructure();

    CDOAdminServerRepository repository = (CDOAdminServerRepository)admin.getRepository(name);
    if (repository != null)
    {
      if (admin.deleteRepository(repository, type))
      {
        out.writeBoolean(true);
        return;
      }
    }

    out.writeBoolean(false);
  }
}
