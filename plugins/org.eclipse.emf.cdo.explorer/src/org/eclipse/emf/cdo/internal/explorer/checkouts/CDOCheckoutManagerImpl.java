/*
 * Copyright (c) 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.internal.explorer.AbstractManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.Event;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutManagerImpl extends AbstractManager<CDOCheckout> implements CDOCheckoutManager
{
  private static final String PROPERTIES_FILE = "checkout.properties";

  private final Map<CDOView, CDOCheckout> viewMap = new ConcurrentHashMap<>();

  public CDOCheckoutManagerImpl(File folder)
  {
    super(CDOCheckout.class, folder);
  }

  @Override
  public String getPropertiesFileName()
  {
    return PROPERTIES_FILE;
  }

  @Override
  public CDOCheckout getCheckout(String id)
  {
    return getElement(id);
  }

  @Override
  public CDOCheckout getCheckout(CDOView view)
  {
    return viewMap.get(view);
  }

  @Override
  public CDOCheckout getCheckoutByLabel(String label)
  {
    return getElementByLabel(label);
  }

  @Override
  public CDOCheckout[] getCheckouts()
  {
    return getElements();
  }

  @Override
  public CDOCheckout addCheckout(Properties properties)
  {
    return newElement(properties);
  }

  public void fireCheckoutOpenEvent(CDOCheckout checkout, CDOView view, CDOCheckout.State oldState, CDOCheckout.State newState)
  {
    if (view != null)
    {
      switch (newState)
      {
      case Open:
        viewMap.put(view, checkout);
        break;

      case Closed:
        viewMap.remove(view);
        break;
      }
    }

    fireEvent(new CheckoutStateEventImpl(this, checkout, oldState, newState));
  }

  public void fireCheckoutInitializeEvent(CDOCheckout checkout)
  {
    fireEvent(new CheckoutInitializeEventImpl(this, checkout));
  }

  @Override
  protected CDOCheckout createElement(String type)
  {
    if (CDOCheckout.TYPE_ONLINE_TRANSACTIONAL.equals(type))
    {
      return new OnlineCDOCheckout();
    }

    if (CDOCheckout.TYPE_ONLINE_HISTORICAL.equals(type))
    {
      return new OnlineCDOCheckout(true);
    }

    if (CDOCheckout.TYPE_OFFLINE.equals(type))
    {
      return new OfflineCDOCheckout();
    }

    throw new IllegalArgumentException("Unknown type: " + type);
  }

  @Override
  protected CDOCheckout[] sortElements(CDOCheckout[] array)
  {
    CDOCheckout[] sorted = super.sortElements(array);
    Arrays.sort(sorted);
    return sorted;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (CDOCheckout checkout : getCheckouts())
    {
      checkout.close();
    }

    super.doDeactivate();
  }

  static
  {
    // Make sure all object types are registered.
    ObjectType.values();
  }

  /**
   * @author Eike Stepper
   */
  private static abstract class CheckoutEventImpl extends Event implements CheckoutEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOCheckout checkout;

    public CheckoutEventImpl(CDOCheckoutManager checkoutManager, CDOCheckout checkout)
    {
      super(checkoutManager);
      this.checkout = checkout;
    }

    @Override
    public CDOCheckoutManager getSource()
    {
      return (CDOCheckoutManager)super.getSource();
    }

    @Override
    public CDOCheckout getCheckout()
    {
      return checkout;
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return String.valueOf(checkout);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CheckoutStateEventImpl extends CheckoutEventImpl implements CheckoutStateEvent
  {
    private static final long serialVersionUID = 1L;

    private final CDOCheckout.State oldState;

    private final CDOCheckout.State newState;

    public CheckoutStateEventImpl(CDOCheckoutManager checkoutManager, CDOCheckout checkout, CDOCheckout.State oldState, CDOCheckout.State newState)
    {
      super(checkoutManager, checkout);
      this.oldState = oldState;
      this.newState = newState;
    }

    @Override
    public CDOCheckout.State getOldState()
    {
      return oldState;
    }

    @Override
    public CDOCheckout.State getNewState()
    {
      return newState;
    }

    @Override
    protected String formatEventName()
    {
      return CheckoutStateEvent.class.getSimpleName();
    }

    @Override
    protected String formatAdditionalParameters()
    {
      return super.formatAdditionalParameters() + ", " + oldState + " --> " + newState;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CheckoutInitializeEventImpl extends CheckoutEventImpl implements CheckoutInitializeEvent
  {
    private static final long serialVersionUID = 1L;

    public CheckoutInitializeEventImpl(CDOCheckoutManager checkoutManager, CDOCheckout checkout)
    {
      super(checkoutManager, checkout);
    }

    @Override
    protected String formatEventName()
    {
      return CheckoutInitializeEvent.class.getSimpleName();
    }
  }
}
