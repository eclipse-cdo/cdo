/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public class CheckoutDuplicateHandler extends AbstractCheckoutHandler
{
  public CheckoutDuplicateHandler()
  {
    super(null, true);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor monitor) throws Exception
  {
    for (CDOCheckout checkout : elements)
    {
      ((CDOCheckoutImpl)checkout).duplicate();
    }
  }
}
