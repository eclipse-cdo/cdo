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

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class QueryRepositoriesRequest extends RequestWithConfirmation<Object>
{
  private Set<CDOAdminRepository> result;

  public QueryRepositoriesRequest(CDOAdminClientProtocol protocol, Set<CDOAdminRepository> result)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_QUERY_REPOSITORIES);
    this.result = result;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(true);
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws Exception
  {
    CDOAdminClientProtocol protocol = (CDOAdminClientProtocol)getProtocol();
    CDOAdminClient admin = protocol.getInfraStructure();

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOAdminClientRepository repository = new CDOAdminClientRepository(admin, in);
      result.add(repository);
    }

    return null;
  }
}
