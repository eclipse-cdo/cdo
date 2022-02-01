/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ui.shared;

import org.eclipse.net4j.ui.internal.shared.messages.Messages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Eike Stepper
 * @since 4.7
 */
public class RefreshAction extends Action
{
  private Viewer viewer;

  public RefreshAction(Viewer viewer)
  {
    super(Messages.getString("RefreshAction_name")); //$NON-NLS-1$
    setToolTipText(Messages.getString("RefreshAction_tooltip")); //$NON-NLS-1$
    setImageDescriptor(SharedIcons.getDescriptor(SharedIcons.ETOOL_REFRESH));
    this.viewer = viewer;
  }

  public Viewer getViewer()
  {
    return viewer;
  }

  public void setViewer(Viewer viewer)
  {
    this.viewer = viewer;
  }

  @Override
  public void run()
  {
    viewer.refresh();
  }
}
