/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class RandomizerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.randomizers"; //$NON-NLS-1$

  public static final String TYPE = "default"; //$NON-NLS-1$

  public RandomizerFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  @Override
  public Randomizer create(String description) throws ProductCreationException
  {
    Randomizer randomizer = new Randomizer();
    if (description != null)
    {
      randomizer.setAlgorithmName(description);
    }

    return randomizer;
  }
}
