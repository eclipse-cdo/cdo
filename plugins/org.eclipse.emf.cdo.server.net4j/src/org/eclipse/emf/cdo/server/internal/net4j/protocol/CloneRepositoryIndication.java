/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.WrappedException;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CloneRepositoryIndication extends CDOServerIndication
{
  private long startTime;

  private long endTime;

  private int branchID;

  public CloneRepositoryIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CLONE_REPOSITORY);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    startTime = in.readLong();
    endTime = in.readLong();
    branchID = in.readInt();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    getRepository().clone(new CDOCloningContext()
    {
      public long getStartTime()
      {
        return startTime;
      }

      public long getEndTime()
      {
        return endTime;
      }

      public int getBranchID()
      {
        return branchID;
      }

      public void addPackageUnit(String id)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.CLONING_PACKAGE);
          out.writeString(id);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      public void addBranch(int id)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.CLONING_BRANCH);
          out.writeInt(id);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      public void addRevision(InternalCDORevision revision)
      {
        try
        {
          out.writeByte(CDOProtocolConstants.CLONING_REVISION);
          out.writeCDORevision(revision, CDORevision.UNCHUNKED);
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    });

    out.writeByte(CDOProtocolConstants.CLONING_FINISHED);
  }
}
