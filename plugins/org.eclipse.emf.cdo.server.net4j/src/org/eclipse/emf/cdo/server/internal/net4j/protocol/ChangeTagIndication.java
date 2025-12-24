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
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class ChangeTagIndication extends CDOServerWriteIndication
{
  private final AtomicInteger modCount = new AtomicInteger();

  private CDOBranchPoint result;

  public ChangeTagIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_CHANGE_TAG);
  }

  @Override
  protected void prepareStoreThreadLocal()
  {
    // Needed by Repository.changeTag()!
    StoreThreadLocal.setSession(getSession());
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    modCount.set(in.readXInt());

    String oldName = in.readString();
    String newName = in.readString();
    CDOBranchPoint branchPoint = in.readBoolean() ? in.readCDOBranchPoint() : null;

    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    result = branchManager.changeTagWithModCount(modCount, oldName, newName, branchPoint);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    if (result != null)
    {
      out.writeXInt(modCount.get());
      out.writeCDOBranchPoint(result);
    }
    else
    {
      out.writeXInt(-1);
    }
  }
}
