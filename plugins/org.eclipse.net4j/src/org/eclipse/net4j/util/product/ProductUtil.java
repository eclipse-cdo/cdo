/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.product;

import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 */
public final class ProductUtil
{
  private ProductUtil()
  {
  }

  public static String getProductGroupID(Object product)
  {
    if (product instanceof IProduct)
    {
      return ((IProduct)product).getProductGroupID();
    }

    return null;
  }

  public static String getFactoryType(Object product)
  {
    if (product instanceof IProduct)
    {
      return ((IProduct)product).getFactoryType();
    }

    return null;
  }

  public static String getDescription(Object product)
  {
    if (product instanceof IProduct)
    {
      return ((IProduct)product).getDescription();
    }

    return null;
  }

  public static IProduct create(IRegistry<String, IProductGroup> productGroupRegistry, String productGroupID,
      String factoryType, String description)
  {
    IProductGroup productGroup = productGroupRegistry.get(productGroupID);
    if (productGroup == null)
    {
      return null;
    }

    IRegistry<String, IFactory> factoryRegistry = productGroup.getFactoryRegistry();
    IFactory factory = factoryRegistry.get(factoryType);
    if (factory == null)
    {
      return null;
    }

    IProduct product = factory.create(description);
    return product;
  }
}
