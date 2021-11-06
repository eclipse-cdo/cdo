/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories;

import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.explorer.ui.BaseLabelDecorator;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class CDORepositoryLabelDecorator extends BaseLabelDecorator
{
  private static final Image SECURITY_OVERLAY = SharedIcons.getImage(SharedIcons.OVR_SECURITY);

  public CDORepositoryLabelDecorator()
  {
  }

  @Override
  public String decorateText(String text, Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      text += "  " + repository.getURI();
    }

    return text;
  }

  @Override
  public Image decorateImage(Image image, Object element)
  {
    if (element instanceof CDORepository)
    {
      CDORepository repository = (CDORepository)element;
      if (repository.isAuthenticating())
      {
        image = OM.getOverlayImage(image, SECURITY_OVERLAY, 1, 7);
      }
    }

    return image;
  }
}
