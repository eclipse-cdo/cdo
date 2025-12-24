/*
 * Copyright (c) 2007-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class JVMClientConnector extends JVMConnector
{
  private JVMAcceptor acceptor;

  public JVMClientConnector()
  {
  }

  @Override
  public Location getLocation()
  {
    return Location.CLIENT;
  }

  public JVMAcceptor getAcceptor()
  {
    return acceptor;
  }

  @Override
  public String toString()
  {
    if (getUserID() == null)
    {
      return MessageFormat.format("JVMClientConnector[{0}]", getName()); //$NON-NLS-1$
    }

    return MessageFormat.format("JVMClientConnector[{1}@{0}]", getName(), getUserID()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    String name = getName();
    acceptor = JVMAcceptorManager.INSTANCE.getAcceptor(name);
    if (acceptor == null)
    {
      throw new IllegalStateException("JVM acceptor not found: " + name); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    JVMConnector peer = acceptor.handleAccept(this);
    setPeer(peer);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivateNoisy(getPeer());
    super.doDeactivate();
  }
}
