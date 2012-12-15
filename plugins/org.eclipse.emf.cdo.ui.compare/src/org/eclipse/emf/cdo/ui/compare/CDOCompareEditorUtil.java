/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.compare;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.compare.CDOComparison;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;

/**
 * A CDO-specific implementation of the compare editor input.
 *
 * @author Eike Stepper
 * @since 4.2
 */
@SuppressWarnings("restriction")
public class CDOCompareEditorUtil
{
  public static void openCompareDialog(CDOSession session, CDOBranchPoint left, CDOBranchPoint right)
  {
    CDOView leftView = session.openView(left);
    CDOComparison comparison = CDOCompareUtil.compare(leftView, right);

    IComparisonScope scope = comparison.getScope();

    try
    {
      ICompareEditingDomain editingDomain = EMFCompareEditingDomain.create(scope.getLeft(), scope.getRight(),
          scope.getOrigin());

      ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
      ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);

      CompareConfiguration configuration = new CompareConfiguration();
      ComparisonEditorInput input = new ComparisonEditorInput(configuration, comparison, editingDomain, adapterFactory);
      input.setTitle("Model Comparison");

      CompareUI.openCompareDialog(input);
    }
    finally
    {
      comparison.close();
      leftView.close();
    }
  }
}
