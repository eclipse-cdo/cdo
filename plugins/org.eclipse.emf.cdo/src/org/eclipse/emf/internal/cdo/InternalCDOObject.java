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
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 */
public interface InternalCDOObject extends CDOObject, InternalEObject
{
  public void cdoInternalPostLoad();

  public void cdoInternalPostAttach();

  public void cdoInternalPostDetach();

  public void cdoInternalPreCommit();

  public void cdoInternalSetID(CDOID id);

  public void cdoInternalSetResource(CDOResource resource);

  public void cdoInternalSetView(CDOView view);

  public void cdoInternalSetRevision(CDORevision revision);

  public CDOState cdoInternalSetState(CDOState state);

  public InternalEObject cdoInternalInstance();

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID);
}
