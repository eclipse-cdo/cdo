/*
 * Copyright (c) 2007, 2008, 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.common.ICollaboration;
import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.internal.buddies.Self;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.swt.graphics.Image;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class CollaborationsItemProvider extends AbstractItemProvider
{
  public CollaborationsItemProvider()
  {
  }

  public CollaborationsItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof Self)
    {
      return new SelfNode((Self)element);
    }

    if (element instanceof IMembership)
    {
      IMembership membership = (IMembership)element;
      if (membership.getBuddy() instanceof Self)
      {
        return null;
      }
    }

    return super.createNode(parent, element);
  }

  @Override
  protected Node createMembershipNode(Node parent, IMembership membership)
  {
    return createLeafNode(parent, membership);
  }

  @Override
  protected String getText(IMembership membership)
  {
    return getText(membership.getBuddy());
  }

  @Override
  protected Image getImage(IMembership membership)
  {
    return getImage(membership.getBuddy());
  }

  /**
   * @since 4.0
   */
  @Override
  public void refreshElement(Object element, boolean updateLabels)
  {
    super.refreshElement(null, updateLabels);
  }

  /**
   * @author Eike Stepper
   */
  private class SelfNode extends AbstractContainerNode implements IListener
  {
    private Self self;

    public SelfNode(Self self)
    {
      super(null);
      this.self = self;
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        self.removeListener(containerListener);
        super.dispose();
      }
    }

    @Override
    public Self getElement()
    {
      return self;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      IMembership membership = (IMembership)event.getSource();
      if (event instanceof ILifecycleEvent)
      {
        ILifecycleEvent e = (ILifecycleEvent)event;
        if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
        {
          membership.removeListener(this);
          ICollaboration collaboration = membership.getCollaboration();
          Node node = getNode(collaboration);
          if (node != null)
          {
            node.dispose();
          }
        }
      }
    }

    @Override
    protected Node addChild(Collection<Node> children, Object element)
    {
      IMembership membership = (IMembership)element;
      membership.addListener(this);
      return super.addChild(children, membership.getCollaboration());
    }
  }
}
