/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.properties.Property;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.21
 */
public interface PropertyExtender
{
  public void forEachExtendedProperty(Object selectedObject, Consumer<Property<?>> consumer);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.propertyExtenders"; //$NON-NLS-1$

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract PropertyExtender create(String description) throws ProductCreationException;
  }
}
