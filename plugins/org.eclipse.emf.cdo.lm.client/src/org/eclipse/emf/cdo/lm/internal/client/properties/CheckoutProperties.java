/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;

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

  public static final String CATEGORY_CHECKOUT = "Checkout"; //$NON-NLS-1$

  private CheckoutProperties()
  {
    super(CDOCheckout.class);

    add(new Property<CDOCheckout>("isModule")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(checkout);
        return descriptor != null;
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
    public static final String NAMESPACE = "org.eclipse.emf.cdo.lm.checkout";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
