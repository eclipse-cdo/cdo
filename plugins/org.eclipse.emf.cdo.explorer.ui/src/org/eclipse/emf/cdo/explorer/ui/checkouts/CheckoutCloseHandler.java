/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Eike Stepper
 */
public class CheckoutCloseHandler extends CheckoutHandler
{
  public CheckoutCloseHandler()
  {
    super(null, true);
  }

  @Override
  protected void doExecute(ExecutionEvent event, IProgressMonitor progressMonitor) throws Exception
  {
    for (CDOCheckout checkout : elements)
    {
      checkout.close();
    }
  }
}
