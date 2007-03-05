/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.jvm.JVMAcceptor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.Acceptor;
import org.eclipse.internal.net4j.transport.DescriptionUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class JVMAcceptorImpl extends Acceptor implements JVMAcceptor
{
  @SuppressWarnings("unused")
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, JVMAcceptorImpl.class);

  private String name;

  public JVMAcceptorImpl()
  {
  }

  public String getName()
  {
    return name;
  }

  public ServerJVMConnectorImpl handleAccept(ClientJVMConnectorImpl client)
  {
    ServerJVMConnectorImpl connector = new ServerJVMConnectorImpl(client);
    connector.setReceiveExecutor(getReceiveExecutor());
    connector.setProtocolFactoryRegistry(getProtocolFactoryRegistry());
    connector.setBufferProvider(getBufferProvider());
    return connector;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("JVMAcceptor[{0}]", getDescription()); //$NON-NLS-1$ 
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getDescription() == null)
    {
      throw new IllegalStateException("getDescription() == null"); //$NON-NLS-1$
    }
    else
    {
      name = DescriptionUtil.getElement(getDescription(), 1);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    JVMAcceptorManagerImpl.INSTANCE.registerAcceptor(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    JVMAcceptorManagerImpl.INSTANCE.deregisterAcceptor(this);
    super.doDeactivate();
  }
}
