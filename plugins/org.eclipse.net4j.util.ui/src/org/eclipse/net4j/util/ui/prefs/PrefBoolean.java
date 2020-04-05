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
package org.eclipse.net4j.util.ui.prefs;

import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Eike Stepper
 * @since 3.8
 */
public class PrefBoolean extends Composite implements OMPreference.Participant
{
  private final OMPreference<Boolean> booleanPreference;

  private final Button button;

  public PrefBoolean(Composite parent, int buttonStyle, OMPreference<Boolean> booleanPreference)
  {
    super(parent, SWT.NONE);
    this.booleanPreference = booleanPreference;

    setLayout(new FillLayout());
    button = new Button(this, buttonStyle);
  }

  public final OMPreference<Boolean> getBooleanPreference()
  {
    return booleanPreference;
  }

  public final Button getButton()
  {
    return button;
  }

  @Override
  public void loadPreferences()
  {
    button.setSelection(booleanPreference.getValue());
  }

  @Override
  public void savePreferences()
  {
    booleanPreference.setValue(button.getSelection());
  }
}
