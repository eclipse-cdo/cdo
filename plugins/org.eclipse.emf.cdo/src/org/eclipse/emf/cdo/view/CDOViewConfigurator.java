/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 4.14
 */
@FunctionalInterface
public interface CDOViewConfigurator
{
  public void configureView(CDOView view);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.viewConfigurators";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    @Override
    public abstract CDOViewConfigurator create(String description) throws ProductCreationException;
  }
}
