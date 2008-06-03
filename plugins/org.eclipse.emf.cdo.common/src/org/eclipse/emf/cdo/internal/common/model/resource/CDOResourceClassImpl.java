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
package org.eclipse.emf.cdo.internal.common.model.resource;

import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.internal.common.model.CDOClassImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceClassImpl extends CDOClassImpl implements CDOResourceClass
{
  private CDOPathFeatureImpl cdoPathFeature;

  private CDOContentsFeatureImpl cdoContentsFeature;

  public CDOResourceClassImpl(CDOPackage containingPackage, CDOPackageManager packageManager)
  {
    super(containingPackage, CLASSIFIER_ID, NAME, false);
    addFeature(cdoPathFeature = new CDOPathFeatureImpl(this));
    addFeature(cdoContentsFeature = new CDOContentsFeatureImpl(this, packageManager));
  }

  @Override
  public boolean isResource()
  {
    return true;
  }

  public CDOPathFeatureImpl getCDOPathFeature()
  {
    return cdoPathFeature;
  }

  public CDOContentsFeatureImpl getCDOContentsFeature()
  {
    return cdoContentsFeature;
  }
}
