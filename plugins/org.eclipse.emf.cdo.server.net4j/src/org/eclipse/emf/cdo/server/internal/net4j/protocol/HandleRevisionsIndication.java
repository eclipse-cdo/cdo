/*
 * Copyright (c) 2010-2012, 2016, 2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;

import org.eclipse.emf.ecore.EClass;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class HandleRevisionsIndication extends CDOServerReadIndication
{
  private EClass eClass;

  private CDOBranch branch;

  private boolean exactBranch;

  private long timeStamp;

  private boolean exactTime;

  public HandleRevisionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_HANDLE_REVISIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    if (in.readBoolean())
    {
      eClass = (EClass)in.readCDOClassifierRefAndResolve();
    }

    if (in.readBoolean())
    {
      branch = in.readCDOBranch();
      exactBranch = in.readBoolean();
    }

    timeStamp = in.readXLong();
    exactTime = in.readBoolean();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    final IOException[] ioException = { null };
    final RuntimeException[] runtimeException = { null };

    getRepository().handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
    {
      @Override
      public boolean handleRevision(CDORevision revision)
      {
        try
        {
          out.writeBoolean(true);
          out.writeCDORevision(revision, CDORevision.UNCHUNKED); // Exposes revision to client side
          return true;
        }
        catch (IOException ex)
        {
          ioException[0] = ex;
        }
        catch (RuntimeException ex)
        {
          runtimeException[0] = ex;
        }

        return false;
      }
    }));

    if (ioException[0] != null)
    {
      throw ioException[0];
    }

    if (runtimeException[0] != null)
    {
      throw runtimeException[0];
    }

    out.writeBoolean(false);
  }
}
