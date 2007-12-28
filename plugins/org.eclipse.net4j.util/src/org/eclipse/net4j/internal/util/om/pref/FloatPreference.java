/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public final class FloatPreference extends Preference<Float> implements OMPreference<Float>
{
  public FloatPreference(Preferences preferences, String name, Float defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    return Float.toString(getValue());
  }

  @Override
  protected Float convert(String value)
  {
    return Float.parseFloat(value);
  }

  public Type getType()
  {
    return Type.FLOAT;
  }
}
