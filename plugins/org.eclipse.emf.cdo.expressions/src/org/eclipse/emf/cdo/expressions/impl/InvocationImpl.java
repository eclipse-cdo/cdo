/*
 * Copyright (c) 2013, 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.Invocation;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Invocation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl#getArguments <em>Arguments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.InvocationImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class InvocationImpl extends CDOObjectImpl implements Invocation
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected InvocationImpl()
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
    return ExpressionsPackage.Literals.INVOCATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Expression> getArguments()
  {
    return (EList<Expression>)eDynamicGet(ExpressionsPackage.INVOCATION__ARGUMENTS, ExpressionsPackage.Literals.INVOCATION__ARGUMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expression getName()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.INVOCATION__NAME, ExpressionsPackage.Literals.INVOCATION__NAME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetName(Expression newName, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newName, ExpressionsPackage.INVOCATION__NAME, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(Expression newName)
  {
    eDynamicSet(ExpressionsPackage.INVOCATION__NAME, ExpressionsPackage.Literals.INVOCATION__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Object evaluate(EvaluationContext context)
  {
    String name = (String)getName().evaluate(context);

    EList<Expression> arguments = getArguments();
    int size = arguments.size();

    Object[] evaluatedArguments = new Object[size];
    for (int i = 0; i < evaluatedArguments.length; i++)
    {
      Expression argument = arguments.get(i);
      evaluatedArguments[i] = argument.evaluate(context);
    }

    List<Invocable> invocables = new BasicEList<>();
    collectInvocables(context, name, invocables);

    Invocable invocable = selectInvocable(invocables, evaluatedArguments);
    return invocable.invoke(evaluatedArguments);
  }

  protected abstract boolean staticModifier();

  protected abstract void collectInvocables(EvaluationContext context, String name, List<Invocable> invocables);

  protected void collectMethods(Object object, Class<?> c, String name, List<Invocable> invocables)
  {
    for (Method method : c.getMethods())
    {
      boolean static1 = Modifier.isStatic(method.getModifiers());
      boolean staticModifier = staticModifier();
      String name2 = method.getName();
      if (name2.equals(name) && static1 == staticModifier)
      {
        invocables.add(createMethod(object, method));
      }
    }
  }

  protected Invocable createMethod(final Object object, final Method method)
  {
    return new Invocable()
    {
      @Override
      public String getName()
      {
        return method.getName();
      }

      @Override
      public Class<?>[] getParameterTypes()
      {
        return method.getParameterTypes();
      }

      @Override
      public Object invoke(Object[] arguments)
      {
        try
        {
          return method.invoke(object, arguments);
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      @Override
      public String toString()
      {
        return method.toString();
      }
    };
  }

  protected Invocable selectInvocable(List<Invocable> invocables, Object[] arguments)
  {
    Invocable result = null;
    for (Invocable invocable : invocables)
    {
      Class<?>[] parameterTypes = invocable.getParameterTypes();
      if (isAssignable(parameterTypes, arguments))
      {
        if (result != null)
        {
          throw new IllegalStateException("Ambiguous invocation: " + invocable.getName() + arguments);
        }

        result = invocable;
      }
    }

    return result;
  }

  protected boolean isAssignable(Class<?>[] parameterTypes, Object[] arguments)
  {
    if (parameterTypes.length != arguments.length)
    {
      return false;
    }

    for (int i = 0; i < parameterTypes.length; i++)
    {
      Class<?> parameterType = box(parameterTypes[i]);
      Class<?> argumentType = arguments[i].getClass();
      if (!parameterType.isAssignableFrom(argumentType))
      {
        return false;
      }
    }

    return true;
  }

  protected Class<?> box(Class<?> type)
  {
    if (type.isPrimitive())
    {
      if (type == boolean.class)
      {
        return Boolean.class;
      }

      if (type == char.class)
      {
        return Character.class;
      }

      if (type == byte.class)
      {
        return Byte.class;
      }

      if (type == short.class)
      {
        return Short.class;
      }

      if (type == int.class)
      {
        return Integer.class;
      }

      if (type == long.class)
      {
        return Long.class;
      }

      if (type == float.class)
      {
        return Float.class;
      }

      if (type == double.class)
      {
        return Double.class;
      }
    }

    return type;
  }

  protected Class<?>[] getTypes(Object[] objects)
  {
    Class<?>[] types = new Class<?>[objects.length];
    for (int i = 0; i < objects.length; i++)
    {
      Object object = objects[i];
      types[i] = object == null ? Object.class : object.getClass();
    }

    return types;
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return ((InternalEList<?>)getArguments()).basicRemove(otherEnd, msgs);
    case ExpressionsPackage.INVOCATION__NAME:
      return basicSetName(null, msgs);
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return getArguments();
    case ExpressionsPackage.INVOCATION__NAME:
      return getName();
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      getArguments().clear();
      getArguments().addAll((Collection<? extends Expression>)newValue);
      return;
    case ExpressionsPackage.INVOCATION__NAME:
      setName((Expression)newValue);
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      getArguments().clear();
      return;
    case ExpressionsPackage.INVOCATION__NAME:
      setName((Expression)null);
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
    case ExpressionsPackage.INVOCATION__ARGUMENTS:
      return !getArguments().isEmpty();
    case ExpressionsPackage.INVOCATION__NAME:
      return getName() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case ExpressionsPackage.INVOCATION___EVALUATE__EVALUATIONCONTEXT:
      return evaluate((EvaluationContext)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

  /**
   * An abstraction of something that can be invoked, for example an {@link EOperation} or a {@link Method}.
   *
   * @author Eike Stepper
   */
  public interface Invocable
  {
    public String getName();

    public Class<?>[] getParameterTypes();

    public Object invoke(Object[] arguments);
  }

} // InvocationImpl
