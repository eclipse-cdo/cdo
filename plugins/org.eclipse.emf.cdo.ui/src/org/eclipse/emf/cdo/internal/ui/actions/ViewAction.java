/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;

import org.eclipse.emf.internal.cdo.CDOViewImpl;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Eike Stepper
 */
public abstract class ViewAction extends LongRunningAction
{
  protected static int lastResourceNumber = 0;

  private CDOView view;

  public ViewAction(IWorkbenchPage page, String text, String toolTipText, ImageDescriptor image, CDOView view)
  {
    super(page, text, toolTipText, image);
    this.view = view;
  }

  public CDOView getView()
  {
    return view;
  }

  public CDOTransaction getTransaction()
  {
    return ((CDOViewImpl)view).toTransaction();
  }
}
