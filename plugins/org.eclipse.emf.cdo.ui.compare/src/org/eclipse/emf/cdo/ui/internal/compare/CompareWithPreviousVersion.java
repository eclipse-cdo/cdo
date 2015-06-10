/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.ui.compare.CDOCompareEditorUtil;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class CompareWithPreviousVersion extends CompareActionDelegate<CDOCommitInfo>
{
  public CompareWithPreviousVersion()
  {
    super(CDOCommitInfo.class);
  }

  @Override
  protected void run(List<CDOCommitInfo> targets, IProgressMonitor progressMonitor)
  {
    if (targets.size() == 1)
    {
      CDOCommitInfo commitInfo = targets.get(0);
      CDOCompareEditorUtil.openEditor(commitInfo, true);
    }
  }
}
