/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
