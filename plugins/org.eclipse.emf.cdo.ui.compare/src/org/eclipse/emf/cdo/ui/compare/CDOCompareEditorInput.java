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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import java.lang.reflect.InvocationTargetException;

/**
 * A CDO-specific implementation of the compare editor input.
 *
 * @author Eike Stepper
 * @since 4.2
 */
@SuppressWarnings("restriction")
public class CDOCompareEditorInput extends CompareEditorInput
{
  private final Comparison comparison;

  private final AdapterFactory adapterFactory;

  protected CDOCompareEditorInput(CompareConfiguration configuration, AdapterFactory adapterFactory)
  {
    super(configuration);
    comparison = (Comparison)configuration
        .getProperty(org.eclipse.emf.compare.ide.ui.internal.EMFCompareConstants.COMPARE_RESULT);
    this.adapterFactory = adapterFactory;
  }

  @Override
  protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
  {
    return adapterFactory.adapt(comparison, IDiffElement.class);
  }

  @Override
  public Viewer createDiffViewer(Composite parent)
  {
    return new org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.EMFCompareStructureMergeViewer(parent,
        getCompareConfiguration());
  }

  public static void openCompareDialog(CDOSession session, CDOBranchPoint left, CDOBranchPoint right)
  {
    CDOView leftView = session.openView(left);
    CDOComparison comparison = CDOCompareUtil.compare(leftView, right);
    IComparisonScope scope = comparison.getScope();

    try
    {
      final CompareConfiguration configuration = new CompareConfiguration();
      configuration.setProperty(org.eclipse.emf.compare.ide.ui.internal.EMFCompareConstants.COMPARE_RESULT, comparison);
      configuration.setProperty(
          org.eclipse.emf.compare.ide.ui.internal.EMFCompareConstants.EDITING_DOMAIN,
          new org.eclipse.emf.compare.ide.ui.internal.util.EMFCompareEditingDomain(comparison, scope.getLeft(), scope
              .getRight(), scope.getOrigin()));

      ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
      ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);

      final CompareEditorInput input = new CDOCompareEditorInput(configuration, adapterFactory);
      input.setTitle("My own compare dialog");

      configuration.setContainer(input);
      CompareUI.openCompareDialog(input);
    }
    finally
    {
      comparison.close();
      leftView.close();
    }
  }
}
