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
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassProxy;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;

/**
 * @author Eike Stepper
 */
public interface InternalCDOFeature extends CDOFeature, InternalCDOModelElement
{
  public CDOClassProxy getReferenceTypeProxy();

  public void setContainingClass(CDOClass containingClass);

  public void setFeatureIndex(int featureIndex);

  public void setReferenceType(CDOClassRef cdoClassRef);

  @Deprecated
  public void setReferenceType(CDOClass cdoClass);
}
