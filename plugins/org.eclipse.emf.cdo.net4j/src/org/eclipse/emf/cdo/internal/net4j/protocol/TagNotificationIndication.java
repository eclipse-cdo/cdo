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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class TagNotificationIndication extends CDOClientIndication
{
  public TagNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_TAG_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int modCount = in.readXInt();
    String oldName = in.readString();
    String newName = in.readString();
    CDOBranchPoint branchPoint = in.readBoolean() ? in.readCDOBranchPoint() : null;

    InternalCDOBranchManager branchManager = getSession().getBranchManager();
    branchManager.handleTagChanged(modCount, oldName, newName, branchPoint);
  }
}
