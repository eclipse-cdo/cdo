/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.expressions.ExpressionsPackage;
import org.eclipse.emf.cdo.expressions.FunctionInvocation;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Function Invocation</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class FunctionInvocationImpl extends InvocationImpl implements FunctionInvocation
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FunctionInvocationImpl()
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
    return ExpressionsPackage.Literals.FUNCTION_INVOCATION;
  }

  @Override
  protected boolean staticModifier()
  {
    return true;
  }

  @Override
  protected void collectInvocables(EvaluationContext context, String name, List<Invocable> invocables)
  {
    int methodStart = name.lastIndexOf('.');
    if (methodStart == -1)
    {
      throw new IllegalArgumentException("Method name missing: " + name);
    }

    String methodName = name.substring(methodStart + 1);
    String className = name.substring(0, methodStart);
    Class<?> c = context.getClass(className);

    if ("new".equals(methodName))
    {
      for (Constructor<?> constructor : c.getConstructors())
      {
        invocables.add(createConstructor(constructor));
      }
    }
    else
    {
      collectMethods(null, c, methodName, invocables);
    }
  }

  protected Invocable createConstructor(final Constructor<?> constructor)
  {
    return new Invocable()
    {
      @Override
      public String getName()
      {
        return "new";
      }

      @Override
      public Class<?>[] getParameterTypes()
      {
        return constructor.getParameterTypes();
      }

      @Override
      public Object invoke(Object[] arguments)
      {
        try
        {
          return constructor.newInstance(arguments);
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
        return constructor.toString();
      }
    };
  }

  // @Override
  // protected Object evaluate(EvaluationContext context, String name, Object[] arguments)
  // throws InvocationTargetException
  // {
  // int methodStart = name.lastIndexOf('.');
  // if (methodStart == -1)
  // {
  // throw new IllegalArgumentException("Method name missing: " + name);
  // }
  //
  // String className = name.substring(0, methodStart);
  // String methodName = name.substring(methodStart + 1);
  //
  // try
  // {
  // Class<?> c = getClass(className);
  // Class<?>[] argumentTypes = getTypes(arguments);
  //
  // if ("new".equals(methodName))
  // {
  // Constructor<?> constructor = c.getConstructor(argumentTypes);
  // return constructor.newInstance(arguments);
  // }
  // else
  // {
  // Method method = c.getMethod(methodName, argumentTypes);
  // if (!Modifier.isStatic(method.getModifiers()))
  // {
  // throw new IllegalArgumentException("Method is not static: " + name);
  // }
  //
  // return method.invoke(null, arguments);
  // }
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
  //
  // // Object left = getLeft().evaluate(context);
  // // Object right = getRight().evaluate(context);
  // //
  // // Operator operator = getOperator();
  // // switch (operator)
  // // {
  // // case EQUAL:
  // // return left.equals(right);
  // //
  // // case NOT_EQUAL:
  // // return !left.equals(right);
  // //
  // // case LESS_THAN:
  // // return ((Comparable<Object>)left).compareTo(right) < 0;
  // //
  // // case LESS_THAN_OR_EQUAL:
  // // return ((Comparable<Object>)left).compareTo(right) <= 0;
  // //
  // // case GREATER_THAN:
  // // return ((Comparable<Object>)left).compareTo(right) > 0;
  // //
  // // case GREATER_THAN_OR_EQUAL:
  // // return ((Comparable<Object>)left).compareTo(right) >= 0;
  // //
  // // case AND:
  // // return (Boolean)left && (Boolean)right;
  // //
  // // case OR:
  // // return (Boolean)left || (Boolean)right;
  // //
  // // default:
  // // throw new IllegalStateException("Unhandled operator: " + operator);
  // // }
  // //
  // }

} // FunctionInvocationImpl
