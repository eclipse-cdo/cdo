/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.filters;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Victor Roldan Betancort
 */
public class CDOStateFilter extends CDOObjectFilter
{
  private CDOState stateFilter;

  public CDOStateFilter(StructuredViewer viewer)
  {
    super(viewer);
  }

  @Override
  protected void parsePattern(String pattern)
  {
    if (pattern.compareToIgnoreCase("dirty") == 0)
    {
      stateFilter = CDOState.DIRTY;
    }
    else if (pattern.compareToIgnoreCase("clean") == 0)
    {
      stateFilter = CDOState.CLEAN;
    }
    else if (pattern.compareToIgnoreCase("new") == 0)
    {
      stateFilter = CDOState.NEW;
    }
    else if (pattern.compareToIgnoreCase("transient") == 0)
    {
      stateFilter = CDOState.TRANSIENT;
    }
    else if (pattern.compareToIgnoreCase("conflict") == 0)
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
      return ((CDOObject)element).cdoState() == stateFilter;
    }
    return true;
  }

  @Override
  public String getDescription()
  {
    return "Specify a state to filter: dirty | clean | transient | new | conflict";
  }

  @Override
  public String getTitle()
  {
    return "CDOState filter";
  }
}
