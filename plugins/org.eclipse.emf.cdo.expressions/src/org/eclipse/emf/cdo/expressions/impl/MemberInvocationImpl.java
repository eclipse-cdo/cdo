/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.MemberInvocation;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Member Invocation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.expressions.impl.MemberInvocationImpl#getObject <em>Object</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MemberInvocationImpl extends InvocationImpl implements MemberInvocation
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MemberInvocationImpl()
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
    return ExpressionsPackage.Literals.MEMBER_INVOCATION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Expression getObject()
  {
    return (Expression)eDynamicGet(ExpressionsPackage.MEMBER_INVOCATION__OBJECT, ExpressionsPackage.Literals.MEMBER_INVOCATION__OBJECT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetObject(Expression newObject, NotificationChain msgs)
  {
    msgs = eDynamicInverseAdd((InternalEObject)newObject, ExpressionsPackage.MEMBER_INVOCATION__OBJECT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setObject(Expression newObject)
  {
    eDynamicSet(ExpressionsPackage.MEMBER_INVOCATION__OBJECT, ExpressionsPackage.Literals.MEMBER_INVOCATION__OBJECT, newObject);
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
    case ExpressionsPackage.MEMBER_INVOCATION__OBJECT:
      return basicSetObject(null, msgs);
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
    case ExpressionsPackage.MEMBER_INVOCATION__OBJECT:
      return getObject();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ExpressionsPackage.MEMBER_INVOCATION__OBJECT:
      setObject((Expression)newValue);
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
    case ExpressionsPackage.MEMBER_INVOCATION__OBJECT:
      setObject((Expression)null);
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
    case ExpressionsPackage.MEMBER_INVOCATION__OBJECT:
      return getObject() != null;
    }
    return super.eIsSet(featureID);
  }

  @Override
  protected boolean staticModifier()
  {
    return false;
  }

  @Override
  protected void collectInvocables(EvaluationContext context, String name, List<Invocable> invocables)
  {
    Object object = getObject().evaluate(context);
    if (object instanceof EObject)
    {
      EObject eObject = (EObject)object;
      EClass eClass = eObject.eClass();

      for (EOperation eOperation : eClass.getEAllOperations())
      {
        if (eOperation.getName().equals(name))
        {
          invocables.add(createEOperation(eClass.getOverride(eOperation), eObject));
        }
      }
    }

    collectMethods(object, object.getClass(), name, invocables);
  }

  private Invocable createEOperation(final EOperation eOperation, final EObject eObject)
  {
    return new Invocable()
    {
      @Override
      public String getName()
      {
        return eOperation.getName();
      }

      @Override
      public Class<?>[] getParameterTypes()
      {
        EList<EParameter> parameters = eOperation.getEParameters();
        Class<?>[] types = new Class<?>[parameters.size()];
        for (int i = 0; i < types.length; i++)
        {
          EParameter parameter = parameters.get(i);
          types[i] = parameter.getEType().getInstanceClass();
        }

        return types;
      }

      @Override
      public Object invoke(Object[] arguments)
      {
        try
        {
          return eObject.eInvoke(eOperation, ECollections.asEList(arguments));
        }
        catch (InvocationTargetException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }

      @Override
      public String toString()
      {
        return eOperation.toString();
      }
    };
  }

  // @Override
  // protected Object evaluate(EvaluationContext context, String name, Object[] arguments)
  // throws InvocationTargetException
  // {
  // Object object = getObject().evaluate(context);
  //
  // try
  // {
  // if (object instanceof EObject)
  // {
  // EObject eObject = (EObject)object;
  // return evaluateEOperation(eObject, name, ECollections.asEList(arguments));
  // }
  //
  // Class<?>[] argumentTypes = getTypes(arguments);
  //
  // Method method = object.getClass().getMethod(name, argumentTypes);
  // if (Modifier.isStatic(method.getModifiers()))
  // {
  // throw new IllegalArgumentException("Method is static: " + name);
  // }
  //
  // return method.invoke(object, arguments);
  // }
  // catch (RuntimeException ex)
  // {
  // throw ex;
  // }
  // catch (InvocationTargetException ex)
  // {
  // throw ex;
  // }
  // catch (Exception ex)
  // {
  // throw new InvocationTargetException(ex);
  // }
  // }
  //
  // protected Object evaluateEOperation(EObject object, String name, EList<Object> arguments)
  // throws InvocationTargetException
  // {
  // EOperation operation = getEOperation(object.eClass(), name, arguments);
  // return object.eInvoke(operation, arguments);
  // }
  //
  // protected EOperation getEOperation(EClass eClass, String name, EList<Object> arguments)
  // throws InvocationTargetException
  // {
  // EOperation result = null;
  // for (EOperation operation : eClass.getEOperations())
  // {
  // if (operation.getName().equals(name))
  // {
  // if (isAssignable(operation.getEParameters(), arguments))
  // {
  // if (result != null)
  // {
  // throw new IllegalStateException("Ambiguous member invocation: " + eClass.getName() + "." + name + arguments);
  // }
  //
  // result = operation;
  // }
  // }
  // }
  //
  // return result;
  // }
  //
  // protected boolean isAssignable(EList<EParameter> parameters, EList<Object> arguments)
  // {
  // if (parameters.size() != arguments.size())
  // {
  // return false;
  // }
  //
  // for (int i = 0; i < parameters.size(); i++)
  // {
  // EParameter parameter = parameters.get(i);
  // Class<?> instanceClass = parameter.getEType().getInstanceClass();
  // if (!instanceClass.isAssignableFrom(arguments.get(i).getClass()))
  // {
  // return false;
  // }
  // }
  //
  // return true;
  // }
  //
  // protected Class<?>[] getTypes(EList<EParameter> parameters)
  // {
  // Class<?>[] types = new Class<?>[parameters.size()];
  // for (int i = 0; i < types.length; i++)
  // {
  // EParameter parameter = parameters.get(i);
  // types[i] = parameter.getEType().getInstanceClass();
  // }
  //
  // return types;
  // }

} // MemberInvocationImpl
