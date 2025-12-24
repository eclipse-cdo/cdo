/*
 * Copyright (c) 2009-2012, 2015, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryTimeIndication extends CDOServerIndication
{
  private long indicated;

  public RepositoryTimeIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPOSITORY_TIME);
  }

  public RepositoryTimeIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    indicated = getRepository().getTimeStamp();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    long responded = getRepository().getTimeStamp();

    out.writeXLong(indicated);
    out.writeXLong(responded);
  }
}
