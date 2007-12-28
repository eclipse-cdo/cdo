/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.internal.util.om.LegacyBundle;

/**
 * @author Eike Stepper
 */
public final class LegacyUtil
{
  private LegacyUtil()
  {
  }

  public static void startBundles(OMBundle[] bundles) throws Exception
  {
    for (int i = 0; i < bundles.length; i++)
    {
      ((LegacyBundle)bundles[i]).start();
    }
  }

  public static void stopBundles(OMBundle[] bundles) throws Exception
  {
    for (int i = bundles.length - 1; i >= 0; i--)
    {
      ((LegacyBundle)bundles[i]).stop();
    }
  }
}
