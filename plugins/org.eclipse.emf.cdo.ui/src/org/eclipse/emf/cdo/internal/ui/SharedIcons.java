/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Eike Stepper
 */
public class SharedIcons
{
  private static final ImageRegistry REGISTRY = new ImageRegistry(getDisplay());

  private static final String ETOOL = "etool16/"; //$NON-NLS-1$

  private static final String OBJ = "obj16/"; //$NON-NLS-1$

  private static final String VIEW = "view16/"; //$NON-NLS-1$

  private static final String WIZBAN = "wizban/"; //$NON-NLS-1$

  public static final String ETOOL_OPEN_SESSION = ETOOL + "open_session"; //$NON-NLS-1$

  public static final String ETOOL_OPEN_EDITOR = ETOOL + "open_editor"; //$NON-NLS-1$

  public static final String OBJ_SESSION = OBJ + "cdo_session"; //$NON-NLS-1$

  public static final String OBJ_EDITOR = OBJ + "cdo_editor"; //$NON-NLS-1$

  public static final String OBJ_EDITOR_READWRITE = OBJ + "cdo_editor_readwrite"; //$NON-NLS-1$

  public static final String OBJ_EDITOR_READONLY = OBJ + "cdo_editor_readonly"; //$NON-NLS-1$

  public static final String OBJ_EDITOR_HISTORICAL = OBJ + "cdo_editor_historical"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE = OBJ + "EPackage"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_CONVERTED = OBJ + "EPackageConverted"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_LEGACY = OBJ + "EPackageLegacy"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_NATIVE = OBJ + "EPackageNative"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_DYNAMIC = OBJ + "EPackageDynamic"; //$NON-NLS-1$

  public static final String OBJ_EPACKAGE_UNKNOWN = OBJ + "EPackageUnknown"; //$NON-NLS-1$

  public static final String OBJ_ECLASS = OBJ + "EClass"; //$NON-NLS-1$

  public static final String VIEW_SESSIONS = VIEW + "cdo_sessions"; //$NON-NLS-1$

  public static final String WIZBAN_PACKAGE_MANAGER = WIZBAN + "PackageManager"; //$NON-NLS-1$

  public static Image getImage(String key)
  {
    key = mangleKey(key);
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
    key = mangleKey(key);
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

  private static String mangleKey(String key)
  {
    return "icons/full/" + key + ".gif"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
