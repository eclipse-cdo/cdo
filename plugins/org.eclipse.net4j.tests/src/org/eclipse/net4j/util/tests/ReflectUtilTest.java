/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public class ReflectUtilTest extends AbstractOMTest
{
  public void testMakeAccessible() throws Exception
  {
    Field field = ValueHolder.class.getDeclaredField("value");
    ValueHolder holder = new ValueHolder();

    ReflectUtil.makeAccessible(field);

    assertEquals("initial", field.get(holder));
  }

  public void testSetValueWithForce() throws Exception
  {
    Field field = ValueHolder.class.getDeclaredField("value");
    ValueHolder holder = new ValueHolder();

    ReflectUtil.makeAccessibleNormally(field);
    ReflectUtil.setValue(field, holder, "changed", true);

    assertEquals("changed", field.get(holder));

    ReflectUtil.setValue(field, holder, "changed again", false);

    assertEquals("changed again", field.get(holder));
  }

  /**
   * @author Eike Stepper
   */
  private static final class ValueHolder
  {
    @SuppressWarnings("unused")
    private String value = "initial";
  }
}
