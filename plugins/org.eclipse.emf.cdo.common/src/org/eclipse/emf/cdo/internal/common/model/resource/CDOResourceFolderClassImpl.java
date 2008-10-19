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

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceFolderClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOClassImpl;

/**
 * @author Eike Stepper
 */
public class CDOResourceFolderClassImpl extends CDOClassImpl implements CDOResourceFolderClass
{
  private CDONodesFeatureImpl cdoNodesFeature;

  public CDOResourceFolderClassImpl(CDOPackage containingPackage, CDOPackageManager packageManager)
  {
    super(containingPackage, CLASSIFIER_ID, NAME, false);
    addSuperType(CDOModelUtil.createClassRef(CDOResourcePackage.PACKAGE_URI, CDOResourceNodeClass.CLASSIFIER_ID));
    addFeature(cdoNodesFeature = new CDONodesFeatureImpl(this, packageManager));
  }

  @Override
  public boolean isResourceNode()
  {
    return true;
  }

  @Override
  public boolean isResourceFolder()
  {
    return true;
  }

  @Override
  public boolean isResource()
  {
    return false;
  }

  public CDONodesFeatureImpl getCDONodesFeature()
  {
    return cdoNodesFeature;
  }
}
