/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 * 		Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.io;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.ExtendedDataInput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDODataInput extends ExtendedDataInput
{
  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOPackageUnit readCDOPackageUnit(CDOPackageRegistry packageRegistry) throws IOException;

  public CDOPackageUnit[] readCDOPackageUnits(CDOPackageRegistry packageRegistry) throws IOException;

  public CDOPackageUnit.Type readCDOPackageUnitType() throws IOException;

  public CDOPackageInfo readCDOPackageInfo() throws IOException;

  public CDOClassifierRef readCDOClassifierRef() throws IOException;

  public EClassifier readCDOClassifierRefAndResolve() throws IOException;

  public String readCDOPackageURI() throws IOException;

  public CDOType readCDOType() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOID readCDOID() throws IOException;

  public CDOIDAndVersion readCDOIDAndVersion() throws IOException;

  public CDOIDMetaRange readCDOIDMetaRange() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDORevision readCDORevision() throws IOException;

  public CDOList readCDOList(CDORevision revision, EStructuralFeature feature) throws IOException;

  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException;

  public CDORevisionDelta readCDORevisionDelta() throws IOException;

  public CDOFeatureDelta readCDOFeatureDelta(EClass eClass) throws IOException;

  /**
   * Read either a CDORevision or a primitive value.
   */
  public Object readCDORevisionOrPrimitive() throws IOException;

  /**
   * Read either a CDORevision, a primitive value or a EClass.
   */
  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public RWLockManager.LockType readCDOLockType() throws IOException;
}
