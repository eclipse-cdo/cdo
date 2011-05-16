/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 * 		Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDODataOutput extends ExtendedDataOutput
{
  public CDOPackageRegistry getPackageRegistry();

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

  public void writeCDOBranch(CDOBranch branch) throws IOException;

  public void writeCDOBranchPoint(CDOBranchPoint branchPoint) throws IOException;

  public void writeCDOBranchVersion(CDOBranchVersion branchVersion) throws IOException;

  public void writeCDOChangeSetData(CDOChangeSetData changeSetData) throws IOException;

  public void writeCDOCommitData(CDOCommitData commitData) throws IOException;

  public void writeCDOCommitInfo(CDOCommitInfo commitInfo) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOID(CDOID id) throws IOException;

  /**
   * @since 4.0
   */
  public void writeCDOIDReference(CDOIDReference idReference) throws IOException;

  /**
   * @since 4.0
   */
  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException;

  /**
   * @since 4.0
   */
  public void writeCDOIDAndBranch(CDOIDAndBranch idAndBranch) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDORevisionKey(CDORevisionKey revisionKey) throws IOException;

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException;

  /**
   * @since 4.0
   */
  public void writeCDORevisable(CDORevisable revisable) throws IOException;

  public void writeCDOList(EClass owner, EStructuralFeature feature, CDOList list, int referenceChunk)
      throws IOException;

  public void writeCDOFeatureValue(EStructuralFeature feature, Object value) throws IOException;

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException;

  public void writeCDOFeatureDelta(EClass owner, CDOFeatureDelta featureDelta) throws IOException;

  /**
   * Write either a CDORevision or a primitive value.
   */
  public void writeCDORevisionOrPrimitive(Object value) throws IOException;

  /**
   * Write either a CDORevision, a primitive value or a EClass.
   */
  public void writeCDORevisionOrPrimitiveOrClassifier(Object value) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOLockType(LockType lockType) throws IOException;
}
