/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.graphiti.features;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

/**
 * @author Martin Fluegge
 */
public class AcoreCopyAClassFeature extends AbstractCopyFeature
{

  public AcoreCopyAClassFeature(IFeatureProvider fp)
  {
    super(fp);
  }

  public boolean canCopy(ICopyContext context)
  {
    final PictogramElement[] pes = context.getPictogramElements();
    if (pes == null || pes.length == 0)
    { // nothing selected
      return false;
    }

    // return true, if all selected elements are a AClasses
    for (PictogramElement pe : pes)
    {
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof AClass))
      {
        return false;
      }
    }
    return true;
  }

  public void copy(ICopyContext context)
  {
    // get the business-objects for all pictogram-elements
    // we already verified, that all business-objets are AClasses
    PictogramElement[] pes = context.getPictogramElements();
    Object[] bos = new Object[pes.length];
    for (int i = 0; i < pes.length; i++)
    {
      PictogramElement pe = pes[i];
      bos[i] = getBusinessObjectForPictogramElement(pe);
    }
    // put all business objects to the clipboard
    putToClipboard(bos);
  }
}
