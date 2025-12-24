/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
    return getText(membership.getBuddy()) + "(" + getText(membership.getCollaboration()) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  protected Image getImage(IMembership membership)
  {
    return null;
  }
}
