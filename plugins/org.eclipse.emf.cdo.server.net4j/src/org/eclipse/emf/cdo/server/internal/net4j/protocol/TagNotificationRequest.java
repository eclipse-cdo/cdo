/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class TagNotificationRequest extends CDOServerRequest
{
  private int modCount;

  private String oldName;

  private String newName;

  private CDOBranchPoint branchPoint;

  public TagNotificationRequest(CDOServerProtocol serverProtocol, int modCount, String oldName, String newName, CDOBranchPoint branchPoint)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_TAG_NOTIFICATION);
    this.modCount = modCount;
    this.oldName = oldName;
    this.newName = newName;
    this.branchPoint = branchPoint;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeXInt(modCount);
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
}
