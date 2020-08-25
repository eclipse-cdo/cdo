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
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class LoadTagsRequest extends CDOClientRequest<Integer>
{
  private String name;

  private Consumer<BranchInfo> handler;

  public LoadTagsRequest(CDOClientProtocol protocol, String name, Consumer<BranchInfo> handler)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_TAGS);
    this.name = name;
    this.handler = handler;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeString(name);
  }

  @Override
  protected Integer confirming(CDODataInput in) throws IOException
  {
    if (name != null)
    {
      if (in.readBoolean())
      {
        readTag(in, name);
        return 1;
      }

      return 0;
    }

    int count = in.readXInt();
    for (int i = 0; i < count; i++)
    {
      String name = in.readString();
      readTag(in, name);
    }

    return count;
  }

  private void readTag(CDODataInput in, String name) throws IOException
  {
    CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    handler.accept(new BranchInfo(name, branchPoint.getBranch().getID(), branchPoint.getTimeStamp()));
  }
}
