/*
 * Copyright (c) 2015, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl.OptionsImpl;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * An interface to control if {@link CDOLockState lock states} are loaded when {@link CDORevision revisions} are loaded to limit requests sent to server. This interface is to be used when {@link OptionsImpl#setLockStatePrefetchEnabled(boolean) lock state prefetch view option} is enabled.
 * <br/>
 * <br/>
 * Note that lock states will not be loaded automatically for {@link CDOResource} when being created through {@link ResourceSet#getResource(org.eclipse.emf.common.util.URI, boolean)}, {@link CDOResource#cdoLockState()} must be called explicitly to load it.
 * @see OptionsImpl#setLockStatePrefetchEnabled(boolean)
 * @author Esteban Dugueperoux
 * @since 4.4
 * @deprecated As of 4.12 use {@link CDOLockStatePrefetcher#setObjectFilter(java.util.function.Predicate)}.
 */
@Deprecated
@FunctionalInterface
public interface CDOLockStateLoadingPolicy
{
  /**
   * Tell if the {@link CDOLockState lock state} must be loaded for the current {@link CDOView view} when the {@link CDORevision revision} corresponding to the specified {@link CDOID id} is loaded.
   *
   * @param id the of the loaded revision
   * @return true to have lock state loaded for the specified revision's id for the current view
   */
  @Deprecated
  public boolean loadLockState(CDOID id);
}
