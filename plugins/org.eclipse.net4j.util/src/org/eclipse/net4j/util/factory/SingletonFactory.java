/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

  public SingletonFactory(Object product)
  {
    this.product = product;
  }

  public SingletonFactory(IFactoryKey key, Object product)
  {
    super(key);
    this.product = product;
  }

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
