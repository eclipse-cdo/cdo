/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.properties.IPropertyProvider;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.ui.IActionFilter;

/**
 * @author Eike Stepper
 * @since 3.4
 */
public class DefaultActionFilter<RECEIVER> implements IActionFilter
{
  private IPropertyProvider<RECEIVER> provider;

  public DefaultActionFilter(IPropertyProvider<RECEIVER> provider)
  {
    this.provider = provider;
  }

  @Override
  public boolean testAttribute(Object target, String name, String value)
  {
    for (Property<RECEIVER> property : provider.getProperties())
    {
      if (property.getName().equals(name))
      {
        @SuppressWarnings("unchecked")
        RECEIVER receiver = (RECEIVER)target;

        Object actualValue = property.getValue(receiver);
        return ObjectUtil.equals(value, actualValue);
      }
    }

    return false;
  }
}
