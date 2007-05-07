/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public abstract class ValueStep<V> extends Step
{
  private String key;

  private Class valueClass;

  private Control control;

  public ValueStep(String label, String key, Class valueClass)
  {
    super(label);
    this.key = key;
    this.valueClass = valueClass;
  }

  public ValueStep(String key, Class valueClass)
  {
    this(key, key, valueClass);
  }

  public final String getKey()
  {
    return key;
  }

  public final Class getValueClass()
  {
    return valueClass;
  }

  public final V getValue()
  {
    Object value = getContextValue(key);
    if (value != null && !valueClass.isAssignableFrom(value.getClass()))
    {
      throw new IllegalStateException("Wrong value class: " + value.getClass().getName());
    }

    return (V)value;
  }

  public final Object setValue(V value)
  {
    return setContextValue(key, value);
  }

  public final boolean hasValue()
  {
    return getValue() != null;
  }

  @Override
  public boolean isReady()
  {
    return true;
  }

  @Override
  public boolean isFinished()
  {
    V value = getValue();
    return value != null && validateValue(value) == null;
  }

  protected String validateValue(V value)
  {
    if (value == null)
    {
      return "Enter a " + getLabel() + " value";
    }

    return null;
  }

  protected abstract Control createControl(Composite parent);

  final String validate()
  {
    return validateValue(getValue());
  }

  final Control getControl()
  {
    return control;
  }

  final void setControl(Control control)
  {
    this.control = control;
  }
}
