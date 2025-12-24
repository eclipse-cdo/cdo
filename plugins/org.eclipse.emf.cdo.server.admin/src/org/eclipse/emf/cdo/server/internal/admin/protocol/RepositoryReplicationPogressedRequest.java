/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class RepositoryReplicationPogressedRequest extends Request
{
  private String name;

  private double totalWork;

  private double work;

  public RepositoryReplicationPogressedRequest(CDOAdminServerProtocol serverProtocol, String name, double totalWork, double work)
  {
    super(serverProtocol, CDOAdminProtocolConstants.SIGNAL_REPOSITORY_REPLICATION_PROGRESSED);
    this.name = name;
    this.totalWork = totalWork;
    this.work = work;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(name);
    out.writeDouble(totalWork);
    out.writeDouble(work);
  }
}
