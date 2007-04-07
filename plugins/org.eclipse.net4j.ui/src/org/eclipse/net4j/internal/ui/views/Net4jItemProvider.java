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
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.internal.ui.ContainerItemProvider;
import org.eclipse.net4j.internal.ui.IElementFilter;
import org.eclipse.net4j.internal.ui.bundle.SharedIcons;
import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IConnector;

import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class Net4jItemProvider extends ContainerItemProvider
{
  public Net4jItemProvider()
  {
  }

  public Net4jItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  // @Override
  // public String getText(Object obj)
  // {
  // if (obj instanceof IChannel)
  // {
  // IChannel channel = (IChannel)obj;
  // return MessageFormat.format("[{0}] {1}", channel.getChannelIndex(),
  // channel.getReceiveHandler());
  // }
  //
  // return super.getText(obj);
  // }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IAcceptor)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_ACCEPTOR);
    }

    if (obj instanceof IConnector)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_CONNECTOR);
    }

    if (obj instanceof IChannel)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_CHANNEL);
    }

    return super.getImage(obj);
  }
}
