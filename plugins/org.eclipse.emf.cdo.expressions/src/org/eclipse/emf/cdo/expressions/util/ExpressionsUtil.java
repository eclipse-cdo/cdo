/*
 * Copyright (c) 2013, 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.util;

import org.eclipse.emf.cdo.expressions.BooleanValue;
import org.eclipse.emf.cdo.expressions.ByteValue;
import org.eclipse.emf.cdo.expressions.CharValue;
import org.eclipse.emf.cdo.expressions.DoubleValue;
import org.eclipse.emf.cdo.expressions.Expression;
import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
import org.eclipse.emf.cdo.expressions.FloatValue;
import org.eclipse.emf.cdo.expressions.FunctionInvocation;
import org.eclipse.emf.cdo.expressions.IntValue;
import org.eclipse.emf.cdo.expressions.ListConstruction;
import org.eclipse.emf.cdo.expressions.LongValue;
import org.eclipse.emf.cdo.expressions.MemberInvocation;
import org.eclipse.emf.cdo.expressions.ShortValue;
import org.eclipse.emf.cdo.expressions.StringValue;

import java.util.Arrays;

/**
 * Provides static factory methods for common {@link Expression expressions}.
 *
 * @author Eike Stepper
 */
public class ExpressionsUtil
{
  public ExpressionsUtil()
  {
  }

  public static BooleanValue value(boolean literal)
  {
    BooleanValue value = ExpressionsFactory.eINSTANCE.createBooleanValue();
    value.setLiteral(literal);
    return value;
  }

  public static ByteValue value(byte literal)
  {
    ByteValue value = ExpressionsFactory.eINSTANCE.createByteValue();
    value.setLiteral(literal);
    return value;
  }

  public static CharValue value(char literal)
  {
    CharValue value = ExpressionsFactory.eINSTANCE.createCharValue();
    value.setLiteral(literal);
    return value;
  }

  public static DoubleValue value(double literal)
  {
    DoubleValue value = ExpressionsFactory.eINSTANCE.createDoubleValue();
    value.setLiteral(literal);
    return value;
  }

  public static FloatValue value(float literal)
  {
    FloatValue value = ExpressionsFactory.eINSTANCE.createFloatValue();
    value.setLiteral(literal);
    return value;
  }

  public static IntValue value(int literal)
  {
    IntValue value = ExpressionsFactory.eINSTANCE.createIntValue();
    value.setLiteral(literal);
    return value;
  }

  public static LongValue value(long literal)
  {
    LongValue value = ExpressionsFactory.eINSTANCE.createLongValue();
    value.setLiteral(literal);
    return value;
  }

  public static ShortValue value(short literal)
  {
    ShortValue value = ExpressionsFactory.eINSTANCE.createShortValue();
    value.setLiteral(literal);
    return value;
  }

  public static StringValue value(String literal)
  {
    StringValue value = ExpressionsFactory.eINSTANCE.createStringValue();
    value.setLiteral(literal);
    return value;
  }

  public static ListConstruction list(Expression... elements)
  {
    ListConstruction value = ExpressionsFactory.eINSTANCE.createListConstruction();
    value.getElements().addAll(Arrays.asList(elements));
    return value;
  }

  public static MemberInvocation invokeMember(Expression object, Expression name, Expression... arguments)
  {
    MemberInvocation expression = ExpressionsFactory.eINSTANCE.createMemberInvocation();
    expression.setObject(object);
    expression.setName(name);
    expression.getArguments().addAll(Arrays.asList(arguments));
    return expression;
  }

  public static FunctionInvocation invoke(Expression name, Expression... arguments)
  {
    FunctionInvocation expression = ExpressionsFactory.eINSTANCE.createFunctionInvocation();
    expression.setName(name);
    expression.getArguments().addAll(Arrays.asList(arguments));
    return expression;
  }

  public static FunctionInvocation construct(Expression className, Expression... arguments)
  {
    return invoke(value(className + ".new"), arguments);
  }

  // public static void main(String[] args)
  // {
  // System.out.println(Math.min(2, 4));
  // System.out.println(invoke(value("Math.min"), value(2), value(4)).evaluate(new EvaluationContextImpl()));
  // }
}
