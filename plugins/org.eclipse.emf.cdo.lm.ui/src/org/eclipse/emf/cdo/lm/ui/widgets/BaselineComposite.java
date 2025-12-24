/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.widgets;

import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class BaselineComposite extends Composite
{
  private static BaselineSelectorContributor[] selectors = { new BaselineSelectorContributor()
  {
    @Override
    public boolean contributeBaselineSelector(BaselineComposite composite, Object context)
    {
      Button button = new Button(composite, SWT.PUSH);
      button.setLayoutData(GridDataFactory.fillDefaults().create());
      button.setText("Latest");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          EList<Baseline> choiceOfValues = composite.getChoiceOfValues();
          composite.setBaseline(choiceOfValues.isEmpty() ? null : choiceOfValues.get(0));
        }
      });
  
      return true;
    }
  } };

  private ConcurrentArray<ModifyListener> listeners = new ConcurrentArray<>()
  {
    @Override
    protected ModifyListener[] newArray(int length)
    {
      return new ModifyListener[length];
    }
  };

  private final EList<Baseline> choiceOfValues;

  private Baseline baseline;

  private boolean settingValue;

  private ComboViewer viewer;

  public BaselineComposite(Composite parent, int style, EList<Baseline> choiceOfValues, Object context)
  {
    super(parent, style);

    this.choiceOfValues = new BasicEList<>(choiceOfValues);
    this.choiceOfValues.sort(Baseline.COMPARATOR);

    viewer = new ComboViewer(this, SWT.BORDER | SWT.SINGLE);
    viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());
    viewer.setLabelProvider(new LabelProvider()
    {
      @Override
      public String getText(Object element)
      {
        return BaselineComposite.this.getText((Baseline)element);
      }
    });
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setInput(this.choiceOfValues);
    viewer.addSelectionChangedListener(event -> {
      if (!settingValue)
      {
        Baseline oldBaseline = baseline;
        baseline = (Baseline)event.getStructuredSelection().getFirstElement();
        notifyModifyListeners(oldBaseline);
      }
    });

    int columns = 1;
    if (context != null)
    {
      for (int i = 0; i < selectors.length; i++)
      {
        BaselineSelectorContributor selector = selectors[i];
        if (selector.contributeBaselineSelector(this, context))
        {
          ++columns;
        }
      }
    }

    setLayout(GridLayoutFactory.fillDefaults().numColumns(columns).create());
  }

  public EList<Baseline> getChoiceOfValues()
  {
    return choiceOfValues;
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    UIUtil.forEachChild(this, c -> c.setEnabled(enabled));
  }

  public void addModifyListener(ModifyListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.add(listener);
  }

  public void removeModifyListener(ModifyListener listener)
  {
    CheckUtil.checkArg(listener, "listener"); //$NON-NLS-1$
    listeners.remove(listener);
  }

  public Baseline getBaseline()
  {
    return baseline;
  }

  public void setBaseline(Baseline newBaseline)
  {
    if (!Objects.equals(baseline, newBaseline))
    {
      Baseline oldBaseline = baseline;
      baseline = newBaseline;

      if (!viewer.getControl().isDisposed())
      {
        try
        {
          settingValue = true;
          viewer.setSelection(new StructuredSelection(baseline));
        }
        finally
        {
          settingValue = false;
        }
      }

      notifyModifyListeners(oldBaseline);
    }
  }

  protected String getText(Baseline baseline)
  {
    return baseline.getTypeAndName();
  }

  private void notifyModifyListeners(Baseline oldValue)
  {
    if (!Objects.equals(baseline, oldValue))
    {
      ModifyListener[] array = listeners.get();

      for (int i = 0; i < array.length; i++)
      {
        try
        {
          ModifyListener listener = array[i];
          if (listener != null)
          {
            listener.modifyBaseline(this, baseline);
          }
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface ModifyListener
  {
    public void modifyBaseline(BaselineComposite control, Baseline baseline);
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface BaselineSelectorContributor
  {
    public boolean contributeBaselineSelector(BaselineComposite composite, Object context);
  }
}
