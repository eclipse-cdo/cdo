/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.net4j.buddies.IBuddyCollaboration;
import org.eclipse.net4j.buddies.common.IBuddy;
import org.eclipse.net4j.buddies.internal.ui.dnd.BuddiesTransfer;
import org.eclipse.net4j.util.ui.dnd.DNDDropAdapter;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Eike Stepper
 */
public class BuddiesDropAdapter extends DNDDropAdapter<IBuddy[]>
{
  /**
   * @since 3.0
   */
  public static final Transfer[] TRANSFERS = new Transfer[] { BuddiesTransfer.INSTANCE };

  protected BuddiesDropAdapter(StructuredViewer viewer)
  {
    super(TRANSFERS, viewer);
    setExpandEnabled(false);
  }

  @Override
  protected boolean validateTarget(Object target, int operation)
  {
    return target instanceof IBuddyCollaboration;
  }

  @Override
  protected boolean performDrop(IBuddy[] buddies, Object target)
  {
    IBuddyCollaboration collaboration = (IBuddyCollaboration)target;
    collaboration.invite(buddies);
    return true;
  }

  public static void support(StructuredViewer viewer)
  {
    viewer.addDropSupport(DND.DROP_MOVE, TRANSFERS, new BuddiesDropAdapter(viewer));
  }
}
