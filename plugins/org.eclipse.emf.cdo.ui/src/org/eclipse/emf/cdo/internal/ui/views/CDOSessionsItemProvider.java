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

import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class CDOSessionsItemProvider extends ItemProvider<CDOContainerAdapter> implements IRegistryListener, IListener
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

    if (child instanceof CDOAdapter)
    {
      CDOAdapter adapter = (CDOAdapter)child;
      return adapter.getSession();
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

    if (parent instanceof CDOSession)
    {
      CDOSession session = (CDOSession)parent;
      return session.getAdapters();
    }

    return NO_CHILDREN;
  }

  public void notifyRegistryEvent(IRegistryEvent event)
  {
    IRegistryDelta[] deltas = event.getDeltas();
    for (IRegistryDelta delta : deltas)
    {
      Object element = delta.getElement();
      if (element instanceof CDOSession)
      {
        CDOSession session = (CDOSession)element;
        switch (delta.getKind())
        {
        case REGISTERED:
          session.addListener(this);
          break;
        case DEREGISTERED:
          session.removeListener(this);
          break;
        }
      }
    }

    refreshViewer(false);
  }

  public void notifyEvent(IEvent event)
  {
    refreshViewer(false);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      String description = CDOContainerAdapterImpl.getDescription(session);
      return MessageFormat.format("[{0}] {1}", session.getSessionID(), description);
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_SESSION);
    }

    if (obj instanceof CDOAdapter)
    {
      CDOAdapter adapter = (CDOAdapter)obj;
      if (adapter.isHistorical())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }

      if (adapter.isReadOnly())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_READONLY);
      }

      return SharedIcons.getImage(SharedIcons.OBJ_EDITOR);
    }

    return null;
  }

  @Override
  protected void connectInput(CDOContainerAdapter input)
  {
    IRegistry<String, CDOSession> registry = input.getSessionRegistry();
    registry.addRegistryListener(this);
    for (CDOSession session : registry.values())
    {
      session.addListener(this);
    }
  }

  @Override
  protected void disconnectInput(CDOContainerAdapter input)
  {
    input.getSessionRegistry().removeRegistryListener(this);
  }
}