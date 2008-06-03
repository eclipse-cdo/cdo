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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.INegotiator;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public interface InternalAcceptor extends IAcceptor
{
  public ExecutorService getReceiveExecutor();

  public void setReceiveExecutor(ExecutorService receiveExecutor);

  public void setProtocolFactoryRegistry(IRegistry<IFactoryKey, IFactory> protocolFactoryRegistry);

  public void setProtocolPostProcessors(List<IElementProcessor> protocolPostProcessors);

  public IBufferProvider getBufferProvider();

  public void setBufferProvider(IBufferProvider bufferProvider);

  public INegotiator getNegotiator();

  public void setNegotiator(INegotiator negotiator);
}
