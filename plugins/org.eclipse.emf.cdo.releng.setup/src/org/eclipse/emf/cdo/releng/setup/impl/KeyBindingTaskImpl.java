/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.CommandParameter;
import org.eclipse.emf.cdo.releng.setup.KeyBindingTask;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.Scheme;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.keys.IBindingService;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Key Binding Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getScheme <em>Scheme</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getContext <em>Context</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getPlatform <em>Platform</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getLocale <em>Locale</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getKeys <em>Keys</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getCommand <em>Command</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.KeyBindingTaskImpl#getCommandParameters <em>Command Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class KeyBindingTaskImpl extends SetupTaskImpl implements KeyBindingTask
{
  /**
   * The default value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScheme()
   * @generated
   * @ordered
   */
  protected static final String SCHEME_EDEFAULT = "org.eclipse.ui.defaultAcceleratorConfiguration";

  /**
   * The cached value of the '{@link #getScheme() <em>Scheme</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScheme()
   * @generated
   * @ordered
   */
  protected String scheme = SCHEME_EDEFAULT;

  /**
   * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected static final String CONTEXT_EDEFAULT = "org.eclipse.ui.contexts.window";

  /**
   * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContext()
   * @generated
   * @ordered
   */
  protected String context = CONTEXT_EDEFAULT;

  /**
   * The default value of the '{@link #getPlatform() <em>Platform</em>}' attribute.
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @see #getPlatform()
   * @generated
   * @ordered
   */
  protected static final String PLATFORM_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPlatform() <em>Platform</em>}' attribute.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getPlatform()
   * @generated
   * @ordered
   */
  protected String platform = PLATFORM_EDEFAULT;

  /**
   * The default value of the '{@link #getLocale() <em>Locale</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getLocale()
   * @generated
   * @ordered
   */
  protected static final String LOCALE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getLocale() <em>Locale</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getLocale()
   * @generated
   * @ordered
   */
  protected String locale = LOCALE_EDEFAULT;

  /**
   * The default value of the '{@link #getKeys() <em>Keys</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKeys()
   * @generated
   * @ordered
   */
  protected static final String KEYS_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getKeys() <em>Keys</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getKeys()
   * @generated
   * @ordered
   */
  protected String keys = KEYS_EDEFAULT;

  /**
   * The default value of the '{@link #getCommand() <em>Command</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getCommand()
   * @generated
   * @ordered
   */
  protected static final String COMMAND_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getCommand() <em>Command</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getCommand()
   * @generated
   * @ordered
   */
  protected String command = COMMAND_EDEFAULT;

  /**
   * The cached value of the '{@link #getCommandParameters() <em>Command Parameters</em>}' containment reference list.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getCommandParameters()
   * @generated
   * @ordered
   */
  protected EList<CommandParameter> commandParameters;

  /**
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @generated
   */
  protected KeyBindingTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.KEY_BINDING_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getScheme()
  {
    return scheme;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setScheme(String newScheme)
  {
    String oldScheme = scheme;
    scheme = newScheme;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__SCHEME, oldScheme, scheme));
    }
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public String getContext()
  {
    return context;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setContext(String newContext)
  {
    String oldContext = context;
    context = newContext;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__CONTEXT, oldContext, context));
    }
  }

  /**
   * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
   * @generated
   */
  public String getPlatform()
  {
    return platform;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setPlatform(String newPlatform)
  {
    String oldPlatform = platform;
    platform = newPlatform;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__PLATFORM, oldPlatform,
          platform));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLocale()
  {
    return locale;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setLocale(String newLocale)
  {
    String oldLocale = locale;
    locale = newLocale;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__LOCALE, oldLocale, locale));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getKeys()
  {
    return keys;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setKeys(String newKeys)
  {
    String oldKeys = keys;
    keys = newKeys;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__KEYS, oldKeys, keys));
    }
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public String getCommand()
  {
    return command;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setCommand(String newCommand)
  {
    String oldCommand = command;
    command = newCommand;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.KEY_BINDING_TASK__COMMAND, oldCommand, command));
    }
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public EList<CommandParameter> getCommandParameters()
  {
    if (commandParameters == null)
    {
      commandParameters = new EObjectContainmentEList.Resolving<CommandParameter>(CommandParameter.class, this,
          SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS);
    }
    return commandParameters;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS:
      return ((InternalEList<?>)getCommandParameters()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
    	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.KEY_BINDING_TASK__SCHEME:
      return getScheme();
    case SetupPackage.KEY_BINDING_TASK__CONTEXT:
      return getContext();
    case SetupPackage.KEY_BINDING_TASK__PLATFORM:
      return getPlatform();
    case SetupPackage.KEY_BINDING_TASK__LOCALE:
      return getLocale();
    case SetupPackage.KEY_BINDING_TASK__KEYS:
      return getKeys();
    case SetupPackage.KEY_BINDING_TASK__COMMAND:
      return getCommand();
    case SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS:
      return getCommandParameters();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.KEY_BINDING_TASK__SCHEME:
      setScheme((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__CONTEXT:
      setContext((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__PLATFORM:
      setPlatform((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__LOCALE:
      setLocale((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__KEYS:
      setKeys((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__COMMAND:
      setCommand((String)newValue);
      return;
    case SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS:
      getCommandParameters().clear();
      getCommandParameters().addAll((Collection<? extends CommandParameter>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.KEY_BINDING_TASK__SCHEME:
      setScheme(SCHEME_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__CONTEXT:
      setContext(CONTEXT_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__PLATFORM:
      setPlatform(PLATFORM_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__LOCALE:
      setLocale(LOCALE_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__KEYS:
      setKeys(KEYS_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__COMMAND:
      setCommand(COMMAND_EDEFAULT);
      return;
    case SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS:
      getCommandParameters().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.KEY_BINDING_TASK__SCHEME:
      return SCHEME_EDEFAULT == null ? scheme != null : !SCHEME_EDEFAULT.equals(scheme);
    case SetupPackage.KEY_BINDING_TASK__CONTEXT:
      return CONTEXT_EDEFAULT == null ? context != null : !CONTEXT_EDEFAULT.equals(context);
    case SetupPackage.KEY_BINDING_TASK__PLATFORM:
      return PLATFORM_EDEFAULT == null ? platform != null : !PLATFORM_EDEFAULT.equals(platform);
    case SetupPackage.KEY_BINDING_TASK__LOCALE:
      return LOCALE_EDEFAULT == null ? locale != null : !LOCALE_EDEFAULT.equals(locale);
    case SetupPackage.KEY_BINDING_TASK__KEYS:
      return KEYS_EDEFAULT == null ? keys != null : !KEYS_EDEFAULT.equals(keys);
    case SetupPackage.KEY_BINDING_TASK__COMMAND:
      return COMMAND_EDEFAULT == null ? command != null : !COMMAND_EDEFAULT.equals(command);
    case SetupPackage.KEY_BINDING_TASK__COMMAND_PARAMETERS:
      return commandParameters != null && !commandParameters.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (scheme: ");
    result.append(scheme);
    result.append(", context: ");
    result.append(context);
    result.append(", platform: ");
    result.append(platform);
    result.append(", locale: ");
    result.append(locale);
    result.append(", keys: ");
    result.append(keys);
    result.append(", command: ");
    result.append(command);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    IBindingService bindingService = (IBindingService)PlatformUI.getWorkbench().getService(IBindingService.class);

    Binding[] bindings = bindingService.getBindings();
    for (int i = 0; i < bindings.length; i++)
    {
      Binding binding = bindings[i];
      if (binding instanceof KeyBinding)
      {
        KeyBinding keyBinding = (KeyBinding)binding;

        if (!ObjectUtil.equals(keyBinding.getSchemeId(), getScheme()))
        {
          continue;
        }

        if (!ObjectUtil.equals(keyBinding.getContextId(), getContext()))
        {
          continue;
        }

        if (!ObjectUtil.equals(keyBinding.getPlatform(), getPlatform()))
        {
          continue;
        }

        if (!ObjectUtil.equals(keyBinding.getLocale(), getLocale()))
        {
          continue;
        }

        KeySequence keySequence = KeySequence.getInstance(getKeys());
        if (!ObjectUtil.equals(keyBinding.getKeySequence(), keySequence))
        {
          continue;
        }

        ParameterizedCommand parameterizedCommand = keyBinding.getParameterizedCommand();
        if (parameterizedCommand == null || !ObjectUtil.equals(parameterizedCommand.getId(), getCommand()))
        {
          continue;
        }

        if (!ObjectUtil.equals(parameterizedCommand.getParameterMap(), getCommandParameterMap()))
        {
          continue;
        }

        return false;
      }
    }

    return true;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    KeyBinding binding = new KeyBinding(KeySequence.getInstance(getKeys()), getParameterizedCommand(), getScheme(),
        getContext(), getLocale(), getPlatform(), null, Binding.USER);

    final IBindingService bindingService = (IBindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
    Binding[] bindings = bindingService.getBindings();

    final Binding[] newBindings = new Binding[bindings.length + 1];
    System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
    newBindings[bindings.length] = binding;

    final Exception[] exception = { null };
    Display.getDefault().syncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          Scheme activeScheme = bindingService.getActiveScheme();
          bindingService.savePreferences(activeScheme, newBindings);
        }
        catch (IOException ex)
        {
          exception[0] = ex;
        }
      }
    });

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }

  private ParameterizedCommand getParameterizedCommand() throws NotDefinedException
  {
    ICommandService commandService = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
    Command command = commandService.getCommand(getCommand());

    @SuppressWarnings("rawtypes")
    Map params = getCommandParameterMap();

    return ParameterizedCommand.generateCommand(command, params);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Map getCommandParameterMap()
  {
    Map params = new HashMap();
    for (CommandParameter commandParameter : getCommandParameters())
    {
      params.put(commandParameter.getID(), commandParameter.getValue());
    }

    return params;
  }

} // KeyBindingTaskImpl
