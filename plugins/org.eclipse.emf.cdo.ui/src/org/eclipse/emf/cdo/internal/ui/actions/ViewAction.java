/*
 * Copyright (c) 2007-2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.actions.LongRunningAction;

import org.eclipse.emf.spi.cdo.InternalCDOView;

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
    return ((InternalCDOView)view).toTransaction();
  }
}
