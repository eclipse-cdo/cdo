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
package org.eclipse.net4j.internal.util.collection;

import org.eclipse.net4j.util.om.pref.OMPreference;

/**
 * @author Eike Stepper
 */
public class PreferenceHistory extends History<String>
{
  private OMPreference<String[]> preference;

  public PreferenceHistory(OMPreference<String[]> preference)
  {
    this.preference = preference;
  }

  public OMPreference<String[]> getPreference()
  {
    return preference;
  }

  @Override
  protected void load()
  {
    for (String data : preference.getValue())
    {
      internalAdd(data);
    }
  }

  @Override
  protected void save()
  {
    preference.setValue(getData());
  }
}
