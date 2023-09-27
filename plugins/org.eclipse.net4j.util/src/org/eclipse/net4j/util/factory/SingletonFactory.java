/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public class SingletonFactory extends Factory
{
  private final Object product;

  public SingletonFactory(FactoryKey key, Object product)
  {
    super(key);
    this.product = product;
  }

  public SingletonFactory(String productGroup, String type, Object product)
  {
    super(productGroup, type);
    this.product = product;
  }

  public SingletonFactory(String productGroup, Object product)
  {
    super(productGroup);
    this.product = product;
  }

  @Override
  public Object create(String description) throws ProductCreationException
  {
    return product;
  }
}
