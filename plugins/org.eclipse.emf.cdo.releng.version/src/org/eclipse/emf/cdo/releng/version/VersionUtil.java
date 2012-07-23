/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.osgi.framework.Version;

/**
 * @author Eike Stepper
 */
public class VersionUtil
{
  public static Version normalize(Version version)
  {
    return new Version(version.getMajor(), version.getMinor(), version.getMicro());
  }

  /**
   * @deprecated This method exists so that others can produce a reliable compiler warning by calling it. A
   *             <code>@SuppressWarnings("deprecation")</code> annotation will never become unnecessary then.
   */
  @Deprecated
  public static void someDeprecatedCode()
  {
  }
}
