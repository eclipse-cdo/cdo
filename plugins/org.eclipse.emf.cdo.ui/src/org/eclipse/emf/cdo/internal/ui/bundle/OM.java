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
package org.eclipse.emf.cdo.internal.ui.bundle;

import org.eclipse.emf.cdo.ui.CDOLabelProvider;

import org.eclipse.net4j.Net4jTransportInjector;
import org.eclipse.net4j.util.internal.ui.security.InteractiveCredentialsProvider;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.ui.UIActivator;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMTracer TEST = BUNDLE.tracer("test"); //$NON-NLS-1$

  public static final OMTracer TEST_BULK_ADD = TEST.tracer("bulk_add"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<String> PREF_LABEL_DECORATION = //
  PREFS.init("PREF_LABEL_DECORATION", CDOLabelProvider.DEFAULT_DECORATION); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_SELECT_PACKAGES = //
  PREFS.initArray("PREF_HISTORY_SELECT_PACKAGES"); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_CONNECTORS = //
  PREFS.initArray("PREF_HISTORY_CONNECTORS"); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_REPOSITORIES = //
  PREFS.initArray("PREF_HISTORY_REPOSITORIES"); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_AUTOMATIC_PACKAGE_REGISTY = //
  PREFS.init("PREF_AUTOMATIC_PACKAGE_REGISTY", true); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_LEGACY_MODEL_SUPPORT = //
  PREFS.init("PREF_LEGACY_MODEL_SUPPORT", false); //$NON-NLS-1$

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
