/*
 * Copyright (c) 2010-2013, 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class BranchNotificationRequest extends CDOServerRequest
{
  private final ChangeKind changeKind;

  private final CDOBranch[] branches;

  public BranchNotificationRequest(CDOServerProtocol serverProtocol, ChangeKind changeKind, CDOBranch... branches)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_BRANCH_NOTIFICATION);
    this.changeKind = changeKind;
    this.branches = branches;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeEnum(changeKind);
    out.writeXInt(branches.length);

    for (int i = 0; i < branches.length; i++)
    {
      CDOBranch branch = branches[i];
      out.writeXInt(branch.getID());

      if (changeKind == ChangeKind.RENAMED)
      {
        out.writeString(branch.getName());
        break;
      }
    }
  }
}
