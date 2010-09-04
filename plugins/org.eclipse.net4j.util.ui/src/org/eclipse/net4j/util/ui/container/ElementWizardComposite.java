/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.container;

import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.container.IElementWizard.ValidationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class ElementWizardComposite extends Composite
{
  private final String productGroup;

  private final String label;

  private Control selection;

  private List<String> factoryTypes;

  private List<IElementWizard> wizards;

  private ValidationContext validationContext;

  public ElementWizardComposite(Composite parent, int style, String productGroup, String label)
  {
    super(parent, style);
    this.productGroup = productGroup;
    this.label = label;
    create();
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public String getLabel()
  {
    return label;
  }

  public void setValidationContext(ValidationContext validationContext)
  {
    this.validationContext = validationContext;
  }

  protected void create()
  {
    IManagedContainer container = getContainer();
    factoryTypes = new ArrayList<String>(container.getFactoryTypes(getProductGroup()));
    Collections.sort(factoryTypes);

    wizards = new ArrayList<IElementWizard>();
    for (Iterator<String> it = factoryTypes.iterator(); it.hasNext();)
    {
      String factoryType = it.next();

      try
      {
        IElementWizard wizard = (IElementWizard)container.getElement(ElementWizardFactory.PRODUCT_GROUP,
            getProductGroup() + ":" + factoryType, null);
        wizards.add(wizard);
      }
      catch (FactoryNotFoundException ex)
      {
        it.remove();
      }
    }

    setLayout(new GridLayout(2, false));

    Label label2 = new Label(this, SWT.NONE);
    label2.setText(getLabel());
    label2.setLayoutData(UIUtil.createGridData(false, false));

    selection = createSelection();
    selection.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
  }

  protected Control createSelection()
  {
    final Combo combo = new Combo(this, SWT.SINGLE);
    for (String factoryType : factoryTypes)
    {
      combo.add(factoryType);
    }

    combo.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        factoryChosen(combo.getSelectionIndex());
      }
    });

    return combo;
  }

  protected void disposeWizardControl()
  {
    Control[] children = getChildren();
    for (int i = 2; i < children.length; i++)
    {
      Control child = children[i];
      child.dispose();
    }
  }

  protected void factoryChosen(int index)
  {
    disposeWizardControl();

    IElementWizard wizard = wizards.get(index);
    String factoryType = factoryTypes.get(index);
    wizard.create(this, factoryType, null, validationContext);
    getParent().layout();
  }

  protected String getDefaultDescription(String factoryType)
  {
    return null;
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
