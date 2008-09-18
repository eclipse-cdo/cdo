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
package org.eclipse.emf.cdo.common;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOClassRef;
import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;

import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public interface CDODataOutput extends ExtendedDataOutput
{
  public CDOIDProvider getIDProvider();

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOPackageURI(String uri) throws IOException;

  public void writeCDOType(CDOType cdoType) throws IOException;

  public void writeCDOClassRef(CDOClassRef cdoClassRef) throws IOException;

  public void writeCDOClassRef(CDOClass cdoClass) throws IOException;

  public void writeCDOPackage(CDOPackage cdoPackage) throws IOException;

  public void writeCDOClass(CDOClass cdoClass) throws IOException;

  public void writeCDOFeature(CDOFeature cdoFeature) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDOID(CDOID id) throws IOException;

  public void writeCDOIDAndVersion(CDOIDAndVersion idAndVersion) throws IOException;

  public void writeCDOIDMetaRange(CDOIDMetaRange metaRange) throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public void writeCDORevision(CDORevision revision, int referenceChunk) throws IOException;

  public void writeCDORevisionDelta(CDORevisionDelta revisionDelta) throws IOException;

  public void writeCDOFeatureDelta(CDOFeatureDelta featureDelta, CDOClass cdoClass) throws IOException;

  /**
   * Write either a CDORevision or a primitive value.
   */
  public void writeCDORevisionOrPrimitive(Object value) throws IOException;

  /**
   * Write either a CDORevision, a primitive value or a CDOClass.
   */
  public void writeCDORevisionOrPrimitiveOrClass(Object value) throws IOException;
}
