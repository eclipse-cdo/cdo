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

import org.eclipse.emf.cdo.common.branch.CDOBranchManager.CDOTagList;
import org.eclipse.emf.cdo.common.branch.CDOBranchTag;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadTagsIndication extends CDOServerReadIndication
{
  private String name;

  public LoadTagsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_TAGS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    name = in.readString();
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();

    if (name != null)
    {
      CDOBranchTag tag = branchManager.getTag(name);
      if (tag != null)
      {
        out.writeBoolean(true);
        out.writeCDOBranchPoint(tag);
      }
      else
      {
        out.writeBoolean(false);
      }
    }
    else
    {
      CDOTagList tagList = branchManager.getTagList();
      CDOBranchTag[] tags = tagList.getTags();

      int count = tags.length;
      out.writeXInt(count);

      for (int i = 0; i < count; i++)
      {
        CDOBranchTag tag = tags[i];
        out.writeString(tag.getName());
        out.writeCDOBranchPoint(tag);
      }
    }
  }
}
