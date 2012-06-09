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
import org.eclipse.emf.cdo.internal.admin.CDOAdminClientImpl;
import org.eclipse.emf.cdo.internal.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class QueryRepositoriesRequest extends RequestWithConfirmation<Set<CDOAdminRepository>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRepositoriesRequest.class);

  public QueryRepositoriesRequest(CDOAdminClientProtocol protocol)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_QUERY_REPOSITORIES);
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(true);
  }

  @Override
  protected Set<CDOAdminRepository> confirming(ExtendedDataInputStream in) throws Exception
  {
    CDOAdminClientProtocol protocol = (CDOAdminClientProtocol)getProtocol();
    CDOAdminClientImpl admin = protocol.getInfraStructure();

    int size = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.format("Reading {0} repository infos...", size); //$NON-NLS-1$
    }

    Set<CDOAdminRepository> result = new HashSet<CDOAdminRepository>();
    for (int i = 0; i < size; i++)
    {
      CDOAdminClientRepository repository = new CDOAdminClientRepository(admin, in);
      result.add(repository);
      if (TRACER.isEnabled())
      {
        TRACER.format("Read repository info for {0}", repository.getName()); //$NON-NLS-1$
      }
    }

    return result;
  }
}
