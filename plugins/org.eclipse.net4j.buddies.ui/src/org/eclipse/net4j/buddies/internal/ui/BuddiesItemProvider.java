/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.buddies.common.IMembership;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class BuddiesItemProvider extends AbstractItemProvider
{
  public BuddiesItemProvider()
  {
  }

  public BuddiesItemProvider(IElementFilter rootElementFilter)
  {
    super(rootElementFilter);
  }

  @Override
  protected Node createMembershipNode(Node parent, IMembership membership)
  {
    return createLeafNode(parent, membership);
  }

  @Override
  protected String getText(IMembership membership)
  {
    return getText(membership.getCollaboration());
  }

  @Override
  protected Image getImage(IMembership membership)
  {
    return getImage(membership.getCollaboration());
  }
}
