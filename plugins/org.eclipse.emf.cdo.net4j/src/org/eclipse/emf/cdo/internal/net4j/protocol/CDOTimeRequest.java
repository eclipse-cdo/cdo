/*
 * Copyright (c) 2009-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol.RepositoryTimeResult;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOTimeRequest<RESULT> extends CDOClientRequest<RESULT>
{
  private RepositoryTimeResult repositoryTimeResult = new RepositoryTimeResult();

  public CDOTimeRequest(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  public RepositoryTimeResult getRepositoryTimeResult()
  {
    return repositoryTimeResult;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    repositoryTimeResult.setRequested(System.currentTimeMillis());
  }

  @Override
  protected RESULT confirming(CDODataInput in) throws IOException
  {
    repositoryTimeResult.setConfirmed(System.currentTimeMillis());
    repositoryTimeResult.setIndicated(in.readXLong());
    repositoryTimeResult.setResponded(in.readXLong());
    return null;
  }
}
