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

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDONodesFeature;
import org.eclipse.emf.cdo.common.model.resource.CDOResourceNodeClass;
import org.eclipse.emf.cdo.common.model.resource.CDOResourcePackage;
import org.eclipse.emf.cdo.internal.common.model.CDOFeatureImpl;

/**
 * @author Eike Stepper
 */
public class CDONodesFeatureImpl extends CDOFeatureImpl implements CDONodesFeature
{
  public CDONodesFeatureImpl(CDOClass containingClass, CDOPackageManager packageManager)
  {
    super(containingClass, FEATURE_ID, NAME, new CDOClassProxy(CDOModelUtil.createClassRef(
        CDOResourcePackage.PACKAGE_URI, CDOResourceNodeClass.CLASSIFIER_ID), packageManager), true, true);
  }
}
