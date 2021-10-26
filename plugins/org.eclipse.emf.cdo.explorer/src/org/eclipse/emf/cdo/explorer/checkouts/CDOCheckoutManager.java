/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;

import java.util.Properties;

/**
 * Manages a set of {@link CDOCheckout checkouts}.
 *
 * @author Eike Stepper
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOCheckoutManager extends CDOExplorerManager<CDOCheckout>
{
  public CDOCheckout getCheckout(String id);

  public CDOCheckout getCheckout(CDOView view);

  public CDOCheckout getCheckoutByLabel(String label);

  public CDOCheckout[] getCheckouts();

  public CDOCheckout addCheckout(Properties properties);

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @since 4.11
   */
  public interface CheckoutEvent extends IEvent
  {
    @Override
    public CDOCheckoutManager getSource();

    public CDOCheckout getCheckout();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface CheckoutStateEvent extends CheckoutEvent
  {
    public CDOCheckout.State getOldState();

    public CDOCheckout.State getNewState();
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @since 4.11
   */
  public interface CheckoutInitializeEvent extends CheckoutEvent
  {
  }
}
