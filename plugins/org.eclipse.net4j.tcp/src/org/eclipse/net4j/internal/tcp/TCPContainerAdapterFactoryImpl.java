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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.TCPContainerAdapter;
import org.eclipse.net4j.transport.container.Container;
import org.eclipse.net4j.transport.container.ContainerAdapter;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;

public final class TCPContainerAdapterFactoryImpl implements ContainerAdapterFactory
{
  public TCPContainerAdapterFactoryImpl()
  {
  }

  public String getType()
  {
    return TCPContainerAdapter.TYPE;
  }

  public ContainerAdapter createAdapter(Container container)
  {
    return new TCPContainerAdapterImpl(container);
  }
}