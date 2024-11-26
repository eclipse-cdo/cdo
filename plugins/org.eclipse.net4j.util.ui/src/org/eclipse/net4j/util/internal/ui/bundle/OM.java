/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015, 2019-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.net4j.util.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<Boolean> PREF_SHOW_CONTAINER_FACTORIES = PREFS.init("PREF_SHOW_CONTAINER_FACTORIES", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_DECORATE_CONTAINER_ELEMENTS = PREFS.init("PREF_DECORATE_CONTAINER_ELEMENTS", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_LINK_SELECTION = PREFS.init("PREF_LINK_SELECTION", true); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_LOGICAL_STRUCTURE = PREFS.init("PREF_LOGICAL_STRUCTURE", true); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_ACTIVE_PART = PREFS.init("PREF_ACTIVE_PART", false); //$NON-NLS-1$

  /**
   * @since 3.19
   */
  public static Image getImage(String path)
  {
    return Activator.INSTANCE.getImage(path);
  }

  public static ImageDescriptor getImageDescriptor(String path)
  {
    return Activator.INSTANCE.getImageDescriptor(path);
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
