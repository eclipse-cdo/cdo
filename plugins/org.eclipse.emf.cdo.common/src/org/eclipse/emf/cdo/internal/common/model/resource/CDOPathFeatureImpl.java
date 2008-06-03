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
import org.eclipse.emf.cdo.common.model.resource.CDOPathFeature;
import org.eclipse.emf.cdo.internal.common.model.CDOFeatureImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;

/**
 * @author Eike Stepper
 */
public class CDOPathFeatureImpl extends CDOFeatureImpl implements CDOPathFeature
{
  public CDOPathFeatureImpl(CDOClass containingClass)
  {
    super(containingClass, FEATURE_ID, NAME, CDOTypeImpl.STRING, false);
  }
}
