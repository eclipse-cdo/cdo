/*
 * Copyright (c) 2015, 2016, 2020-2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui.bundle;

import org.eclipse.emf.cdo.explorer.ui.checkouts.workingsets.OthersWorkingSetUpdater;
import org.eclipse.emf.cdo.ui.OverlayImage;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * The <em>Operations & Maintenance</em> class of this bundle.
 *
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.explorer.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<Integer> PREF_REPOSITORY_TIMEOUT_MINUTES = //
      PREFS.init("PREF_REPOSITORY_TIMEOUT_MINUTES", 5); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_REPOSITORY_TIMEOUT_DISABLED = //
      PREFS.init("PREF_REPOSITORY_TIMEOUT_DISABLED", false); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_REMEMBER_OPEN_EDITORS = //
      PREFS.init("PREF_REMEMBER_OPEN_EDITORS", true); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_SHOW_OBJECTS_IN_EXPLORER = //
      PREFS.init("PREF_SHOW_OBJECTS_IN_EXPLORER", true); //$NON-NLS-1$

  public static final OMPreference<Integer> PREF_DASHBOARD_HEIGHT = //
      PREFS.init("PREF_DASHBOARD_HEIGHT", 0); //$NON-NLS-1$

  public static Image getOverlayImage(Object image, Object overlayImage, int x, int y)
  {
    if (image == null)
    {
      return null;
    }

    return new OverlayImage(image, overlayImage, x, y).compose();
  }

  public static Image getImage(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImage(getBundleURI(imagePath));
  }

  public static ImageDescriptor getImageDescriptor(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImageDescriptor(getBundleURI(imagePath));
  }

  private static URI getBundleURI(String path)
  {
    return URI.createPlatformPluginURI(BUNDLE_ID + "/" + path, true);
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

    @Override
    protected void doStart() throws Exception
    {
      IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      if (!existsOthersWorkingSet(workingSetManager))
      {
        IWorkingSet workingSet = workingSetManager.createWorkingSet(OthersWorkingSetUpdater.WORKING_SET_NAME, new IAdaptable[0]);
        workingSet.setId(OthersWorkingSetUpdater.WORKING_SET_ID);
        workingSetManager.addWorkingSet(workingSet);
      }
    }

    private boolean existsOthersWorkingSet(IWorkingSetManager workingSetManager)
    {
      for (IWorkingSet workingSet : workingSetManager.getAllWorkingSets())
      {
        if (OthersWorkingSetUpdater.WORKING_SET_ID.equals(workingSet.getId()))
        {
          return true;
        }
      }

      return false;
    }
  }
}
