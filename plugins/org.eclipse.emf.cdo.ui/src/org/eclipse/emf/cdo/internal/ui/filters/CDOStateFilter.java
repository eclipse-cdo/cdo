/*
 * Copyright (c) 2008-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.filters;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import java.text.MessageFormat;

/**
 * @author Victor Roldan Betancort
 */
public class CDOStateFilter extends CDOObjectFilter
{
  private static final String STATE_CONFLICT = Messages.getString("CDOStateFilter.0"); //$NON-NLS-1$

  private static final String STATE_TRANSIENT = Messages.getString("CDOStateFilter.1"); //$NON-NLS-1$

  private static final String STATE_NEW = Messages.getString("CDOStateFilter.2"); //$NON-NLS-1$

  private static final String STATE_CLEAN = Messages.getString("CDOStateFilter.3"); //$NON-NLS-1$

  private static final String STATE_DIRTY = Messages.getString("CDOStateFilter.4"); //$NON-NLS-1$

  private CDOState stateFilter;

  public CDOStateFilter(StructuredViewer viewer)
  {
    super(viewer);
  }

  @Override
  protected void parsePattern(String pattern)
  {
    if (pattern.compareToIgnoreCase(STATE_DIRTY) == 0)
    {
      stateFilter = CDOState.DIRTY;
    }
    else if (pattern.compareToIgnoreCase(STATE_CLEAN) == 0)
    {
      stateFilter = CDOState.CLEAN;
    }
    else if (pattern.compareToIgnoreCase(STATE_NEW) == 0)
    {
      stateFilter = CDOState.NEW;
    }
    else if (pattern.compareToIgnoreCase(STATE_TRANSIENT) == 0)
    {
      stateFilter = CDOState.TRANSIENT;
    }
    else if (pattern.compareToIgnoreCase(STATE_CONFLICT) == 0)
    {
      stateFilter = CDOState.CONFLICT;
    }
    else
    {
      stateFilter = null;
    }
  }

  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element)
  {
    if (stateFilter != null)
    {
      return CDOUtil.getCDOObject((EObject)element).cdoState() == stateFilter;
    }

    return true;
  }

  @Override
  public String getDescription()
  {
    return MessageFormat.format(Messages.getString("CDOStateFilter.5"), STATE_DIRTY, STATE_CLEAN, //$NON-NLS-1$
        STATE_TRANSIENT, STATE_NEW, STATE_CONFLICT);
  }

  @Override
  public String getTitle()
  {
    return Messages.getString("CDOStateFilter.6"); //$NON-NLS-1$
  }
}
