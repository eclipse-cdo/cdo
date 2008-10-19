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

import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOClassImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceClassImpl extends CDOClassImpl implements CDOResourceClass
{
  private CDOContentsFeatureImpl cdoContentsFeature;

  public CDOResourceClassImpl(CDOPackage containingPackage, CDOPackageManager packageManager)
  {
    super(containingPackage, CLASSIFIER_ID, NAME, false);
    addSuperType(CDOModelUtil.createClassRef(CDOResourcePackage.PACKAGE_URI, CDOResourceNodeClass.CLASSIFIER_ID));
    addFeature(cdoContentsFeature = new CDOContentsFeatureImpl(this, packageManager));
  }

  @Override
  public boolean isResourceNode()
  {
    return true;
  }

  @Override
  public boolean isResourceFolder()
  {
    return false;
  }

  @Override
  public boolean isResource()
  {
    return true;
  }

  public CDOContentsFeatureImpl getCDOContentsFeature()
  {
    return cdoContentsFeature;
  }

  @Override
  public CDOFeature lookupFeature(int featureID)
  {
    if (featureID == 5)
    {
      return cdoContentsFeature;
    }

    return super.lookupFeature(featureID);
  }
}
