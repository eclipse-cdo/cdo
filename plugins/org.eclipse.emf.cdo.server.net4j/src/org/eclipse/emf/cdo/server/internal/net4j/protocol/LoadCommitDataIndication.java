/*
 * Copyright (c) 2010-2012, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadCommitDataIndication extends CDOServerReadIndication
{
  private long timeStamp;

  public LoadCommitDataIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_COMMIT_DATA);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    timeStamp = in.readXLong();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    CDOCommitData commitData = getRepository().loadCommitData(timeStamp);

    try
    {
      CDOSetFeatureDeltaImpl.transferOldValue(true);
      out.writeCDOCommitData(commitData); // Exposes revision to client side
    }
    finally
    {
      CDOSetFeatureDeltaImpl.transferOldValue(false);
    }
  }
}
