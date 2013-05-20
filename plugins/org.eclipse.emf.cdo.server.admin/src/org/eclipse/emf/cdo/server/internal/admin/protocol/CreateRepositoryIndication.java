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

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Map;

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
    CDOAdminServerProtocol protocol = (CDOAdminServerProtocol)getProtocol();
    CDOAdminServer admin = protocol.getInfraStructure();

    CDOAdminRepository repository = admin.createRepository(name, type, properties);
    out.writeBoolean(repository != null);
  }
}
