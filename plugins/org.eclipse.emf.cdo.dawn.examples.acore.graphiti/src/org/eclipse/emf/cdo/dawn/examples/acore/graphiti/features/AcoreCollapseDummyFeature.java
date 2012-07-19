/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
public class AcoreCollapseDummyFeature extends AbstractCustomFeature
{

  public AcoreCollapseDummyFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  @Override
  public boolean canExecute(ICustomContext context)
  {
    boolean ret = false;
    PictogramElement[] pes = context.getPictogramElements();
    if (pes != null && pes.length == 1)
    {
      Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof AClass)
      {
        ret = true;
      }
    }
    return ret;
  }

  @Override
  public String getName()
  {
    return "Co&llapse"; //$NON-NLS-1$
  }

  @Override
  public String getDescription()
  {

    return "Collapse Figure"; //$NON-NLS-1$
  }

  @Override
  public String getImageId()
  {
    return IPlatformImageConstants.IMG_EDIT_COLLAPSE;
  }

  @Override
  public boolean isAvailable(IContext context)
  {
    return true;
  }

  public void execute(ICustomContext context)
  {
    MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Information",//$NON-NLS-1$
        "The 'Collapse Feature' is intentionally not implemented yet."); //$NON-NLS-1$
  }

}
