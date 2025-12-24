/*
 * Copyright (c) 2015, 2019, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
   * An {@link IEvent event} fired from a {@link CDOCheckoutManager checkout manager}.
   *
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
   * A {@link CheckoutEvent checkout event} fired when the {@link CDOCheckout#getState() state} of a checkout has changed.
   *
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
   * A {@link CheckoutEvent checkout event} fired when a {@link CDOCheckout checkout} is in the process of being opened.
   *
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @since 4.11
   */
  public interface CheckoutInitializeEvent extends CheckoutEvent
  {
  }
}
