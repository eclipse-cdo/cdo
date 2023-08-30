/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.providers;

import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.provider.LMEditPlugin;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 */
public class SystemLabelProvider extends AdapterFactoryLabelProvider.StyledLabelProvider
{
  private final Color gray = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);

  public SystemLabelProvider(AdapterFactory adapterFactory, Viewer viewer)
  {
    super(adapterFactory, viewer);
  }

  @Override
  public Image getImage(Object object)
  {
    if (object instanceof ISystemDescriptor)
    {
      return ExtendedImageRegistry.INSTANCE.getImage(LMEditPlugin.INSTANCE.getImage("full/obj16/System"));
    }

    if (object instanceof Change)
    {
      Change change = (Change)object;
      if (!change.isDeliverable())
      {
        return ExtendedImageRegistry.INSTANCE.getImage(LMEditPlugin.INSTANCE.getImage("full/obj16/ChangeDisabled"));
      }
    }

    return super.getImage(object);
  }

  @Override
  public String getText(Object object)
  {
    if (object instanceof ISystemDescriptor)
    {
      ISystemDescriptor descriptor = (ISystemDescriptor)object;
      return descriptor.getSystemName() + " " + descriptor.getState();
    }

    return super.getText(object);
  }

  @Override
  public Color getForeground(Object object)
  {
    if (object instanceof Change)
    {
      Change change = (Change)object;
      if (!change.isDeliverable())
      {
        return gray;
      }
    }

    return super.getForeground(object);
  }
}
