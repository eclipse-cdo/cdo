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
package org.eclipse.internal.net4j.util.product;

import org.eclipse.net4j.util.product.IProduct;
import org.eclipse.net4j.util.product.IProductFactory;
import org.eclipse.net4j.util.product.IProductGroup;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public class ProductGroup<PRODUCT extends IProduct> implements IProductGroup<PRODUCT>
{
  private String id;

  private IRegistry<String, IProductFactory<PRODUCT>> factoryRegistry = new HashMapRegistry();

  public ProductGroup(String id)
  {
    this.id = id;
  }

  public String getID()
  {
    return id;
  }

  public IRegistry<String, IProductFactory<PRODUCT>> getFactoryRegistry()
  {
    return factoryRegistry;
  }

  public void addFactory(IProductFactory<PRODUCT> factory)
  {
    factoryRegistry.put(factory.getType(), factory);
  }

  public void removeFactory(IProductFactory<PRODUCT> factory)
  {
    factoryRegistry.remove(factory.getType());
  }
}
