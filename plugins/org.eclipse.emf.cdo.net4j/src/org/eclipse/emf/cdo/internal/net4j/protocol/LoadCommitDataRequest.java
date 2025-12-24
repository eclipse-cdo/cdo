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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadCommitDataRequest extends CDOClientRequest<CDOCommitData>
{
  private long timeStamp;

  public LoadCommitDataRequest(CDOClientProtocol protocol, long timeStamp)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_COMMIT_DATA);
    this.timeStamp = timeStamp;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXLong(timeStamp);
  }

  @Override
  protected CDOCommitData confirming(CDODataInput in) throws IOException
  {
    try
    {
      CDOSetFeatureDeltaImpl.transferOldValue(true);
      return in.readCDOCommitData();
    }
    finally
    {
      CDOSetFeatureDeltaImpl.transferOldValue(false);
    }
  }
}
