/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.common.util.Support;

import org.eclipse.core.expressions.PropertyTester;

/**
 * @author Eike Stepper
 */
public class SupportPropertyTester extends PropertyTester
{
  public SupportPropertyTester()
  {
  }

  @Override
  public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
  {
    if (expectedValue == null)
    {
      expectedValue = Boolean.TRUE;
    }

    if ("history".equals(property))
    {
      return expectedValue.equals(Support.UI_HISTORY.isAvailable());
    }

    if ("compare".equals(property))
    {
      return expectedValue.equals(Support.UI_COMPARE.isAvailable());
    }

    return false;
  }
}
