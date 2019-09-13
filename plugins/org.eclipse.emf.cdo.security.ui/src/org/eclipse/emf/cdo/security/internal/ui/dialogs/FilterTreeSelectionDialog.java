/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.dialogs;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * A tree selection dialog that offers the user a filter field.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class FilterTreeSelectionDialog extends ElementTreeSelectionDialog
{
  private PatternFilter filter = new PatternFilter();

  public FilterTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider)
  {
    super(parent, labelProvider, contentProvider);
  }

  @Override
  protected TreeViewer doCreateTreeViewer(Composite parent, int style)
  {
    @SuppressWarnings("deprecation")
    FilteredTree tree = new FilteredTree(parent, style, filter, true);
    tree.setLayoutData(new GridData(GridData.FILL_BOTH));
    tree.setQuickSelectionMode(false);

    applyDialogFont(tree);
    return tree.getViewer();
  }
}
