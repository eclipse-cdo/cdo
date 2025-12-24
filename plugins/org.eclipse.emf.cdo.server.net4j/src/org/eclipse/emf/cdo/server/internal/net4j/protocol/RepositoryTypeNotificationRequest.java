/*
 * Copyright (c) 2010-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTypeNotificationRequest extends CDOServerRequest
{
  private CDOCommonRepository.Type oldType;

  private CDOCommonRepository.Type newType;

  public RepositoryTypeNotificationRequest(CDOServerProtocol serverProtocol, CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_REPOSITORY_TYPE_NOTIFICATION);
    this.oldType = oldType;
    this.newType = newType;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeEnum(oldType);
    out.writeEnum(newType);
  }
}
