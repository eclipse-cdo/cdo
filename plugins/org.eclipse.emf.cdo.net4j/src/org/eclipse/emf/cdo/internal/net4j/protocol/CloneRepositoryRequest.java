/***************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOCloningContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;


import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CloneRepositoryRequest extends CDOClientRequest<Boolean>
{
  private CDOCloningContext context;

  public CloneRepositoryRequest(CDOClientProtocol protocol, CDOCloningContext context)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_VIEW);
    this.context = context;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeLong(context.getStartTime());
    out.writeLong(context.getEndTime());
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    for (;;)
    {
      byte opcode = in.readByte();
      switch (opcode)
      {
      case CDOProtocolConstants.CLONING_FINISHED:
        return true;

      case CDOProtocolConstants.CLONING_PACKAGE:
        context.addPackageUnit(in.readCDOPackageURI());
        break;

      case CDOProtocolConstants.CLONING_BRANCH:
        context.addBranch(in.readInt());
        break;

      case CDOProtocolConstants.CLONING_REVISION:
        context.addRevision((InternalCDORevision)in.readCDORevision());
        break;

      default:
        throw new IOException("Invalid cloning opcode: " + opcode);
      }
    }
  }
}
