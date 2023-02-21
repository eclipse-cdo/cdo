/*
 * Copyright (c) 2010-2012, 2017, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockGrade;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * @author Eike Stepper
 */
public class OpenViewIndication extends CDOServerReadIndication
{
  private int viewID;

  private boolean readOnly;

  private CDOBranchPoint branchPoint;

  private String durableLockingID;

  private boolean respondLockGrades;

  public OpenViewIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    viewID = in.readXInt();
    readOnly = in.readBoolean();

    if (in.readBoolean())
    {
      branchPoint = in.readCDOBranchPoint();
    }
    else
    {
      durableLockingID = in.readString();
      respondLockGrades = in.readBoolean();
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession session = getSession();

    if (branchPoint != null)
    {
      // Open normal view/transaction (not durable).
      IView newView;

      if (readOnly)
      {
        newView = session.openView(viewID, branchPoint);
      }
      else
      {
        newView = session.openTransaction(viewID, branchPoint);
      }

      respondNewView(out, newView, null);
    }
    else
    {
      // Open durable view/transaction.
      InternalLockManager lockManager = getRepository().getLockingManager();
      String message = null;

      BiConsumer<CDOID, LockGrade> lockConsumer = respondLockGrades ? (id, lockGrade) -> {
        try
        {
          out.writeCDOID(id);
          out.writeEnum(lockGrade);
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }
      } : null;

      try
      {
        try
        {
          lockManager.openView(session, viewID, readOnly, durableLockingID, newView -> {
            try
            {
              respondNewView(out, newView, null);
            }
            catch (IOException ex)
            {
              throw new IORuntimeException(ex);
            }
          }, lockConsumer);
        }
        catch (IORuntimeException ex)
        {
          Throwable cause = ex.getCause();
          if (cause instanceof IOException)
          {
            throw (IOException)cause;
          }

          throw ex;
        }

        if (respondLockGrades)
        {
          out.writeCDOID(null);
        }

        return;
      }
      catch (LockAreaNotFoundException ex)
      {
        // Client uses durableLockingID!=null && result==null to detect exceptional case
      }
      catch (IllegalStateException ex)
      {
        message = ex.getMessage();
      }

      respondNewView(out, null, message);
    }
  }

  private static void respondNewView(CDODataOutput out, IView newView, String message) throws IOException
  {
    if (newView != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(newView);
    }
    else
    {
      out.writeBoolean(false);
      out.writeString(message);
    }
  }
}
