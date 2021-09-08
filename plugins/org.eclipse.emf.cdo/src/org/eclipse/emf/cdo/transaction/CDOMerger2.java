/*
 * Copyright (c) 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Set;

/**
 * Merges the changes between a local {@link CDOTransaction transaction} and a remote {@link CDOView view}
 * into the local transaction. Only the changes of a set of affected {@link CDOObject objects},
 * specified by the set of their {@link CDOID ids}, is considered.
 *
 * @author Eike Stepper
 * @since 4.5
 */
public interface CDOMerger2 extends CDOMerger
{
  /**
   * @noreference This method is not intended to be referenced by clients.
   */
  @Override
  @Deprecated
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws UnsupportedOperationException;

  public void merge(CDOTransaction localTransaction, CDOView remoteView, Set<CDOID> affectedIDs) throws ConflictException;
}
