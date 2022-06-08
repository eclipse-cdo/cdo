/*
 * Copyright (c) 2015, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CompareWithEachOther extends CompareActionDelegate<CDOCommitInfo>
{
  public CompareWithEachOther()
  {
    super(CDOCommitInfo.class);
  }

  @Override
  protected void run(List<CDOCommitInfo> targets, IProgressMonitor progressMonitor)
  {
    if (targets.size() == 2)
    {
      CDOCommitInfo commitInfo0 = targets.get(0);
      CDOCommitInfo commitInfo1 = targets.get(1);

      if (CDOCommonUtil.compareTimeStamps(commitInfo0.getTimeStamp(), commitInfo1.getTimeStamp()) > 0)
      {
        CDOCommitInfo tmp = commitInfo0;
        commitInfo0 = commitInfo1;
        commitInfo1 = tmp;
      }

      CDOCompareEditorUtil.openEditor(commitInfo0, commitInfo1, true);
    }
  }
}
