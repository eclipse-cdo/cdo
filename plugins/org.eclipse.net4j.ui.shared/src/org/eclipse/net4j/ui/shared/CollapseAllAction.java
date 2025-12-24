/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui.shared;

import org.eclipse.net4j.ui.internal.shared.messages.Messages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public class CollapseAllAction extends Action
{
  private TreeViewer viewer;

  public CollapseAllAction(TreeViewer viewer)
  {
    super(Messages.getString("CollapseAllAction_name")); //$NON-NLS-1$
    setToolTipText(Messages.getString("CollapseAllAction_tooltip")); //$NON-NLS-1$
    setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_COLLAPSE_ALL));
    this.viewer = viewer;
  }

  public Viewer getViewer()
  {
    return viewer;
  }

  public void setViewer(TreeViewer viewer)
  {
    this.viewer = viewer;
  }

  @Override
  public void run()
  {
    viewer.collapseAll();
  }
}
