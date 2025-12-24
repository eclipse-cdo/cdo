/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.pref;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMPreference<T>
{
  public OMPreferences getPreferences();

  public String getName();

  public Type getType();

  public T getDefaultValue();

  public T getValue();

  public T setValue(T value);

  public T unSet();

  public boolean isSet();

  /**
   * @author Eike Stepper
   */
  public enum Type
  {
    BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, STRING, ARRAY, BYTES
  }

  /**
   * @author Eike Stepper
   * @since 3.5
   */
  public interface Participant
  {
    public void loadPreferences();

    public void savePreferences();
  }
}
