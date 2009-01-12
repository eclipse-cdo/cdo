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
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class RepositoryLabelProvider extends LabelProvider implements ILabelProvider
{
  public RepositoryLabelProvider()
  {
  }

  @Override
  public String getText(Object element)
  {
    if (element instanceof Repository.Content)
    {
      Repository.Content content = (Repository.Content)element;
      return content.getText();
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (element instanceof Repository.Content)
    {
      Repository.Content content = (Repository.Content)element;
      String imageKey = content.getImageKey();
      ImageDescriptor imageDescriptor = OM.Activator.imageDescriptorFromPlugin(OM.BUNDLE_ID, imageKey);
      return imageDescriptor.createImage();
    }

    return super.getImage(element);
  }
}
