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

import org.eclipse.net4j.util.ui.handlers.AbstractBaseHandler;

import org.eclipse.jface.viewers.ISelection;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCheckoutHandler extends AbstractBaseHandler<CDOCheckout>
{
  private final Boolean open;

  public AbstractCheckoutHandler(Boolean multi, Boolean open)
  {
    super(CDOCheckout.class, multi);
    this.open = open;
  }

  @Override
  protected boolean updateSelection(ISelection selection)
  {
    boolean result = super.updateSelection(selection);

    if (result && open != null)
    {
      for (CDOCheckout checkout : elements)
      {
        if (open != checkout.isOpen())
        {
          return false;
        }
      }
    }

    return result;
  }
}
