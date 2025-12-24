/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public class PreferenceHistory extends History<String>
{
  private OMPreference<String[]> preference;

  public PreferenceHistory(OMPreference<String[]> preference)
  {
    CheckUtil.checkArg(preference, "preference"); //$NON-NLS-1$
    this.preference = preference;
  }

  public OMPreference<String[]> getPreference()
  {
    return preference;
  }

  @Override
  protected void load()
  {
    String[] value = preference.getValue();
    if (value != null)
    {
      for (String data : value)
      {
        IHistoryElement<String> element = createElement(data);
        elements.add(element);
      }
    }
  }

  @Override
  protected void save()
  {
    String[] array = getData(new String[size()]);
    preference.setValue(array);
  }
}
