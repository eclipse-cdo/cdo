/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.efs.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 * 
 * @author Martin Fluegge
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ui.efs"; //$NON-NLS-1$

  public static final String ERROR_ICON = "icons/full/elcl16/error.gif"; //$NON-NLS-1$

  public static final String WARNING_ICON = "icons/full/elcl16/warning.gif"; //$NON-NLS-1$

  public static final String INFO_ICON = "icons/full/elcl16/info.gif"; //$NON-NLS-1$

  public static final String BRANCH_ICON = "icons/full/obj16/branch.png"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static ImageDescriptor getImageDescriptor(String imageFilePath)
  {
    return Activator.imageDescriptorFromPlugin(BUNDLE_ID, imageFilePath);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public Activator()
    {
      super(BUNDLE);
    }
  }
}
