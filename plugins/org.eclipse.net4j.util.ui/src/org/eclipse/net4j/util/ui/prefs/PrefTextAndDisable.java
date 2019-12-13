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
public class PrefTextAndDisable extends TextAndDisable implements OMPreference.Participant
{
  private final OMPreference<String> textPreference;

  private final OMPreference<Boolean> disabledPreference;

  public PrefTextAndDisable(Composite parent, int textStyle, OMPreference<String> textPreference, OMPreference<Boolean> disabledPreference)
  {
    super(parent, textStyle, null);
    this.textPreference = textPreference;
    this.disabledPreference = disabledPreference;
  }

  public final OMPreference<String> getTextPreference()
  {
    return textPreference;
  }

  public final OMPreference<Boolean> getDisabledPreference()
  {
    return disabledPreference;
  }

  @Override
  public void loadPreferences()
  {
    setValue(textPreference.getValue());
    setDisabled(disabledPreference.getValue());
  }

  @Override
  public void savePreferences()
  {
    textPreference.setValue(getValue());
    disabledPreference.setValue(isDisabled());
  }

  @Override
  protected void checkSubclass()
  {
    // Allow subclassing.
  }
}
