/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.actions;

import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class ToggleAction extends SafeAction
{
  private final OMPreference<Boolean> preference;

  public ToggleAction(String text, ImageDescriptor imageDescriptor, OMPreference<Boolean> preference)
  {
    super(text, AS_CHECK_BOX);
    setImageDescriptor(imageDescriptor);
    this.preference = preference;
    setChecked(preference.getValue());
  }

  @Override
  protected final void safeRun() throws Exception
  {
    boolean checked = isChecked();
    preference.setValue(checked);
    run(checked);
  }

  protected abstract void run(boolean checked);
}
