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
import org.eclipse.net4j.util.registry.IRegistryEvent;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class AdaptersItemProvider extends ItemProvider<Container> implements IRegistryListener
{
  public AdaptersItemProvider()
  {
  }

  public Object getParent(Object child)
  {
    if (child instanceof ContainerAdapterFactory)
    {
      return getInput();
    }

    return null;
  }

  public Object[] getChildren(Object parent)
  {
    if (parent == getInput())
    {
      Collection values = getInput().getAdapterFactoryRegistry().values();
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
    ContainerAdapterFactory factory = (ContainerAdapterFactory)obj;
    return MessageFormat.format("{0} ({1})", factory.getType(), factory.getClass().getName());
  }

  @Override
  public Image getImage(Object obj)
  {
    return SharedIcons.OBJ_ADAPTER.createImage();
  }

  @Override
  protected void connectInput(Container input)
  {
    input.getAdapterFactoryRegistry().addRegistryListener(this);
  }

  @Override
  protected void disconnectInput(Container input)
  {
    input.getAdapterFactoryRegistry().removeRegistryListener(this);
  }
}