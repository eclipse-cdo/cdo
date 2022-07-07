/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class ValueFormatter implements Comparable<ValueFormatter>
{
  public static final int DEFAULT_PRIORITY = 100;

  private final String id;

  private final String label;

  public ValueFormatter(String id, String label)
  {
    this.id = id;
    this.label = label;
  }

  public int getPriority()
  {
    return DEFAULT_PRIORITY;
  }

  public final String getId()
  {
    return id;
  }

  public final String getLabel()
  {
    return label;
  }

  public abstract boolean canHandle(Object value);

  public abstract String formatValue(Object value) throws Exception;

  @Override
  public final int compareTo(ValueFormatter o)
  {
    return Integer.compare(getPriority(), o.getPriority());
  }

  @Override
  public final String toString()
  {
    return getLabel();
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.valueFormatters";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract ValueFormatter create(String description) throws ProductCreationException;
  }
}
