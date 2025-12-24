/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class LMCheckoutProperties extends AbstractLMProperties<CDOCheckout>
{
  public static final IProperties<CDOCheckout> INSTANCE = new LMCheckoutProperties();

  public static final String CATEGORY_CHECKOUT = "Checkout"; //$NON-NLS-1$

  private LMCheckoutProperties()
  {
    super(CDOCheckout.class);
  }

  @Override
  protected IAssemblyDescriptor getAssemblyDescriptor(CDOCheckout checkout)
  {
    return IAssemblyManager.INSTANCE.getDescriptor(checkout);
  }

  @Override
  protected void initProperties()
  {
    add(new Property<CDOCheckout>("isModule")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return getAssemblyDescriptor(checkout) != null;
      }
    });

    super.initProperties();
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
