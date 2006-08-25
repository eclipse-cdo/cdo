/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


/**
 * <code>Assert</code> is useful for for embedding runtime sanity checks
 * in code.
 * The predicate methods all test a condition and throw some
 * type of unchecked exception if the condition does not hold.
 * <p>
 * Assertion failure exceptions, like most runtime exceptions, are
 * thrown when something is misbehaving. Assertion failures are invariably
 * unspecified behavior; consequently, clients should never rely on
 * these being thrown (and certainly should not being catching them
 * specifically).
 * </p>
 */
public final class Argument
{
  /* This class is not intended to be instantiated. */
  private Argument()
  {
    // not allowed
  }

  /** Asserts that the given object is not <code>null</code>. If this
   * is not the case, some kind of unchecked exception is thrown.
   * 
   * @param object the value to test
   * @exception IllegalArgumentException if the object is <code>null</code>
   */
  public static void isNotNull(Object object)
  {
    isNotNull(object, ""); //$NON-NLS-1$
  }

  /** Asserts that the given object is not <code>null</code>. If this
   * is not the case, some kind of unchecked exception is thrown.
   * The given message is included in that exception, to aid debugging.
   *
   * @param object the value to test
   * @param message the message to include in the exception
   * @exception IllegalArgumentException if the object is <code>null</code>
   */
  public static void isNotNull(Object object, String message)
  {
    if (object == null) throw new IllegalArgumentException("null argument:" + message); //$NON-NLS-1$
  }

  /** Asserts that the given boolean is <code>true</code>. If this
   * is not the case, some kind of unchecked exception is thrown.
   *
   * @param expression the outcode of the check
   * @return <code>true</code> if the check passes (does not return
   *    if the check fails)
   */
  public static boolean isTrue(boolean expression)
  {
    return isTrue(expression, ""); //$NON-NLS-1$
  }

  /** Asserts that the given boolean is <code>true</code>. If this
   * is not the case, some kind of unchecked exception is thrown.
   * The given message is included in that exception, to aid debugging.
   *
   * @param expression the outcode of the check
   * @param message the message to include in the exception
   * @return <code>true</code> if the check passes (does not return
   *    if the check fails)
   */
  public static boolean isTrue(boolean expression, String message)
  {
    if (!expression) throw new IllegalArgumentException("assertion failed: " + message); //$NON-NLS-1$
    return expression;
  }
}
