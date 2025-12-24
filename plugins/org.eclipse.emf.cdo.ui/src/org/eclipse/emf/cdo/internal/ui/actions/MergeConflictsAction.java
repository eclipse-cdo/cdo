/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.ui.actions.SafeAction;

import org.eclipse.emf.spi.cdo.CDOMergingConflictResolver;

/**
 * @author Eike Stepper
 */
public class MergeConflictsAction extends SafeAction
{
  private static final String TITLE = Messages.getString("MergeConflictsAction.0"); //$NON-NLS-1$

  private static final String TOOL_TIP = Messages.getString("MergeConflictsAction.1"); //$NON-NLS-1$

  private final CDOTransaction transaction;

  public MergeConflictsAction(CDOTransaction transaction)
  {
    super(TITLE, AS_CHECK_BOX);
    setToolTipText(TOOL_TIP);

    this.transaction = transaction;
    for (CDOConflictResolver conflictResolver : transaction.options().getConflictResolvers())
    {
      setEnabled(false);
      if (conflictResolver instanceof CDOMergingConflictResolver)
      {
        setEnabled(true);
        setChecked(true);
        return;
      }
    }
  }

  @Override
  protected void safeRun() throws Exception
  {
    for (CDOConflictResolver conflictResolver : transaction.options().getConflictResolvers())
    {
      if (conflictResolver instanceof CDOMergingConflictResolver)
      {
        transaction.options().removeConflictResolver(conflictResolver);
        return;
      }
    }

    CDOMergingConflictResolver conflictResolver = new CDOMergingConflictResolver();
    transaction.options().addConflictResolver(conflictResolver);
  }
}
