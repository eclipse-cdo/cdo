/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.ui.bundle;

import org.eclipse.emf.cdo.ui.OverlayImage;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;
import org.eclipse.net4j.util.ui.UIActivator;

import org.eclipse.emf.common.util.URI;
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
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.lm.reviews.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static ImageDescriptor getImageDescriptor(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImageDescriptor(getBundleURI(imagePath));
  }

  public static Image getImage(String imagePath)
  {
    return ExtendedImageRegistry.INSTANCE.getImage(getBundleURI(imagePath));
  }

  public static Image getOverlayImage(String imagePath, String overlayImagePath, int x, int y)
  {
    URI image = getBundleURI(imagePath);
    if (image == null)
    {
      return null;
    }

    URI overlayImage = getBundleURI(overlayImagePath);
    return new OverlayImage(image, overlayImage, x, y).compose();
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
  }
}
