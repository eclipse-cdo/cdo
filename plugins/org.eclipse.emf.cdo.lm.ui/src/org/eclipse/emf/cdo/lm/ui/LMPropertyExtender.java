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
package org.eclipse.emf.cdo.lm.ui;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.internal.client.properties.LMCheckoutProperties;

import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.properties.Property;
import org.eclipse.net4j.util.ui.PropertyExtender;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class LMPropertyExtender implements PropertyExtender
{
  public LMPropertyExtender()
  {
  }

  @Override
  public void forEachExtendedProperty(Object selectedObject, Consumer<Property<?>> consumer)
  {
    if (selectedObject instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)selectedObject;

      IAssemblyDescriptor descriptor = IAssemblyManager.INSTANCE.getDescriptor(checkout);
      if (descriptor != null)
      {
        LMCheckoutProperties.INSTANCE.getProperties().forEach(consumer);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends PropertyExtender.Factory
  {
    public static final String TYPE = "lm";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public PropertyExtender create(String description) throws ProductCreationException
    {
      return new LMPropertyExtender();
    }
  }
}
