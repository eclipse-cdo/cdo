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
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class MembershipItemProvider extends AbstractItemProvider
{
  public MembershipItemProvider()
  {
  }

  public MembershipItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  protected Node createMembershipNode(Node parent, IMembership membership)
  {
    return createContaineNode(parent, membership);
  }

  @Override
  protected String getText(IMembership membership)
  {
    return getText(membership.getBuddy()) + "(" + getText(membership.getCollaboration()) + ")";
  }

  @Override
  protected Image getImage(IMembership membership)
  {
    return null;
  }
}
