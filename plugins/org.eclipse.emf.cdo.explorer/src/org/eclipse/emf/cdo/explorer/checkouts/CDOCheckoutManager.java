/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.util.event.IEvent;

import java.util.Properties;

/**
 * Manages a set of {@link CDOCheckout checkouts}.
 *
 * @author Eike Stepper
 * @since 4.4
 * @apiviz.composedOf {@link CDOCheckout}
 */
public interface CDOCheckoutManager extends CDOExplorerManager<CDOCheckout>
{
  public CDOCheckout getCheckout(String id);

  public CDOCheckout[] getCheckouts();

  public CDOCheckout addCheckout(Properties properties);

  /**
   * @author Eike Stepper
   */
  public interface CheckoutOpenEvent extends IEvent
  {
    public CDOCheckoutManager getSource();

    public CDOCheckout getCheckout();

    public boolean isOpen();
  }
}
