/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.buddies.internal.ui;

import org.eclipse.net4j.buddies.internal.ui.bundle.OM;

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

  @SuppressWarnings("unused")
  private static final String ETOOL = "etool16/";

  private static final String OBJ = "obj16/";

  @SuppressWarnings("unused")
  private static final String VIEW = "view16/";

  @SuppressWarnings("unused")
  private static final String WIZBAN = "wizban/";

  public static final String OBJ_COLLABORATION = OBJ + "collaboration";

  public static final String OBJ_BUDDY = OBJ + "buddy";

  public static final String OBJ_BUDDY_LONESOME = OBJ + "buddy_lonesome";

  public static final String OBJ_BUDDY_AWAY = OBJ + "buddy_away";

  public static final String OBJ_BUDDY_DO_NOT_DISTURB = OBJ + "buddy_do_not_disturb";

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
