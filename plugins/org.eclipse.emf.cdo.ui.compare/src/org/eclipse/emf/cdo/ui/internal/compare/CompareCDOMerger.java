/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.transaction.CDOMerger2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CompareCDOMerger implements CDOMerger2
{
  public CompareCDOMerger()
  {
  }

  @Override
  @Deprecated
  public CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void merge(final CDOTransaction localTransaction, CDOView remoteView, Set<CDOID> affectedIDs) throws ConflictException
  {
    CDOCompareEditorUtil.closeTransactionAfterCommit(localTransaction);
    CDOCompareEditorUtil.closeEditorWithTransaction(localTransaction);
    CDOCompareEditorUtil.openEditor(remoteView, localTransaction, affectedIDs, true);
  }
}
