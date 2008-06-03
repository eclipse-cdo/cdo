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

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

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

    CDOIDObjectFactory factory = getStore().getCDOIDObjectFactory();
    ids = new CDOID[size];
    for (int i = 0; i < ids.length; i++)
    {
      ids[i] = CDOIDUtil.read(in, factory);
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
      CDOClassRef classRef = getSession().getClassRef(id);
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.format("Wrinting type: {0}", classRef);
      }

      CDOModelUtil.writeClassRef(out, classRef);
    }
  }
}
