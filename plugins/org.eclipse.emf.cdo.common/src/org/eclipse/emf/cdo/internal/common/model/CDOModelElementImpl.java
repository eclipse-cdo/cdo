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
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDOModelElement;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOModelElementImpl implements InternalCDOModelElement
{
  private static final ContextTracer MODEL = new ContextTracer(OM.DEBUG_MODEL, CDOModelElementImpl.class);

  private String name;

  private Object clientInfo;

  private Object serverInfo;

  private boolean initialized;

  protected CDOModelElementImpl(String name)
  {
    this.name = name;
  }

  protected CDOModelElementImpl()
  {
  }

  public void read(ExtendedDataInput in) throws IOException
  {
    name = in.readString();
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeString(name);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Object getClientInfo()
  {
    return clientInfo;
  }

  public void setClientInfo(Object clientInfo)
  {
    if (MODEL.isEnabled())
    {
      MODEL.format("Setting client info: {0} --> {1}", this, clientInfo);
    }

    this.clientInfo = clientInfo;
  }

  public Object getServerInfo()
  {
    return serverInfo;
  }

  public void setServerInfo(Object serverInfo)
  {
    if (MODEL.isEnabled())
    {
      MODEL.format("Setting server info: {0} --> {1}", this, serverInfo);
    }

    this.serverInfo = serverInfo;
  }

  public boolean isInitialized()
  {
    return initialized;
  }

  public final void initialize()
  {
    if (initialized)
    {
      throw new ImplementationError("Duplicate initialization");
    }

    initialized = true;
    onInitialize();
  }

  protected abstract void onInitialize();
}
