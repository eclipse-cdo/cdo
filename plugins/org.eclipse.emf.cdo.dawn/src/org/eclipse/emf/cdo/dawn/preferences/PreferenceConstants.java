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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Martin Fluegge
 */
public class PreferenceConstants
{

  public static final String P_SERVER_NAME = "serverURL";

  public static final String P_SERVER_PORT = "serverPort";

  public static final String P_REPOSITORY_NAME = "repositoryName";

  public static final String P_PROTOCOL = "protocol";

  public static String getServerName()
  {
    return getPreferenceStore().getString(P_SERVER_NAME);
  }

  public static String getServerPort()
  {
    return getPreferenceStore().getString(P_SERVER_PORT);
  }

  public static String getRepositoryName()
  {
    return getPreferenceStore().getString(P_REPOSITORY_NAME);
  }

  public static String getProtocol()
  {
    return getPreferenceStore().getString(P_PROTOCOL);
  }

  private static IPreferenceStore getPreferenceStore()
  {
    return DawnRuntimePlugin.getDefault().getPreferenceStore();
  }
}
