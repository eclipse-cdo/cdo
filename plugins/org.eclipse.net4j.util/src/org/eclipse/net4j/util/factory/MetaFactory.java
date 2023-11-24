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
public abstract class MetaFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.metaFactories"; //$NON-NLS-1$

  public MetaFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  @Override
  public abstract IFactory[] create(String description) throws ProductCreationException;
}
