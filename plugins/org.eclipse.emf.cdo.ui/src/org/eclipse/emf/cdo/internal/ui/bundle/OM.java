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
package org.eclipse.emf.cdo.internal.ui.bundle;

import org.eclipse.net4j.internal.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.internal.ui.security.InteractiveCredentialsProvider;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.internal.net4j.Net4jTransportInjector;

/**
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<String[]> PREF_HISTORY_SELECT_PACKAGES = //
  PREFS.initArray("PREF_HISTORY_SELECT_PACKAGES");

  public static final OMPreference<String[]> PREF_HISTORY_CONNECTORS = //
  PREFS.initArray("PREF_HISTORY_CONNECTORS");

  public static final OMPreference<String[]> PREF_HISTORY_REPOSITORIES = //
  PREFS.initArray("PREF_HISTORY_REPOSITORIES");

  public static final OMPreference<Boolean> PREF_LEGACY_SUPPORT = //
  PREFS.init("PREF_LEGACY_SUPPORT", true);

  static void start()
  {
    if (false)
    {
      try
      {
        ResponseNegotiator responseNegotiator = new ResponseNegotiator();
        responseNegotiator.setCredentialsProvider(new InteractiveCredentialsProvider());
        responseNegotiator.activate();

        Net4jTransportInjector.clientNegotiator = responseNegotiator;
      }
      catch (Exception ex)
      {
        LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public static Activator INSTANCE;

    public Activator()
    {
      super(BUNDLE);
      INSTANCE = this;
    }
  }
}
