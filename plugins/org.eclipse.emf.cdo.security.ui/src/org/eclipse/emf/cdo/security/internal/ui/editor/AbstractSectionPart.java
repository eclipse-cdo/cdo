/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Common framework for a part of a form page that is an SWT {@link Section}
 * presenting some object, list/table of objects, or attributes of an object.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public abstract class AbstractSectionPart<T extends EObject> extends AbstractFormPart
{
  private final EditingDomain domain;

  private final AdapterFactory adapterFactory;

  private final Class<T> inputType;

  private final EClass inputEClass;

  private T input;

  private DataBindingContext context;

  private WritableValue<Object> value;

  private IActionBars editorActionBars;

  public AbstractSectionPart(Class<T> inputType, EClass inputEClass, EditingDomain domain, AdapterFactory adapterFactory)
  {
    this.inputType = inputType;
    this.inputEClass = inputEClass;
    this.domain = domain;
    this.adapterFactory = adapterFactory;
  }

  @Override
  public void initialize(IManagedForm form)
  {
    super.initialize(form);
    initDatabindings();
  }

  public void setEditorActionBars(IActionBars actionBars)
  {
    this.editorActionBars = actionBars;
  }

  protected IActionBars getEditorActionBars()
  {
    return editorActionBars;
  }

  protected void initDatabindings()
  {
    context = new EMFDataBindingContext();
    value = new WritableValue<Object>(context.getValidationRealm());
  }

  @Override
  public void dispose()
  {
    if (context != null)
    {
      context.dispose();
    }

    super.dispose();
  }

  @Override
  public boolean setFormInput(Object input)
  {
    boolean result = false;
    this.input = null;

    if (inputType.isInstance(input))
    {
      this.input = inputType.cast(input);
      result = true;
    }

    value.setValue(this.input);
    return result;
  }

  public void selectionChanged(IFormPart part, ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection sel = (IStructuredSelection)selection;
      if (!sel.isEmpty())
      {
        setFormInput(sel.getFirstElement());
      }
    }
  }

  public final Class<T> getInputType()
  {
    return inputType;
  }

  public final EClass getInputEClass()
  {
    return inputEClass;
  }

  protected T getInput()
  {
    return input;
  }

  protected EditingDomain getEditingDomain()
  {
    return domain;
  }

  protected AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  protected Realm getRealm()
  {
    return getContext().getValidationRealm();
  }

  protected DataBindingContext getContext()
  {
    return context;
  }

  protected IObservableValue<Object> getValue()
  {
    return value;
  }

  protected ISWTObservableValue<?> observeText(Text text)
  {
    return WidgetProperties.text(new int[] { SWT.DefaultSelection, SWT.FocusOut }).observe(text);
  }

  public void createContents(Composite parent)
  {
    FormToolkit toolkit = getManagedForm().getToolkit();

    Section section = toolkit.createSection(parent, Section.SHORT_TITLE_BAR);
    section.setText(getTitle());

    if (parent.getLayout() instanceof TableWrapLayout)
    {
      section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
    }
    else if (parent.getLayout() instanceof GridLayout)
    {
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    Composite body = toolkit.createComposite(section);
    section.setClient(body);

    createContents(body, toolkit);
    createActionToolbar(section, toolkit);
  }

  protected abstract String getTitle();

  protected abstract void createContents(Composite parent, FormToolkit toolkit);

  protected void createActionToolbar(Section section, FormToolkit toolkit)
  {
    // Pass
  }
}
