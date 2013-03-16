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

import org.eclipse.emf.cdo.compare.CDOCompare;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;

/**
 * Static methods to open an EMF Compare dialog.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOCompareEditorUtil
{
  public static boolean openDialog(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    Comparison comparison = CDOCompareUtil.compare(leftView, rightView, originView);

    IComparisonScope scope = CDOCompare.getScope(comparison);
    ICompareEditingDomain editingDomain = EMFCompareEditingDomain.create(scope.getLeft(), scope.getRight(),
        scope.getOrigin());

    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(registry);

    CompareConfiguration configuration = new CompareConfiguration();
    configuration.setLeftEditable(!leftView.isReadOnly());
    configuration.setRightEditable(!rightView.isReadOnly());

    Input input = new Input(configuration, comparison, editingDomain, adapterFactory);
    CompareUI.openCompareDialog(input);
    return input.isOK();
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static final class Input extends org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput
  {
    private boolean ok;

    private Input(CompareConfiguration configuration, Comparison comparison, ICompareEditingDomain editingDomain,
        AdapterFactory adapterFactory)
    {
      super(configuration, comparison, editingDomain, adapterFactory);
      setTitle("Model Comparison");
    }

    public boolean isOK()
    {
      return ok;
    }

    @Override
    public boolean okPressed()
    {
      ok = true;
      return super.okPressed();
    }
  }
}
