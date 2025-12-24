/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;

import org.eclipse.emf.internal.cdo.object.CDOElementTester;

import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutObjectProperties extends Properties<EObject>
{
  public static final IProperties<EObject> INSTANCE = new CDOCheckoutObjectProperties();

  public static final String NAMESPACE = "org.eclipse.emf.cdo.explorer.object";

  private CDOCheckoutObjectProperties()
  {
    super(EObject.class);

    add(new Property<EObject>("inCheckout")
    {
      @Override
      protected Object eval(EObject object)
      {
        return CDOExplorerUtil.getCheckout(object) != null;
      }
    });

    add(new Property<EObject>("stateCheckout")
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
        if (checkout == null)
        {
          return null;
        }

        return checkout.getState();
      }
    });

    add(new Property<EObject>("openCheckout")
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
        if (checkout == null)
        {
          return null;
        }

        return checkout.isOpen();
      }
    });

    add(new Property<EObject>("typeCheckout")
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
        if (checkout == null)
        {
          return null;
        }

        return checkout.getType();
      }
    });

    add(new Property<EObject>("readOnlyCheckout")
    {
      @Override
      protected Object eval(EObject object)
      {
        CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
        if (checkout == null)
        {
          return null;
        }

        return checkout.isReadOnly();
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
  public static final class Tester extends DefaultPropertyTester<EObject>
  {
    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ElementTester extends CDOElementTester
  {
    public ElementTester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
