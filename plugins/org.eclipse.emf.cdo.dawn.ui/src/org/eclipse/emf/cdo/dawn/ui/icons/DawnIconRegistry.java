/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.dawn.ui.icons;

import org.eclipse.emf.cdo.dawn.internal.ui.bundle.OM;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Based on the implementation from org.eclipse.emf.cdo.ui.shared.SharedIcons. TODO clarify with Eike whether we could
 * provide a better extensibility.
 *
 * @author Martin Fluegge
 * @since 2.0
 */
public class DawnIconRegistry
{
  private static final String GIF = ".gif"; //$NON-NLS-1$

  private static final String PNG = ".png"; //$NON-NLS-1$

  private static final ImageRegistry REGISTRY = new ImageRegistry(getDisplay());

  private static final String ICONS_FOLDER = "icons/"; //$NON-NLS-1$

  public static final String LOCKED = ICONS_FOLDER + "dawn_locked_16x16" + GIF; //$NON-NLS-1$

  public static final String DAWN_LIGHT = ICONS_FOLDER + "dawn_16x16" + PNG; //$NON-NLS-1$

  public static final String DAWN_DARK = ICONS_FOLDER + "dawn_16x16" + GIF; //$NON-NLS-1$

  public static Image getImage(String key)
  {
    Image image = REGISTRY.get(key);
    if (image == null)
    {
      createDescriptor(key);
      image = REGISTRY.get(key);
    }

    // For some reason, sometimes images get disposed.
    // In that case, create and put in registry again.
    if (image.isDisposed())
    {
      REGISTRY.remove(key);
      createDescriptor(key);
      image = REGISTRY.get(key);
    }

    return image;
  }

  public static ImageDescriptor getDescriptor(String key)
  {
    ImageDescriptor descriptor = REGISTRY.getDescriptor(key);
    if (descriptor == null)
    {
      descriptor = createDescriptor(key);
    }

    return descriptor;
  }

  private static ImageDescriptor createDescriptor(String key)
  {
    ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(OM.BUNDLE_ID, key);
    if (descriptor != null)
    {
      REGISTRY.put(key, descriptor);
    }

    return descriptor;
  }

  private static Display getDisplay()
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      throw new IllegalStateException("display == null"); //$NON-NLS-1$
    }

    return display;
  }
}
