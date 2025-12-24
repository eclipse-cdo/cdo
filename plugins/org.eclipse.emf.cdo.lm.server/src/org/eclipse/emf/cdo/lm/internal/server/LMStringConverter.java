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
package org.eclipse.emf.cdo.lm.internal.server;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.StringConverter;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class LMStringConverter implements StringConverter
{
  private final String key;

  private LMStringConverter(String key)
  {
    this.key = key;
  }

  @Override
  public String apply(Object value)
  {
    if (key != null && value instanceof IRepository)
    {
      IRepository repository = (IRepository)value;
      Object property = repository.properties().get(key);
      return IDENTITY.apply(property);
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public Factory()
    {
      super(StringConverter.PRODUCT_GROUP);
    }

    @Override
    public StringConverter create(String description) throws ProductCreationException
    {
      String type = getFactoryKey().getType();
      return new LMStringConverter(type);
    }
  }
}
