/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class RepositoryTypeChangedRequest extends Request
{
  private String name;

  private Type oldType;

  private Type newType;

  public RepositoryTypeChangedRequest(CDOAdminServerProtocol serverProtocol, String name, Type oldType, Type newType)
  {
    super(serverProtocol, CDOAdminProtocolConstants.SIGNAL_REPOSITORY_TYPE_CHANGED);
    this.name = name;
    this.oldType = oldType;
    this.newType = newType;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(name);
    out.writeEnum(oldType);
    out.writeEnum(newType);
  }
}
