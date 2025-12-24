/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.preferences;

import org.eclipse.emf.cdo.dawn.DawnRuntimePlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Martin Fluegge
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

  public PreferenceInitializer()
  {
  }

  @Override
  public void initializeDefaultPreferences()
  {
    AbstractUIPlugin pluginInstance = DawnRuntimePlugin.getDefault();

    IPreferenceStore store = pluginInstance.getPreferenceStore();
    store.setDefault(PreferenceConstants.P_SERVER_NAME, "localhost");
    store.setDefault(PreferenceConstants.P_SERVER_PORT, 2036);
    store.setDefault(PreferenceConstants.P_PROTOCOL, "tcp");
    store.setDefault(PreferenceConstants.P_REPOSITORY_NAME, "repo1");
  }
}
