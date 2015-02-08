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
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.RootType;
import org.eclipse.emf.cdo.internal.explorer.AbstractElement;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutProperties extends Properties<CDOCheckout>
{
  public static final IProperties<CDOCheckout> INSTANCE = new CDOCheckoutProperties();

  private static final String CATEGORY_CHECKOUT = "Checkout"; //$NON-NLS-1$

  private CDOCheckoutProperties()
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

    add(new Property<CDOCheckout>("id", "ID", "The ID of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getID();
      }
    });

    add(new Property<CDOCheckout>("folder", "Folder", "The folder of this checkout", CATEGORY_CHECKOUT)
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return ((AbstractElement)checkout).getFolder();
      }
    });

    add(new Property<CDOCheckout>("rootType")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        return checkout.getRootType();
      }
    });

    add(new Property<CDOCheckout>("canContainResources")
    {
      @Override
      protected Object eval(CDOCheckout checkout)
      {
        RootType rootType = checkout.getRootType();
        if (rootType != null)
        {
          switch (rootType)
          {
          case Root:
          case Folder:
            return true;
          }
        }

        return false;
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
