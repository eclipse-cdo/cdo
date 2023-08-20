/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2016, 2018, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.bundle;

import org.eclipse.emf.cdo.common.util.Support;
import org.eclipse.emf.cdo.ui.CDOEditorOpener;
import org.eclipse.emf.cdo.ui.CDOLabelDecorator;
import org.eclipse.emf.cdo.ui.OverlayImage;
import org.eclipse.emf.cdo.ui.UserInfo;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<String> PREF_LABEL_DECORATION = //
      PREFS.init("PREF_LABEL_DECORATION", CDOLabelDecorator.DEFAULT_DECORATION); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_SELECT_PACKAGES = //
      PREFS.initArray("PREF_HISTORY_SELECT_PACKAGES"); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_CONNECTORS = //
      PREFS.init("PREF_HISTORY_CONNECTORS", new String[] { "tcp://localhost" }); //$NON-NLS-1$

  public static final OMPreference<String[]> PREF_HISTORY_REPOSITORIES = //
      PREFS.init("PREF_HISTORY_REPOSITORIES", new String[] { "repo1" }); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_AUTOMATIC_PACKAGE_REGISTRY = //
      PREFS.init("PREF_AUTOMATIC_PACKAGE_REGISTRY", true); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_EDITOR_AUTO_RELOAD = //
      PREFS.init("PREF_EDITOR_AUTO_RELOAD", true); //$NON-NLS-1$

  public static final OMPreference<Long> PREF_LOCK_TIMEOUT = //
      PREFS.init("PREF_LOCK_TIMEOUT", 10000L); //$NON-NLS-1$

  public static final OMPreference<Boolean> PREF_TOPICS_LINK_WITH_EDITOR = //
      PREFS.init("PREF_TOPICS_LINK_WITH_EDITOR", false); //$NON-NLS-1$

  public static final OMPreference<String> PREF_USER_FIRST_NAME = //
      PREFS.init("PREF_USER_FIRST_NAME", ""); //$NON-NLS-1$

  public static final OMPreference<String> PREF_USER_LAST_NAME = //
      PREFS.init("PREF_USER_LAST_NAME", ""); //$NON-NLS-1$

  public static final OMPreference<String> PREF_USER_DISPLAY_NAME = //
      PREFS.init("PREF_USER_DISPLAY_NAME", System.getProperty("user.name")); //$NON-NLS-1$

  /**
   * @deprecated As of 4.13 use {@link Support#UI_HISTORY Support.UI_HISTORY.isAvailable()}.
   */
  @Deprecated
  public static boolean isHistorySupportAvailable()
  {
    return Support.UI_HISTORY.isAvailable();
  }

  /**
   * @deprecated As of 4.13 use {@link Support#UI_COMPARE Support.UI_COMPARE.isAvailable()}.
   */
  @Deprecated
  public static boolean isCompareSupportAvailable()
  {
    return Support.UI_COMPARE.isAvailable();
  }

  public static Image getOverlayImage(Object image, Object overlayImage, int x, int y)
  {
    ComposedImage composedImage = new OverlayImage(image, overlayImage, x, y);
    return ExtendedImageRegistry.INSTANCE.getImage(composedImage);
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
      CDOEditorOpener.Registry.INSTANCE.activate();
    }

    @Override
    protected void doStop() throws Exception
    {
      UserInfo.Manager userInfoManager = UserInfo.Manager.getInstanceOrNull();
      if (userInfoManager != null)
      {
        userInfoManager.deactivate();
      }

      CDOEditorOpener.Registry.INSTANCE.deactivate();
    }
  }
}
