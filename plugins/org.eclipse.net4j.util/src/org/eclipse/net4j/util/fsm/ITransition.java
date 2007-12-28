/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.fsm;

/**
 * @author Eike Stepper
 */
public interface ITransition<STATE extends Enum<?>, EVENT extends Enum<?>, SUBJECT, DATA>
{
  public void execute(SUBJECT subject, STATE state, EVENT event, DATA data);
}
