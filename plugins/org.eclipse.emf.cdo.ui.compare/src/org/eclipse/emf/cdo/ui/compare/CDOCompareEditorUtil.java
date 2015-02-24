/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.compare.CDOCompare;
import org.eclipse.emf.cdo.compare.CDOCompareUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.domain.ICompareEditingDomain;
import org.eclipse.emf.compare.domain.impl.EMFCompareEditingDomain;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * Static methods to open an EMF Compare dialog.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOCompareEditorUtil
{
  private static final ThreadLocal<Boolean> ACTIVATE_EDITOR = new ThreadLocal<Boolean>();

  private static final ThreadLocal<Runnable> DISPOSE_RUNNABLE = new ThreadLocal<Runnable>();

  public static boolean openDialog(CDOSession session, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint)
  {
    final Boolean activateEditor = ACTIVATE_EDITOR.get();
    final CDOView[] leftAndRightView = { null, null };
    final Runnable disposeRunnable = new Runnable()
    {
      public void run()
      {
        LifecycleUtil.deactivate(leftAndRightView[0]);
        LifecycleUtil.deactivate(leftAndRightView[1]);
      }
    };

    try
    {
      leftAndRightView[0] = session.openView(leftPoint);
      leftAndRightView[1] = session.openView(rightPoint);

      if (activateEditor != null)
      {
        DISPOSE_RUNNABLE.set(disposeRunnable);
      }

      return openDialog(leftAndRightView[0], leftAndRightView[1], null);
    }
    finally
    {
      if (activateEditor == null)
      {
        disposeRunnable.run();
      }
    }
  }

  public static boolean openDialog(CDOCommitInfo leftCommitInfo, CDOBranchPoint rightPoint)
  {
    CDORepositoryInfo repositoryInfo = (CDORepositoryInfo)leftCommitInfo.getCommitInfoManager().getRepository();
    CDOSession session = repositoryInfo.getSession();

    return openDialog(session, leftCommitInfo, rightPoint);
  }

  public static boolean openDialog(CDOCommitInfo commitInfo)
  {
    long previousTimeStamp = commitInfo.getPreviousTimeStamp();
    if (previousTimeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return false;
    }

    CDOBranchPoint previous = CDOBranchUtil.normalizeBranchPoint(commitInfo.getBranch(), previousTimeStamp);
    return openDialog(commitInfo, previous);
  }

  public static boolean openDialog(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    final Input input = createComparisonInput(leftView, rightView, originView);

    final Boolean activateEditor = ACTIVATE_EDITOR.get();
    if (activateEditor != null)
    {
      input.setDisposeRunnable(DISPOSE_RUNNABLE.get());
      DISPOSE_RUNNABLE.remove();

      UIUtil.getDisplay().asyncExec(new Runnable()
      {
        public void run()
        {
          CompareUI.openCompareEditor(input, activateEditor);
        }
      });
    }
    else
    {
      CompareUI.openCompareDialog(input);
    }

    return input.isOK();
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOSession session, CDOBranchPoint leftPoint, CDOBranchPoint rightPoint,
      boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(session, leftPoint, rightPoint);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOCommitInfo leftCommitInfo, CDOBranchPoint rightPoint, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(leftCommitInfo, rightPoint);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOCommitInfo commitInfo, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(commitInfo);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static boolean openEditor(CDOView leftView, CDOView rightView, CDOView[] originView, boolean activate)
  {
    ACTIVATE_EDITOR.set(activate);

    try
    {
      return openDialog(leftView, rightView, originView);
    }
    finally
    {
      ACTIVATE_EDITOR.remove();
    }
  }

  /**
   * @since 4.3
   */
  public static Input createComparisonInput(CDOView leftView, CDOView rightView, CDOView[] originView)
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

    return new Input(configuration, comparison, editingDomain, adapterFactory);
  }

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  private static final class Input extends org.eclipse.emf.compare.ide.ui.internal.editor.ComparisonEditorInput
  {
    private Runnable disposeRunnable;

    private boolean ok;

    private Input(CompareConfiguration configuration, Comparison comparison, ICompareEditingDomain editingDomain,
        AdapterFactory adapterFactory)
    {
      super(new org.eclipse.emf.compare.ide.ui.internal.configuration.EMFCompareConfiguration(configuration),
          comparison, editingDomain, adapterFactory);
      setTitle("CDO Model Comparison");
    }

    public void setDisposeRunnable(Runnable disposeRunnable)
    {
      this.disposeRunnable = disposeRunnable;
    }

    public boolean isOK()
    {
      return ok;
    }

    @Override
    public boolean okPressed()
    {
      try
      {
        ok = true;
        return super.okPressed();
      }
      finally
      {
        dispose();
      }
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener listener)
    {
      try
      {
        super.removePropertyChangeListener(listener);
      }
      finally
      {
        dispose();
      }
    }

    private void dispose()
    {
      AdapterFactory adapterFactory = getAdapterFactory();
      if (adapterFactory instanceof ComposedAdapterFactory)
      {
        ComposedAdapterFactory composedAdapterFactory = (ComposedAdapterFactory)adapterFactory;
        composedAdapterFactory.dispose();
      }

      if (disposeRunnable != null)
      {
        disposeRunnable.run();
        disposeRunnable = null;
      }
    }
  }
}
