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
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.internal.ui.bundle.SharedIcons;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryEvent;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class FactoriesItemProvider extends ItemProvider<Container> implements IRegistryListener
{
  private IRegistry[] registries = new IRegistry[3];

  public FactoriesItemProvider()
  {
  }

  public Object getParent(Object child)
  {
    if (child instanceof IRegistry)
    {
      return getInput();
    }

    if (child instanceof IAcceptorFactory)
    {
      return registries[1];
    }

    if (child instanceof IConnectorFactory)
    {
      return registries[2];
    }

    if (child instanceof IProtocolFactory)
    {
      return registries[3];
    }

    return null;
  }

  public Object[] getChildren(Object parent)
  {
    if (parent == getInput())
    {
      return registries;
    }

    if (parent instanceof IRegistry)
    {
      Collection values = ((IRegistry)parent).values();
      return values.toArray(new Object[values.size()]);
    }

    return NO_CHILDREN;
  }

  public void notifyRegistryEvent(IRegistryEvent event)
  {
    refreshViewer(false);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj == getInput().getAcceptorFactoryRegistry())
    {
      return "Acceptor Factories";
    }

    if (obj == getInput().getConnectorFactoryRegistry())
    {
      return "Connector Factories";
    }

    if (obj == getInput().getProtocolFactoryRegistry())
    {
      return "Protocol Factories";
    }

    if (obj instanceof IAcceptorFactory)
    {
      IAcceptorFactory factory = (IAcceptorFactory)obj;
      return MessageFormat.format("{0} = {1}", factory.getType(), factory.getClass().getName());
    }

    if (obj instanceof IConnectorFactory)
    {
      IConnectorFactory factory = (IConnectorFactory)obj;
      return MessageFormat.format("{0} = {1}", factory.getType(), factory.getClass().getName());
    }

    if (obj instanceof IProtocolFactory)
    {
      IProtocolFactory factory = (IProtocolFactory)obj;
      return MessageFormat.format("{0} {1} = {2}", factory.getProtocolID(), factory.getLocations(), factory.getClass()
          .getName());
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IRegistry)
    {
      return SharedIcons.OBJ_FOLDER.createImage();
    }

    return SharedIcons.OBJ_FACTORY.createImage();
  }

  @Override
  protected void connectInput(Container input)
  {
    registries[0] = input.getAcceptorFactoryRegistry();
    registries[0].addRegistryListener(this);
    registries[1] = input.getConnectorFactoryRegistry();
    registries[1].addRegistryListener(this);
    registries[2] = input.getProtocolFactoryRegistry();
    registries[2].addRegistryListener(this);
  }

  @Override
  protected void disconnectInput(Container input)
  {
    for (int i = 0; i < registries.length; i++)
    {
      registries[i].removeRegistryListener(this);
      registries[i] = null;
    }
  }
}