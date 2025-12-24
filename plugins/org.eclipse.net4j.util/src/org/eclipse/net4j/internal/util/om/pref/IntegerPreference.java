/*
 * Copyright (c) 2007, 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om.pref;

/**
 * @author Eike Stepper
 */
public final class IntegerPreference extends Preference<Integer>
{
  public IntegerPreference(Preferences preferences, String name, Integer defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return Integer.toString(getValue());
  }

  @Override
  protected Integer convert(String value)
  {
    return Integer.parseInt(value);
  }

  @Override
  public Type getType()
  {
    return Type.INTEGER;
  }
}
