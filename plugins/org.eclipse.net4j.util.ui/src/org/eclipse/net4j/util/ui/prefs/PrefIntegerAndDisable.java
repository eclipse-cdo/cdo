/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.prefs;

import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class PrefIntegerAndDisable extends TextAndDisable implements OMPreference.Participant
{
  private final OMPreference<Integer> integerPreference;

  private final OMPreference<Boolean> disabledPreference;

  public PrefIntegerAndDisable(Composite parent, int textStyle, OMPreference<Integer> integerPreference, OMPreference<Boolean> disabledPreference)
  {
    super(parent, textStyle, null);
    this.integerPreference = integerPreference;
    this.disabledPreference = disabledPreference;
  }

  public final OMPreference<Integer> getIntegerPreference()
  {
    return integerPreference;
  }

  public final OMPreference<Boolean> getDisabledPreference()
  {
    return disabledPreference;
  }

  @Override
  public void loadPreferences()
  {
    setValue(Integer.toString(integerPreference.getValue()));
    setDisabled(disabledPreference.getValue());
  }

  @Override
  public void savePreferences()
  {
    integerPreference.setValue(Integer.parseInt(getValue()));
    disabledPreference.setValue(isDisabled());
  }

  @Override
  protected void checkSubclass()
  {
    // Allow subclassing.
  }
}
