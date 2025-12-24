/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl.RepositoryInfo;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTypeNotificationIndication extends CDOClientIndication
{
  public RepositoryTypeNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPOSITORY_TYPE_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDOCommonRepository.Type oldType = in.readEnum(CDOCommonRepository.Type.class);
    CDOCommonRepository.Type newType = in.readEnum(CDOCommonRepository.Type.class);

    CDONet4jSessionImpl session = (CDONet4jSessionImpl)getSession();
    RepositoryInfo repositoryInfo = (RepositoryInfo)session.getRepositoryInfo();
    repositoryInfo.setType(newType);
    session.handleRepositoryTypeChanged(oldType, newType);
  }
}
