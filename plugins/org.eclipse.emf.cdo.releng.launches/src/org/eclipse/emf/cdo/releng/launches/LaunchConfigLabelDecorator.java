/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.launches;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class LaunchConfigLabelDecorator extends BaseLabelProvider implements ILabelDecorator
{
  private ImageDescriptor overlay = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/local_ovr.gif");

  public LaunchConfigLabelDecorator()
  {
  }

  public Image decorateImage(Image image, Object element)
  {
    if (isLocalConfiguration(element))
    {
      DecorationOverlayIcon icon = new DecorationOverlayIcon(image, overlay, IDecoration.TOP_LEFT);
      return icon.createImage();
    }

    return null;
  }

  public String decorateText(String text, Object element)
  {
    if (isLocalConfiguration(element))
    {
      return "*" + text;
    }

    return null;
  }

  private boolean isLocalConfiguration(Object element)
  {
    if (element instanceof ILaunchConfiguration)
    {
      ILaunchConfiguration configuration = (ILaunchConfiguration)element;
      return configuration.isLocal();
    }

    return false;
  }
}
