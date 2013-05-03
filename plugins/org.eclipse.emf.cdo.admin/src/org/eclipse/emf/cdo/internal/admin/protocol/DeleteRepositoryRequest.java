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
package org.eclipse.emf.cdo.internal.admin.protocol;

import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class DeleteRepositoryRequest extends RequestWithConfirmation<Boolean>
{
  private String name;

  private String type;

  public DeleteRepositoryRequest(CDOAdminClientProtocol protocol, String name, String type)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_DELETE_REPOSITORY);
    this.name = name;
    this.type = type;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(name);
    out.writeString(type);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
