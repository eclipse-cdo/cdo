/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class ChangeTagRequest extends CDOClientRequest<CDOBranchPoint>
{
  private AtomicInteger modCount;

  private String oldName;

  private String newName;

  private CDOBranchPoint branchPoint;

  public ChangeTagRequest(CDOClientProtocol protocol, AtomicInteger modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_TAG);
    this.modCount = modCount;
    this.oldName = oldName;
    this.newName = newName;
    this.branchPoint = branchPoint;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(modCount.get());
    out.writeString(oldName);
    out.writeString(newName);

    if (branchPoint != null)
    {
      out.writeBoolean(true);
      out.writeCDOBranchPoint(branchPoint);
    }
    else
    {
      out.writeBoolean(false);
    }
  }

  @Override
  protected CDOBranchPoint confirming(CDODataInput in) throws IOException
  {
    int tagModCount = in.readXInt();
    if (tagModCount != -1)
    {
      modCount.set(tagModCount);
      return in.readCDOBranchPoint();
    }

    return null;
  }
}
