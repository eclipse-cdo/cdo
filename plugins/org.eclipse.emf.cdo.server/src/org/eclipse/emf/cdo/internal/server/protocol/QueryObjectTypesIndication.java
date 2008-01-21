/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.internal.protocol.model.CDOClassRefImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOIDUtil;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class QueryObjectTypesIndication extends CDOReadIndication
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, QueryObjectTypesIndication.class);

  private CDOID[] ids;

  public QueryObjectTypesIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY_OBJECT_TYPES;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (PROTOCOL.isEnabled())
    {
      PROTOCOL.format("Reading {0} IDs", size);
    }

    ids = new CDOID[size];
    for (int i = 0; i < ids.length; i++)
    {
      ids[i] = CDOIDUtil.read(in);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Read ID: {0}", ids[i]);
      }
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    for (CDOID id : ids)
    {
      CDOClassRef classRef = getSession().getObjectTypeRef(id);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Wrinting type: {0}", classRef);
      }

      ((CDOClassRefImpl)classRef).write(out, null);
    }
  }
}
