/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.editors.impl;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Martin Fluegge
 */
public abstract class DawnAbstractEditorSupport implements IDawnEditorSupport
{
  private final IDawnEditor editor;

  private CDOView view;

  private boolean dirty;

  public CDOView getView()
  {
    return view;
  }

  public void setView(CDOView view)
  {
    this.view = view;
  }

  public DawnAbstractEditorSupport(IDawnEditor editor)
  {
    this.editor = editor;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public IDawnEditor getEditor()
  {
    return editor;
  }

  /**
   * @since 1.0
   */
  public void rollback()
  {
    CDOView view = getEditor().getView();

    if (view != null && view instanceof CDOTransaction)
    {
      ((CDOTransaction)view).rollback();
    }
  }
}
