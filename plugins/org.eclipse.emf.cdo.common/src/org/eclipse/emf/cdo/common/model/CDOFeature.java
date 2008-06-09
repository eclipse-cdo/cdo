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
package org.eclipse.emf.cdo.common.model;

/**
 * @author Eike Stepper
 */
public interface CDOFeature extends CDOModelElement
{
  public int getFeatureID();

  public int getFeatureIndex();

  public CDOType getType();

  public boolean isMany();

  public boolean isReference();

  public boolean isContainment();

  public CDOClass getReferenceType();

  public CDOClass getContainingClass();

  public CDOPackage getContainingPackage();
}
