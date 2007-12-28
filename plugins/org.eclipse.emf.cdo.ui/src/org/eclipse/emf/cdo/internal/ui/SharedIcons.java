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

  private static final String ETOOL = "etool16/";

  private static final String OBJ = "obj16/";

  private static final String VIEW = "view16/";

  private static final String WIZBAN = "wizban/";

  public static final String ETOOL_OPEN_SESSION = ETOOL + "open_session";

  public static final String ETOOL_OPEN_EDITOR = ETOOL + "open_editor";

  public static final String OBJ_SESSION = OBJ + "cdo_session";

  public static final String OBJ_EDITOR = OBJ + "cdo_editor";

  public static final String OBJ_EDITOR_READWRITE = OBJ + "cdo_editor_readwrite";

  public static final String OBJ_EDITOR_READONLY = OBJ + "cdo_editor_readonly";

  public static final String OBJ_EDITOR_HISTORICAL = OBJ + "cdo_editor_historical";

  public static final String OBJ_EPACKAGE = OBJ + "EPackage";

  public static final String OBJ_EPACKAGE_CONVERTED = OBJ + "EPackageConverted";

  public static final String OBJ_EPACKAGE_LEGACY = OBJ + "EPackageLegacy";

  public static final String OBJ_EPACKAGE_NATIVE = OBJ + "EPackageNative";

  public static final String OBJ_ECLASS = OBJ + "EClass";

  public static final String VIEW_SESSIONS = VIEW + "cdo_sessions";

  public static final String WIZBAN_PACKAGE_MANAGER = WIZBAN + "PackageManager";

  public static Image getImage(String key)
  {
    key = mangleKey(key);
    Image image = REGISTRY.get(key);
    if (image == null)
    {
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
      throw new IllegalStateException("display == null");
    }

    return display;
  }

  private static String mangleKey(String key)
  {
    return "icons/full/" + key + ".gif";
  }
}
