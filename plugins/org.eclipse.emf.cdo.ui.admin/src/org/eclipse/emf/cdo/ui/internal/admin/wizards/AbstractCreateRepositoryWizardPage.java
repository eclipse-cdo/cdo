/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.wizards;

import org.eclipse.emf.cdo.ui.internal.admin.bundle.OM;

import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public abstract class AbstractCreateRepositoryWizardPage extends WizardPage
{
  private Whiteboard whiteboard;

  public AbstractCreateRepositoryWizardPage(String pageName)
  {
    super(pageName);
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(UIUtil.createGridLayout(2));
    createContents(composite);
    setControl(composite);

    loadSettings(getDialogSettings());
    hookListeners();
  }

  protected abstract void createContents(Composite parent);

  protected void hookListeners()
  {
    hookListeners(new Listener()
    {
      @Override
      public void handleEvent(Event event)
      {
        updateEnablement(false);
      }
    });

    updateEnablement(true);
  }

  protected void hookListeners(Listener updateListener)
  {
  }

  protected void updateEnablement(boolean firstTime)
  {
    setPageComplete(true);
  }

  boolean performFinish(Map<String, Object> repositoryProperties)
  {
    boolean result = collectRepositoryProperties(repositoryProperties);
    saveSettings(getDialogSettings());
    return result;
  }

  protected abstract boolean collectRepositoryProperties(Map<String, Object> repositoryProperties);

  protected void loadSettings(IDialogSettings pageSettings)
  {
  }

  @Override
  protected IDialogSettings getDialogSettings()
  {
    IDialogSettings wizardSettings = super.getDialogSettings();
    return wizardSettings == null ? null : DialogSettings.getOrCreateSection(wizardSettings, getName());
  }

  protected String getSetting(IDialogSettings pageSettings, String key, String defaultValue)
  {
    return pageSettings.get(key) == null ? defaultValue : pageSettings.get(key);
  }

  protected boolean getSetting(IDialogSettings pageSettings, String key, boolean defaultValue)
  {
    return pageSettings.get(key) == null ? defaultValue : pageSettings.getBoolean(key);
  }

  protected void saveSettings(IDialogSettings pageSettings)
  {
  }

  protected Group group(Composite parent, String label)
  {
    Group result = new Group(parent, SWT.BORDER);
    result.setText(label);
    result.setLayoutData(UIUtil.createGridData(true, false));
    result.setLayout(new GridLayout(2, false));
    return result;
  }

  protected Text text(Composite parent, String label)
  {
    new Label(parent, SWT.NONE).setText(label);
    Text result = new Text(parent, SWT.BORDER);
    result.setLayoutData(UIUtil.createGridData(true, false));
    return result;
  }

  protected Button checkbox(Composite parent, String label)
  {
    Button result = new Button(parent, SWT.CHECK);
    result.setText(label);
    result.setLayoutData(UIUtil.createGridData(2, 1));
    return result;
  }

  protected ComboViewer combo(Composite parent, String label, Object input)
  {
    new Label(parent, SWT.NONE).setText(label);
    ComboViewer result = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
    result.setContentProvider(new ArrayContentProvider());
    result.setInput(input);
    return result;
  }

  protected boolean checked(Button checkbox)
  {
    return checkbox.isEnabled() && checkbox.getSelection();
  }

  protected String text(Text text)
  {
    return text.getText().trim();
  }

  protected boolean positiveInteger(String value)
  {
    try
    {
      return Integer.parseInt(value) > 0;
    }
    catch (NumberFormatException e)
    {
      return false;
    }
  }

  protected final void publish(Object topic)
  {
    if (whiteboard != null && topic != null)
    {
      whiteboard.publish(topic);
    }
  }

  final void bind(Whiteboard whiteboard)
  {
    if (this.whiteboard != null)
    {
      throw new IllegalStateException("already bound to a whiteboard"); //$NON-NLS-1$
    }

    this.whiteboard = whiteboard;
    whiteboard.subscribe(this);
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  @Target({ ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Subscribe
  {
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static final class Whiteboard
  {
    private List<Object> subscribers = new ArrayList<Object>();

    /**
     * Publish a topic to interested subscribers.
     */
    public void publish(Object topic)
    {
      for (Object subscriber : subscribers)
      {
        Method handler = getHandler(subscriber, topic.getClass());
        if (handler != null)
        {
          try
          {
            handler.invoke(subscriber, topic);
          }
          catch (Exception e)
          {
            OM.LOG.error(e);
          }
        }
      }
    }

    void subscribe(Object subscriber)
    {
      subscribers.add(subscriber);
    }

    private Method getHandler(Object subscriber, Class<?> topicType)
    {
      for (Method method : subscriber.getClass().getMethods())
      {
        if (method.isAnnotationPresent(Subscribe.class))
        {
          Class<?>[] parameterTypes = method.getParameterTypes();
          if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(topicType))
          {
            return method;
          }
        }
      }

      return null;
    }
  }
}
