/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

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
