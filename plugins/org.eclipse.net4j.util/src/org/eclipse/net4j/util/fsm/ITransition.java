/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.fsm;

/**
 * Encapsulates the logic to be executed when an <i>event</i> arrives for a <i>subject</i> in a particular <i>state</i>.
 *
 * @author Eike Stepper
 */
@FunctionalInterface
public interface ITransition<STATE extends Enum<?>, EVENT extends Enum<?>, SUBJECT, DATA>
{
  public void execute(SUBJECT subject, STATE state, EVENT event, DATA data);
}
