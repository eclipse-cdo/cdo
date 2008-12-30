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
package org.eclipse.net4j.net4jdefs.util;

import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.ConnectorDef;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;

/**
 * @author Eike Stepper
 */
public class ConnectorDefBuilder
{
  protected BufferProviderDef bufferProviderDef;

  protected ExecutorServiceDef executorServiceDef;

  public ConnectorDefBuilder()
  {
    super();
  }

  public ConnectorDefBuilder bufferProvider(BufferProviderDef bufferProviderDef)
  {
    this.bufferProviderDef = bufferProviderDef;
    return this;
  }

  public ConnectorDefBuilder executorService(ExecutorServiceDef executorServiceDef)
  {
    this.executorServiceDef = executorServiceDef;
    return this;
  }

  public void validate()
  {
    CheckUtil.checkState(bufferProviderDef != null, "bufferProviderDef is not set!");
    CheckUtil.checkState(executorServiceDef != null, "executorServiceDef is not set!");
  }

  public void build(ConnectorDef connectorDef)
  {
    validate();

    connectorDef.setBufferProvider(bufferProviderDef);
    connectorDef.setExecutorService(executorServiceDef);
  }
}
