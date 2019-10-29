/*
 * Copyright (c) 2009-2013, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fl�gge - enhancements
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry.CDOObjectMarker;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOObject extends CDOObject, InternalEObject, InternalCDOLoadable, CDOObjectMarker
{
  /**
   * @since 4.2
   */
  public InternalCDOClassInfo cdoClassInfo();

  public InternalCDOView cdoView();

  public InternalCDORevision cdoRevision();

  /**
   * @since 4.3
   */
  public InternalCDORevision cdoRevision(boolean loadOnDemand);

  /**
   * @since 4.8
   */
  public void cdoInternalPreAttach();

  public void cdoInternalPostAttach();

  public void cdoInternalPostDetach(boolean remote);

  public void cdoInternalPostInvalidate();

  /**
   * @since 3.0
   */
  public void cdoInternalPostRollback();

  public void cdoInternalPreCommit();

  public void cdoInternalSetID(CDOID id);

  public void cdoInternalSetView(CDOView view);

  public void cdoInternalSetRevision(CDORevision revision);

  public CDOState cdoInternalSetState(CDOState state);

  public InternalEObject cdoInternalInstance();

  /**
   * @deprecated As of 4.2 no longer supported.
   */
  @Deprecated
  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID);
}
