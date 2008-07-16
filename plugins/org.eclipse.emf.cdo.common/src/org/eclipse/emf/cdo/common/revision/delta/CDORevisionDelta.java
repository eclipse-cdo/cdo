/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 **************************************************************************/
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public interface CDORevisionDelta
{
  public CDOID getID();

  public int getOriginVersion();

  public int getDirtyVersion();

  public List<CDOFeatureDelta> getFeatureDeltas();

  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);

  public void write(ExtendedDataOutput out, CDOIDProvider idProvider) throws IOException;
}
