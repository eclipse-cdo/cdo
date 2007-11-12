/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.protocol.IMembership;
import org.eclipse.net4j.internal.buddies.Self;
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
   * @author Eike Stepper
   */
  private class SelfNode extends AbstractContainerNode
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

    public Self getElement()
    {
      return self;
    }

    @Override
    protected Node addChild(Collection<Node> children, Object element)
    {
      return super.addChild(children, ((IMembership)element).getCollaboration());
    }
  }
}
