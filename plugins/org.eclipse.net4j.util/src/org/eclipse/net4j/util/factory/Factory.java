/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import java.text.MessageFormat;

/**
 * A default implementation of a {@link IFactory factory}.
 *
 * @author Eike Stepper
 */
public abstract class Factory implements IFactory, IFactoryKeyAware
{
  /**
   * @since 3.23
   */
  public static final String NO_DESCRIPTION = null;

  private IFactoryKey key;

  /**
   * @since 3.23
   */
  public Factory()
  {
    this((IFactoryKey)null);
  }

  public Factory(FactoryKey key)
  {
    this((IFactoryKey)key);
  }

  /**
   * @since 3.23
   */
  public Factory(IFactoryKey key)
  {
    this.key = key;
  }

  public Factory(String productGroup, String type)
  {
    this(new FactoryKey(productGroup, type));
  }

  /**
   * @since 3.16
   */
  public Factory(String productGroup)
  {
    this(productGroup, null);
  }

  /**
   * @since 3.23
   */
  public IFactoryKey getFactoryKey()
  {
    return key;
  }

  @Override
  public void setFactoryKey(IFactoryKey factoryKey)
  {
    key = factoryKey;
  }

  @Override
  public FactoryKey getKey()
  {
    if (key instanceof FactoryKey)
    {
      return (FactoryKey)key;
    }

    return new FactoryKey(key.getProductGroup(), key.getType());
  }

  public String getProductGroup()
  {
    return key.getProductGroup();
  }

  public String getType()
  {
    return key.getType();
  }

  @Override
  public String getDescriptionFor(Object product)
  {
    if (product instanceof ProductDescriptionProvider)
    {
      return ((ProductDescriptionProvider)product).getDescription();
    }

    return null;
  }

  /**
   * @since 3.23
   */
  protected final ProductCreationException productCreationException(String description, Throwable cause)
  {
    return new ProductCreationException(productCreationExceptionMessage(description), cause);
  }

  /**
   * @since 3.23
   */
  protected final ProductCreationException productCreationException(String description)
  {
    return new ProductCreationException(productCreationExceptionMessage(description));
  }

  private String productCreationExceptionMessage(String description)
  {
    return "Could not create product " + getKey() + " with " + description;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Factory[{0}, {1}]", getProductGroup(), getType()); //$NON-NLS-1$
  }
}
