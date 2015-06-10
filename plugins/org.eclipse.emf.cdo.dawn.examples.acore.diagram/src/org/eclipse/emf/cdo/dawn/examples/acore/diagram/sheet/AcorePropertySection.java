/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.sheet;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.properties.sections.AdvancedPropertySection;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @generated
 */
public class AcorePropertySection extends AdvancedPropertySection implements IPropertySourceProvider
{

  /**
   * @generated
   */
  public IPropertySource getPropertySource(Object object)
  {
    if (object instanceof IPropertySource)
    {
      return (IPropertySource)object;
    }
    AdapterFactory af = getAdapterFactory(object);
    if (af != null)
    {
      IItemPropertySource ips = (IItemPropertySource)af.adapt(object, IItemPropertySource.class);
      if (ips != null)
      {
        return new PropertySource(object, ips);
      }
    }
    if (object instanceof IAdaptable)
    {
      return ((IAdaptable)object).getAdapter(IPropertySource.class);
    }
    return null;
  }

  /**
   * @generated
   */
  @Override
  protected IPropertySourceProvider getPropertySourceProvider()
  {
    return this;
  }

  /**
   * Modify/unwrap selection.
   *
   * @generated
   */
  protected Object transformSelection(Object selected)
  {

    if (selected instanceof EditPart)
    {
      Object model = ((EditPart)selected).getModel();
      return model instanceof View ? ((View)model).getElement() : null;
    }
    if (selected instanceof View)
    {
      return ((View)selected).getElement();
    }
    if (selected instanceof IAdaptable)
    {
      View view = ((IAdaptable)selected).getAdapter(View.class);
      if (view != null)
      {
        return view.getElement();
      }
    }
    return selected;
  }

  /**
   * @generated
   */
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    if (selection.isEmpty() || false == selection instanceof StructuredSelection)
    {
      super.setInput(part, selection);
      return;
    }
    final StructuredSelection structuredSelection = (StructuredSelection)selection;
    ArrayList transformedSelection = new ArrayList(structuredSelection.size());
    for (Iterator it = structuredSelection.iterator(); it.hasNext();)
    {
      Object r = transformSelection(it.next());
      if (r != null)
      {
        transformedSelection.add(r);
      }
    }
    super.setInput(part, new StructuredSelection(transformedSelection));
  }

  /**
   * @generated
   */
  protected AdapterFactory getAdapterFactory(Object object)
  {
    if (getEditingDomain() instanceof AdapterFactoryEditingDomain)
    {
      return ((AdapterFactoryEditingDomain)getEditingDomain()).getAdapterFactory();
    }
    TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(object);
    if (editingDomain != null)
    {
      return ((AdapterFactoryEditingDomain)editingDomain).getAdapterFactory();
    }
    return null;
  }

}
