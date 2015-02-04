/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.internal.explorer.AbstractManager;

import org.eclipse.net4j.util.event.Event;

import java.io.File;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutManagerImpl extends AbstractManager<CDOCheckout> implements CDOCheckoutManager
{
  public CDOCheckoutManagerImpl(File folder)
  {
    super(CDOCheckout.class, folder);
  }

  public CDOCheckout getCheckout(String id)
  {
    return getElement(id);
  }

  public CDOCheckout[] getCheckouts()
  {
    return getElements();
  }

  public CDOCheckout addCheckout(Properties properties)
  {
    return newElement(properties);
  }

  public void fireCheckoutOpenEvent(CDOCheckout checkout, boolean open)
  {
    fireEvent(new CheckoutOpenEventImpl(this, checkout, open));
  }

  @Override
  protected CDOCheckout createElement(String type)
  {
    if ("online".equals(type))
    {
      return new OnlineCDOCheckout();
    }

    throw new IllegalArgumentException("Unknown type: " + type);
  }

  static
  {
    // Make sure all object types are registered.
    ObjectType.values();
  }

  /**
   * @author Eike Stepper
   */
  private static final class CheckoutOpenEventImpl extends Event implements CheckoutOpenEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOCheckout checkout;

    private final boolean open;

    public CheckoutOpenEventImpl(CDOCheckoutManager repositoryManager, CDOCheckout checkout, boolean open)
    {
      super(repositoryManager);
      this.checkout = checkout;
      this.open = open;
    }

    @Override
    public CDOCheckoutManager getSource()
    {
      return (CDOCheckoutManager)super.getSource();
    }

    public CDOCheckout getCheckout()
    {
      return checkout;
    }

    public boolean isOpen()
    {
      return open;
    }
  }
}
