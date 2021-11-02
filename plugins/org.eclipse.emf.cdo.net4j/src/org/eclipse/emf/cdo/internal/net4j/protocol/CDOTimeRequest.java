/*
 * Copyright (c) 2009-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
