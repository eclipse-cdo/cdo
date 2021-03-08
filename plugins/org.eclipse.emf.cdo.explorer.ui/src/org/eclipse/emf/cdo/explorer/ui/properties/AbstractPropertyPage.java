/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.properties.IPropertyProvider;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PropertyPage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public abstract class AbstractPropertyPage<T> extends PropertyPage
{
  private final IPropertyProvider<T> propertyProvider;

  private final String category;

  private final String[] propertyNames;

  public AbstractPropertyPage(IPropertyProvider<T> propertyProvider, String category, String... propertyNames)
  {
    assert propertyProvider != null;
    assert category != null;

    this.propertyProvider = propertyProvider;
    this.category = category;
    this.propertyNames = propertyNames;

    noDefaultAndApplyButton();
  }

  public final T getInput()
  {
    IAdaptable element = getElement();
    return convertElement(element);
  }

  protected List<Property<T>> getProperties()
  {
    return propertyProvider.getProperties();
  }

  @Override
  protected Control createContents(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout containerGridLayout = new GridLayout();
    container.setLayout(containerGridLayout);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;

    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setLayout(gridLayout);

    T input = getInput();

    createContents(composite, input, getProperties(), propertyNames);

    return container;
  }

  protected void createContents(Composite parent, T input, List<Property<T>> properties, String... propertyNames)
  {
    List<String> names = new ArrayList<>();
    Map<String, Property<T>> propertiesMap = new HashMap<>();

    for (Property<T> property : properties)
    {
      String propertyCategory = property.getCategory();
      if (category.equals(propertyCategory))
      {
        String name = property.getName();
        names.add(name);
        propertiesMap.put(name, property);
      }
    }

    if (propertyNames.length != 0)
    {
      for (String name : propertyNames)
      {
        Property<T> property = propertiesMap.get(name);
        createControl(parent, input, property);

        names.remove(name);
      }

      Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }

    names.sort(null);

    for (String name : names)
    {
      Property<T> property = propertiesMap.get(name);
      createControl(parent, input, property);
    }
  }

  protected Control createControl(Composite parent, T input, Property<T> property)
  {
    String name = property.getName();
    String label = property.getLabel();
    String description = property.getDescription();
    String value = StringUtil.safe(property.getValue(input));

    Label labelControl = new Label(parent, SWT.NONE);
    labelControl.setText(label + ":");

    Control control = createControl(parent, name, description, value);
    if (control == null)
    {
      control = new Label(parent, SWT.NONE);
    }

    if (!StringUtil.isEmpty(description))
    {
      if (StringUtil.isEmpty(labelControl.getToolTipText()))
      {
        labelControl.setToolTipText(description);
      }

      if (StringUtil.isEmpty(control.getToolTipText()))
      {
        control.setToolTipText(description);
      }
    }

    return control;
  }

  protected Control createControl(Composite parent, String name, String description, String value)
  {
    Label control = new Label(parent, SWT.NONE);
    control.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    control.setText(value);

    return control;
  }

  protected Link createFileLink(Composite parent, String name, String description, String value)
  {
    File file = new File(value);
    return createLink(parent, file.toString(), file.toURI().toString(), uri -> IOUtil.openSystemBrowser(uri));
  }

  protected Link createLink(Composite parent, String label, String uri, Consumer<String> consumer)
  {
    Link link = new Link(parent, SWT.NONE);
    link.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    setLinkText(link, label, uri);

    link.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        consumer.accept(uri);
      }
    });

    return link;
  }

  protected void setLinkText(Link link, String label, String uri)
  {
    link.setText("<a href=\"" + uri + "\">" + label + "</a>");
  }

  protected abstract T convertElement(IAdaptable element);
}
