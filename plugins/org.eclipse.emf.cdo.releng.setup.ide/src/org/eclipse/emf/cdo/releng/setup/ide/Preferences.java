/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.helper.Progress;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Eike Stepper
 */
public final class Preferences
{
  private static final IEclipsePreferences ROOT = Platform.getPreferencesService().getRootNode();

  public static void setRunInBackground() throws Exception
  {
    setBoolean(ROOT.node("instance").node("org.eclipse.ui.workbench"), "RUN_IN_BACKGROUND", true);
  }

  private static void setBoolean(org.osgi.service.prefs.Preferences node, String key, boolean value)
  {
    if (node.getBoolean(key, false) != value)
    {
      Progress.log().addLine("Setting " + key + " = " + value);
      node.putBoolean(key, value);
    }
  }

  // private static void dump(Preferences node, String indent) throws Exception
  // {
  // System.out.println(indent + "NODE " + node.name());
  // for (String key : node.keys())
  // {
  // String value = node.get(key, "<null>");
  // System.out.println(indent + key + " = " + value);
  // }
  //
  // for (String name : node.childrenNames())
  // {
  // Preferences child = node.node(name);
  // dump(child, indent + "   ");
  // }
  // }
}
