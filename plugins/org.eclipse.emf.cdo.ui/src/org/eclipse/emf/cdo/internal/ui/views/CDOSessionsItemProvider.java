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
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.container.CDOContainerAdapter;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.net4j.util.registry.IRegistryEvent;
import org.eclipse.net4j.util.registry.IRegistryListener;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class CDOSessionsItemProvider extends ItemProvider<CDOContainerAdapter> implements IRegistryListener
{
  public CDOSessionsItemProvider()
  {
  }

  public Object getParent(Object child)
  {
    if (child instanceof CDOSession)
    {
      return getInput();
    }

    return null;
  }

  public Object[] getChildren(Object parent)
  {
    if (parent == getInput())
    {
      Collection values = getInput().getSessionRegistry().values();
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
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      return MessageFormat.format("[{0}] {1}@{2}", session.getSessionID(), session.getRepository().getName(), session
          .getChannel().getConnector().getDescription());
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      return SharedIcons.OBJ_CHANNEL.createImage();
    }

    return null;
  }

  @Override
  protected void connectInput(CDOContainerAdapter input)
  {
    input.getSessionRegistry().addRegistryListener(this);
  }

  @Override
  protected void disconnectInput(CDOContainerAdapter input)
  {
    input.getSessionRegistry().removeRegistryListener(this);
  }
}