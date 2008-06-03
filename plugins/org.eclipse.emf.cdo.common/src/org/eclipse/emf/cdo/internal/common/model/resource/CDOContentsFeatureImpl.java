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
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.model.resource.CDOContentsFeature;
import org.eclipse.emf.cdo.internal.common.model.CDOFeatureImpl;

/**
 * @author Eike Stepper
 */
public class CDOContentsFeatureImpl extends CDOFeatureImpl implements CDOContentsFeature
{
  public CDOContentsFeatureImpl(CDOClass containingClass, CDOPackageManager packageManager)
  {
    super(containingClass, FEATURE_ID, NAME, new CDOClassProxy(packageManager.getCDOCorePackage().getCDOObjectClass()),
        true, true);
  }
}
