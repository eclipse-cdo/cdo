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
package org.eclipse.emf.cdo.internal.admin.protocol;

import org.eclipse.emf.cdo.spi.common.admin.CDOAdminProtocolConstants;

import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CreateRepositoryRequest extends RequestWithConfirmation<Boolean>
{
  private String name;

  private String type;

  private Map<String, Object> properties;

  public CreateRepositoryRequest(CDOAdminClientProtocol protocol, String name, String type, Map<String, Object> properties)
  {
    super(protocol, CDOAdminProtocolConstants.SIGNAL_CREATE_REPOSITORY);
    this.name = name;
    this.type = type;
    this.properties = properties;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(name);
    out.writeString(type);
    out.writeObject(properties);
  }

  @Override
  protected Boolean confirming(ExtendedDataInputStream in) throws Exception
  {
    return in.readBoolean();
  }
}
