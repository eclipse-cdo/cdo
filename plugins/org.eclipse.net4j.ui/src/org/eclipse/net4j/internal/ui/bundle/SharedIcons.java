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

  private static final String OBJ = "icons/full/obj16/";

  public static final ImageDescriptor OBJ_ACCEPTOR = getDescriptor(OBJ + "acceptor.gif");

  public static final ImageDescriptor OBJ_ADAPTER = getDescriptor(OBJ + "adapter.gif");

  public static final ImageDescriptor OBJ_CHANNEL = getDescriptor(OBJ + "channel.gif");

  public static final ImageDescriptor OBJ_CONNECTOR = getDescriptor(OBJ + "connector.gif");

  public static final ImageDescriptor OBJ_FACTORY = getDescriptor(OBJ + "factory.gif");

  public static final ImageDescriptor OBJ_FOLDER = getDescriptor(OBJ + "folder.gif");

  public static Image getImage(String key)
  {
    ImageDescriptor descriptor = getDescriptor(key);
    return descriptor.createImage();
  }

  public static ImageDescriptor getDescriptor(String key)
  {
    ImageDescriptor descriptor = REGISTRY.getDescriptor(key);
    if (descriptor == null)
    {
      descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Net4jUI.BUNDLE_ID, key);
      if (descriptor != null)
      {
        REGISTRY.put(key, descriptor);
      }
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
}
