/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.protocol;

import org.eclipse.net4j.ILocationAware;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.security.IUserAware;

import java.util.concurrent.ExecutorService;

/**
 * A {@link #getType() typed} {@link IBufferHandler buffer handler} for a {@link #getChannel() channel}.
 *
 * @author Eike Stepper
 */
public interface IProtocol<INFRA_STRUCTURE> extends IUserAware, ILocationAware, IBufferHandler
{
  public String getType();

  public IChannel getChannel();

  public void setChannel(IChannel channel);

  public INFRA_STRUCTURE getInfraStructure();

  public void setInfraStructure(INFRA_STRUCTURE infraStructure);

  public IBufferProvider getBufferProvider();

  public ExecutorService getExecutorService();

  public void setExecutorService(ExecutorService executorService);
}
