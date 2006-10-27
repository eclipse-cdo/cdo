/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.protocol;


import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.om.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.emf.cdo.core.CDOSignals;
import org.eclipse.emf.cdo.server.Mapper;
import org.eclipse.emf.cdo.server.ResourceInfo;
import org.eclipse.emf.cdo.server.ResourceManager;
import org.eclipse.emf.cdo.server.internal.CDOServer;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class LoadResourceIndication extends IndicationWithResponse
{
  private static final ContextTracer TRACER = new ContextTracer(CDOServer.DEBUG_PROTOCOL,
      LoadResourceIndication.class);

  private Mapper mapper;

  private int rid;

  public LoadResourceIndication(Mapper mapper)
  {
    this.mapper = mapper;
  }

  @Override
  protected short getSignalID()
  {
    return CDOSignals.LOAD_RESOURCE;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    rid = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Loading rid " + rid);
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    ResourceManager resourceManager = mapper.getResourceManager();
    ResourceInfo resourceInfo = resourceManager.getResourceInfo(rid, mapper);
    mapper.transmitContent(out, resourceInfo);
  }
}
