/*
 * Copyright (c) 2010-2012, 2016, 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

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
public class HandleRevisionsRequest extends CDOClientRequest<Boolean>
{
  private EClass eClass;

  private CDOBranch branch;

  private boolean exactBranch;

  private long timeStamp;

  private boolean exactTime;

  private CDORevisionHandler handler;

  public HandleRevisionsRequest(CDOClientProtocol protocol, EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_HANDLE_REVISIONS);
    this.eClass = eClass;
    this.branch = branch;
    this.exactBranch = exactBranch;
    this.timeStamp = timeStamp;
    this.exactTime = exactTime;
    this.handler = handler;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (eClass != null)
    {
      out.writeBoolean(true);
      out.writeCDOClassifierRef(eClass);
    }
    else
    {
      out.writeBoolean(false);
    }

    if (branch != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranch(branch);
      out.writeBoolean(exactBranch);
    }
    else
    {
      out.writeBoolean(false);
    }

    out.writeXLong(timeStamp);
    out.writeBoolean(exactTime);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    while (in.readBoolean())
    {
      CDORevision revision = in.readCDORevision();
      handler.handleRevision(revision);
    }

    return true;
  }
}
