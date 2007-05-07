/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.ui.bundle;

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

  public static final String OBJ_ACCEPTOR = OBJ + "acceptor";

  public static final String OBJ_ADAPTER = OBJ + "adapter";

  public static final String OBJ_CHANNEL = OBJ + "channel";

  public static final String OBJ_CONNECTOR = OBJ + "connector";

  public static final String OBJ_FACTORY = OBJ + "factory";

  public static final String OBJ_FOLDER = OBJ + "folder";

  public static final String ETOOL_ADD_ACCEPTOR = ETOOL + "add_acceptor";

  public static final String ETOOL_ADD_CONNECTOR = ETOOL + "add_connector";

  public static final String ETOOL_ADD = ETOOL + "add";

  public static final String ETOOL_DELETE = ETOOL + "delete";

  public static final String ETOOL_REFRESH = ETOOL + "refresh";

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
    ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Net4jUI.BUNDLE_ID, key);
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
