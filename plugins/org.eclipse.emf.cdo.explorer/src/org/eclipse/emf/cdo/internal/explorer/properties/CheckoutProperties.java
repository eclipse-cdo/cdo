/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.properties;

import org.eclipse.emf.cdo.explorer.CDOCheckout;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class CheckoutProperties extends Properties<CDOCheckout>
{
  public static final IProperties<CDOCheckout> INSTANCE = new CheckoutProperties();

  private static final String CATEGORY_CHECKOUT = "Checkout"; //$NON-NLS-1$

  private CheckoutProperties()
  {
    super(CDOCheckout.class);

    add(new Property<CDOCheckout>("state")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getState();
      }
    });

    add(new Property<CDOCheckout>("open", "Open", "Whether this checkout is open or not", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.isOpen();
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<CDOCheckout>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.explorer.checkout";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
