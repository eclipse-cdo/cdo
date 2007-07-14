/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.pref;

import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.om.OMBundle;

/**
 * @author Eike Stepper
 */
public interface OMPreferences extends INotifier
{
  public OMBundle getBundle();

  public boolean isDirty();

  public void save();

  public OMPreference<Boolean> init(String name, boolean defaultValue);

  public OMPreference<Integer> init(String name, int defaultValue);

  public OMPreference<Long> init(String name, long defaultValue);

  public OMPreference<Float> init(String name, float defaultValue);

  public OMPreference<Double> init(String name, double defaultValue);

  public OMPreference<String> init(String name, String defaultValue);

  public OMPreference<Boolean> getBoolean(String name);

  public OMPreference<Integer> getInteger(String name);

  public OMPreference<Long> getLong(String name);

  public OMPreference<Float> getFloat(String name);

  public OMPreference<Double> getDouble(String name);

  public OMPreference<String> getString(String name);
}
