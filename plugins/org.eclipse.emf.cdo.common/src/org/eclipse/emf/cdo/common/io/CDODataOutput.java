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
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDODataOutput extends ExtendedDataOutput
{
  public CDOIDProvider getIDProvider();

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOPackageUnit(CDOPackageUnit packageUnit, boolean withPackages) throws IOException;

  public void writeCDOPackageUnits(CDOPackageUnit... packageUnit) throws IOException;

  public void writeCDOPackageUnitType(CDOPackageUnit.Type type) throws IOException;

  public void writeCDOPackageInfo(CDOPackageInfo packageInfo) throws IOException;

  public void writeCDOClassifierRef(CDOClassifierRef eClassifierRef) throws IOException;

  public void writeCDOClassifierRef(EClassifier eClassifier) throws IOException;

  public void writeCDOPackageURI(String uri) throws IOException;

  public void writeCDOType(CDOType cdoType) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOID(CDOID id) throws IOException;

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException;

  public void writeCDOIDMetaRange(CDOIDMetaRange metaRange) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException;

  public void writeCDOList(CDOList list, EStructuralFeature feature, int referenceChunk) throws IOException;

  public void writeCDOFeatureValue(Object value, EStructuralFeature feature) throws IOException;

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException;

  public void writeCDOFeatureDelta(CDOFeatureDelta featureDelta, EClass eClass) throws IOException;

  /**
   * Write either a CDORevision or a primitive value.
   */
  public void writeCDORevisionOrPrimitive(Object value) throws IOException;

  /**
   * Write either a CDORevision, a primitive value or a EClass.
   */
  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOLockType(RWLockManager.LockType lockType) throws IOException;
}
