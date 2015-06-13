/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;

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
    public static final String NAMESPACE = "org.eclipse.emf.cdo.explorer.object";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
