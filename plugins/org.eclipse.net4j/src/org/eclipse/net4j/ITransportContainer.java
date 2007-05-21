/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 0.8.0
 */
public interface ITransportContainer extends IManagedContainer
{
  public static final String EXECUTOR_SERVICE_GROUP = OM.BUNDLE_ID + ".executorServices";

  public static final String BUFFER_PROVIDER_GROUP = OM.BUNDLE_ID + ".bufferProviders";

  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  public short getBufferCapacity();

  public IBufferProvider getBufferProvider();

  public ExecutorService getExecutorService();

  public IAcceptor getAcceptor(String type, String description);

  public IConnector getConnector(String type, String description);
}
