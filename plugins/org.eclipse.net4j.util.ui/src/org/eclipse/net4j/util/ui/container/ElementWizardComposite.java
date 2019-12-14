/*
 * Copyright (c) 2010-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.ValidationContext;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public abstract class ElementWizardComposite extends Composite implements IListener
{
  private static final IElementWizard NO_WIZARD = new ElementWizard()
  {
    @Override
    protected void create(Composite parent)
    {
      // Do nothing
    }
  };

  private String productGroup;

  private String label;

  private List<String> factoryTypes;

  private String defaultFactoryType;

  private List<IElementWizard> wizards;

  private Map<IElementWizard, List<Control>> wizardControls = new HashMap<>();

  private Map<Control, IElementWizard> controlWizards = new HashMap<>();

  private ValidationContext validationContext;

  private boolean firstLayout = true;

  private Notifier notifier = new Notifier();

  public ElementWizardComposite(Composite parent, int style, String productGroup, String label)
  {
    this(parent, style, productGroup, label, null);
  }

  /**
   * @since 3.2
   */
  public ElementWizardComposite(Composite parent, int style, String productGroup, String label, ValidationContext validationContext)
  {
    this(parent, style, productGroup, label, validationContext, null);
  }

  /**
   * @since 3.2
   */
  public ElementWizardComposite(Composite parent, int style, String productGroup, String label, ValidationContext validationContext, String defaultFactoryType)
  {
    super(parent, style);
    this.productGroup = productGroup;
    this.label = label;
    this.defaultFactoryType = defaultFactoryType;

    setValidationContext(validationContext);
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

  /**
   * @since 3.2
   */
  public String getDefaultFactoryType()
  {
    return defaultFactoryType;
  }

  /**
   * Can be overridden by subclasses.
   */
  public String getDefaultDescription(String factoryType)
  {
    return null;
  }

  /**
   * @deprecated Override {@link #getDefaultDescription(String)} instead.
   */
  @Deprecated
  public void setDefaultDescription(String factoryType, String value)
  {
    // Do nothing
  }

  protected void init()
  {
    IManagedContainer container = getContainer();
    factoryTypes = new ArrayList<>(container.getFactoryTypes(getProductGroup()));
    Collections.sort(factoryTypes);

    wizards = new ArrayList<>();
    wizardControls.put(NO_WIZARD, new ArrayList<Control>());

    for (Iterator<String> it = factoryTypes.iterator(); it.hasNext();)
    {
      String factoryType = it.next();

      try
      {
        IElementWizard wizard = (IElementWizard)container.getElement(ElementWizardFactory.PRODUCT_GROUP, getProductGroup() + ":" + factoryType, null);

        wizards.add(wizard);
        wizardControls.put(wizard, new ArrayList<Control>());

        if (wizard instanceof ElementWizard)
        {
          ElementWizard impl = (ElementWizard)wizard;
          impl.addListener(this);
        }
      }
      catch (FactoryNotFoundException ex)
      {
        it.remove();
      }
    }
  }

  /**
   * @since 3.2
   */
  @Override
  public void notifyEvent(IEvent event)
  {
    notifier.fireEvent(event);
  }

  protected List<String> getFactoryTypes()
  {
    return factoryTypes;
  }

  protected void create()
  {
    init();
    setLayout(new GridLayout(2, false));

    {
      Label label = new Label(this, SWT.NONE);
      label.setText(getLabel());
      label.setLayoutData(UIUtil.createGridData(false, false));

      createFactoryTypeControl();
      harvestControls(NO_WIZARD);
    }

    for (int i = 0; i < wizards.size(); i++)
    {
      String factoryType = factoryTypes.get(i);
      String defaultDescription = getDefaultDescription(factoryType);

      IElementWizard wizard = wizards.get(i);
      wizard.create(this, getContainer(), productGroup, factoryType, defaultDescription, validationContext);
      harvestControls(wizard);
    }

    if (defaultFactoryType != null)
    {
      setFactoryType(defaultFactoryType);
    }
    else if (!factoryTypes.isEmpty())
    {
      setFactoryType(factoryTypes.get(0));
    }
  }

  protected void factoryTypeChanged()
  {
    String newFactoryType = getFactoryType();
    List<Control> controlsToRefresh = new ArrayList<>();

    for (int i = 0; i < wizards.size(); i++)
    {
      IElementWizard wizard = wizards.get(i);

      String factoryType = factoryTypes.get(i);
      boolean visible = factoryType.equals(newFactoryType);

      for (Control control : wizardControls.get(wizard))
      {
        control.setVisible(visible);

        if (!visible)
        {
          control.moveBelow(null);
        }

        controlsToRefresh.add(control);
      }
    }

    // layout(controlsToRefresh.toArray(new Control[controlsToRefresh.size()]));
    // layout(getChildren());

    layout();
    notifier.fireEvent();
  }

  @Override
  public void layout()
  {
    if (firstLayout)
    {
      super.layout();
      firstLayout = false;
    }
    else
    {
      super.layout(getChildren());
    }
  }

  protected void harvestControls(IElementWizard wizard)
  {
    for (Control child : getChildren())
    {
      if (!controlWizards.containsKey(child))
      {
        controlWizards.put(child, wizard);
        wizardControls.get(wizard).add(child);
      }
    }
  }

  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  /**
   * @since 3.2
   */
  public final INotifier getNotifier()
  {
    return notifier;
  }

  public String getDescription()
  {
    String resultType = getFactoryType();
    for (int i = 0; i < wizards.size(); i++)
    {
      String factoryType = factoryTypes.get(i);
      if (resultType.equals(factoryType))
      {
        return wizards.get(i).getResultDescription();
      }
    }

    return null;
  }

  public abstract String getFactoryType();

  protected abstract void setFactoryType(String factoryType);

  protected abstract void createFactoryTypeControl();

  /**
   * @author Eike Stepper
   */
  public static class WithCombo extends ElementWizardComposite implements SelectionListener
  {
    private Combo combo;

    public WithCombo(Composite parent, int style, String productGroup, String label)
    {
      super(parent, style, productGroup, label);
    }

    /**
     * @since 3.2
     */
    public WithCombo(Composite parent, int style, String productGroup, String label, ValidationContext validationContext)
    {
      super(parent, style, productGroup, label, validationContext);
    }

    /**
     * @since 3.2
     */
    public WithCombo(Composite parent, int style, String productGroup, String label, ValidationContext validationContext, String defaultFactoryType)
    {
      super(parent, style, productGroup, label, validationContext, defaultFactoryType);
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
      factoryTypeChanged();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    @Override
    public String getFactoryType()
    {
      return combo.getText();
    }

    @Override
    protected void setFactoryType(String factoryType)
    {
      int index = getFactoryTypes().indexOf(factoryType);
      if (index == -1)
      {
        combo.setText(factoryType);
      }
      else
      {
        combo.select(index);
      }

      factoryTypeChanged();
    }

    @Override
    protected void createFactoryTypeControl()
    {
      combo = new Combo(this, SWT.SINGLE);
      for (String factoryType : getFactoryTypes())
      {
        combo.add(factoryType);
      }

      combo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
      combo.addSelectionListener(this);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class WithRadios extends ElementWizardComposite implements SelectionListener
  {
    private Composite composite;

    public WithRadios(Composite parent, int style, String productGroup, String label)
    {
      super(parent, style, productGroup, label);
    }

    /**
     * @since 3.2
     */
    public WithRadios(Composite parent, int style, String productGroup, String label, ValidationContext validationContext)
    {
      super(parent, style, productGroup, label, validationContext);
    }

    /**
     * @since 3.2
     */
    public WithRadios(Composite parent, int style, String productGroup, String label, ValidationContext validationContext, String defaultFactoryType)
    {
      super(parent, style, productGroup, label, validationContext, defaultFactoryType);
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
      factoryTypeChanged();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    @Override
    public String getFactoryType()
    {
      Control[] choices = composite.getChildren();

      for (int i = 0; i < choices.length; i++)
      {
        Button choice = (Button)choices[i];
        if (choice.getSelection())
        {
          return getFactoryTypes().get(i);
        }
      }

      return null;
    }

    @Override
    protected void setFactoryType(String factoryType)
    {
      List<String> factoryTypes = getFactoryTypes();
      Control[] choices = composite.getChildren();

      for (int i = 0; i < factoryTypes.size(); i++)
      {
        Button choice = (Button)choices[i];
        choice.setSelection(factoryTypes.get(i).equals(factoryType));
      }

      factoryTypeChanged();
    }

    @Override
    protected void createFactoryTypeControl()
    {
      composite = new Composite(this, SWT.SINGLE);
      composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
      composite.setLayout(new FillLayout());
      for (String factoryType : getFactoryTypes())
      {
        Button choice = new Button(composite, SWT.RADIO);
        choice.setText(factoryType);
        choice.addSelectionListener(this);
      }
    }
  }
}
