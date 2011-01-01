/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation 
 *    Eike Stepper - maintenance 
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.session.CDOSession;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Class to indicate if legacy is enabled or not for certain CDOSession
 * 
 * @author Victor Roldan Betancort
 */
public class LegacyModeRegistry
{
  private static Map<CDOSession, Boolean> isLegacyEnabledForSession = Collections
      .synchronizedMap(new WeakHashMap<CDOSession, Boolean>());

  private LegacyModeRegistry()
  {

  }

  public static void setLegacyEnabled(CDOSession session, boolean isLegacyEnabled)
  {
    isLegacyEnabledForSession.put(session, isLegacyEnabled);
  }

  public static boolean isLegacyEnabled(CDOSession session)
  {
    Boolean status = isLegacyEnabledForSession.get(session);
    return status != null ? status : false;
  }
}
