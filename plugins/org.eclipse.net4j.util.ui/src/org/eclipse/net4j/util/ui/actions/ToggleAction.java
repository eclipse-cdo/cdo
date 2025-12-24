/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
