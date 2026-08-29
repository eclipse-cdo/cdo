/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * Extends {@link CDOMerger} with support for merge operations whose target and source change sets may have
 * different base revisions.
 * <p>
 * A base-aware merger receives separate revision providers for the target and source bases from which the
 * respective change sets were produced, as well as a result base against which the merged change set is to be
 * expressed. This is particularly important for re-merge scenarios where a revision or feature value may have
 * been visible to one side but not to the other.
 *
 * @author Eike Stepper
 * @since 4.30
 */
public interface CDOMergerBaseAware extends CDOMerger
{
  /**
   * Merges the given target and source change sets using their respective base revisions.
   * <p>
   * {@code targetBaseProvider} and {@code sourceBaseProvider} define the causal base states in whose coordinate
   * systems the target and source changes must be interpreted. They may represent different branch points.
   * {@code resultBaseProvider} defines the state against which the returned {@link CDOChangeSetData} is expressed.
   * <p>
   * In particular, the absence of an object or feature value from one side's base does not by itself imply that
   * the side removed it; the side may simply never have observed it.
   *
   * @param target
   *          the target change set.
   * @param source
   *          the source change set.
   * @param targetBaseProvider
   *          provides revisions from the base of the target change set.
   * @param sourceBaseProvider
   *          provides revisions from the base of the source change set.
   * @param resultBaseProvider
   *          provides revisions from the base against which the merge result is expressed.
   * @return the merged change set.
   */
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source, //
      CDORevisionProvider targetBaseProvider, //
      CDORevisionProvider sourceBaseProvider, //
      CDORevisionProvider resultBaseProvider) throws ConflictException;
}
