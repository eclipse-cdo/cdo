/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.net4j.ui.DelegatingContentProvider;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SelectionStep<E> extends ValueStep<List<E>>
{
  private Class elementClass;

  private Object input;

  private IStructuredContentProvider contentProvider;

  private ILabelProvider labelProvider;

  private int min;

  private int max;

  public SelectionStep(String label, String key, Class elementClass, Object input,
      IStructuredContentProvider contentProvider, ILabelProvider labelProvider, int min, int max)
  {
    super(label, key, List.class);
    this.elementClass = elementClass;
    this.input = input;
    this.contentProvider = contentProvider;
    this.labelProvider = labelProvider == null ? new LabelProvider() : labelProvider;
    this.min = min;
    this.max = max;

    if (min < 0)
    {
      throw new IllegalArgumentException("min < 0");
    }

    if (max < min)
    {
      throw new IllegalArgumentException("max < min");
    }
  }

  public SelectionStep(String key, Class elementClass, Object input, IStructuredContentProvider contentProvider,
      ILabelProvider labelProvider, int min, int max)
  {
    this(key, key, elementClass, input, contentProvider, labelProvider, min, max);
  }

  public final IStructuredContentProvider getContentProvider()
  {
    return contentProvider;
  }

  public final ILabelProvider getLabelProvider()
  {
    return labelProvider;
  }

  public final int getMin()
  {
    return min;
  }

  public final int getMax()
  {
    return max;
  }

  public final Set<E> getContents()
  {
    Set<E> result = new HashSet();
    if (contentProvider instanceof ITreeContentProvider)
    {
      addElements(((ITreeContentProvider)contentProvider).getChildren(input), result);
    }
    else
    {
      addElements(contentProvider.getElements(input), result);
    }

    return result;
  }

  @Override
  protected String validateValue(List<E> values)
  {
    int size = values == null ? 0 : values.size();
    if (size < min)
    {
      int delta = min - size;
      String count = delta == 1 ? " one" : " " + delta;
      String more = size > 0 ? " more" : " ";
      return "Select" + count + more + getLabel() + " element" + (delta == 1 ? "" : "s");
    }

    if (size > max)
    {
      int delta = size - max;
      String count = delta == 1 ? " one" : " " + delta;
      return "Unselect" + count + " " + getLabel() + " element" + (delta == 1 ? "" : "s");
    }

    // Set<E> contents = getContents();
    // for (E element : values)
    // {
    // if (!contents.contains(element))
    // {
    // return false;
    // }
    // }

    return null;
  }

  public boolean isOptional()
  {
    return min == 0;
  }

  public boolean isMulti()
  {
    return max > 1;
  }

  @Override
  protected Control createControl(Composite parent)
  {
    StructuredViewer viewer;
    boolean grabVertical = true;
    if (contentProvider instanceof ITreeContentProvider)
    {
      if (isMulti())
      {
        viewer = createMultiSelectionTree(parent);
      }
      else
      {
        viewer = new TreeViewer(parent, SWT.NONE);
        prepareViewer(viewer);
      }
    }
    else
    {
      if (isMulti())
      {
        viewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
        prepareViewer(viewer);
      }
      else
      {
        viewer = createSingleSelectionList(parent);
        grabVertical = false;
      }
    }

    Control control = viewer.getControl();
    control.setLayoutData(createLayoutData(control, grabVertical));
    return viewer.getControl();
  }

  protected ComboViewer createSingleSelectionList(Composite parent)
  {
    ComboViewer viewer = new ComboViewer(parent, SWT.NONE);
    prepareViewer(viewer);
    viewer.getCombo().addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent e)
      {
        setValue(null);
      }
    });

    // ComboAutoCompleter.attach(viewer, new ComboAutoCompleter(viewer)
    // {
    // @Override
    // protected void setSelection(StructuredSelection selection)
    // {
    // super.setSelection(selection);
    // setSingleSelection(selection);
    // }
    // });

    viewer.addPostSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        setSingleSelection((IStructuredSelection)event.getSelection());
      }
    });

    return viewer;
  }

  protected CheckboxTreeViewer createMultiSelectionTree(Composite parent)
  {
    CheckboxTreeViewer viewer = new CheckboxTreeViewer(parent, SWT.NONE);
    prepareViewer(viewer);
    viewer.addCheckStateListener(new ICheckStateListener()
    {
      public void checkStateChanged(CheckStateChangedEvent event)
      {
      }
    });

    return viewer;
  }

  protected void prepareViewer(StructuredViewer viewer)
  {
    // Following order is important!
    viewer.setContentProvider(isOptional() ? new DelegatingContentProvider(contentProvider, true) : contentProvider);
    viewer.setInput(input);
    // viewer.setSorter(new ViewerSorter());
    viewer.setLabelProvider(labelProvider);

    List<E> value = getValue();
    if (value != null && !value.isEmpty())
    {
      viewer.setSelection(new StructuredSelection(value));
    }
  }

  protected GridData createLayoutData(Control control, boolean grabVertical)
  {
    GridData gd = new GridData(SWT.FILL, grabVertical ? SWT.FILL : SWT.TOP, true, grabVertical);
    if (!grabVertical)
    {
      gd.horizontalAlignment = SWT.LEFT;
      gd.grabExcessHorizontalSpace = false;
    }

    return gd;
  }

  private void addElements(Object[] elements, Set<E> result)
  {
    for (Object element : elements)
    {
      addElement(element, result);
      if (contentProvider instanceof ITreeContentProvider)
      {
        addElements(((ITreeContentProvider)contentProvider).getChildren(element), result);
      }
    }
  }

  private void addElement(Object element, Set<E> result)
  {
    if (elementClass.isInstance(element))
    {
      result.add((E)element);
    }
  }

  private void setSingleSelection(IStructuredSelection selection)
  {
    Object element = selection.getFirstElement();
    if (element instanceof String && ((String)element).length() == 0)
    {
      setValue(null);
    }
    else if (elementClass.isInstance(element))
    {
      setValue((List<E>)Collections.singletonList(element));
    }
    else
    {
      setValue(null);
    }
  }
}
