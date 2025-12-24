/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.protocol;

import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServer;
import org.eclipse.emf.cdo.server.internal.admin.CDOAdminServerRepository;
import org.eclipse.emf.cdo.server.internal.admin.bundle.OM;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class QueryRepositoriesIndication extends IndicationWithResponse
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRepositoriesIndication.class);

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

    CDOAdminRepository[] repositories = admin.getRepositories();
    int size = repositories.length;
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} repository infos...", size); //$NON-NLS-1$
    }

    out.writeInt(size);
    for (int i = 0; i < size; i++)
    {
      CDOAdminServerRepository repository = (CDOAdminServerRepository)repositories[i];
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing repository info for {0}", repository.getName()); //$NON-NLS-1$
      }

      repository.write(out);
    }
  }
}
