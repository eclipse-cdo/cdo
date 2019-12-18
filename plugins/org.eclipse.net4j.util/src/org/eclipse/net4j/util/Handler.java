/*
 * Copyright (c) 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.util.function.Consumer;

/**
 * @author Eike Stepper
 * @since 3.9
 * @deprecated As of 3.10 use {@link Consumer}.
 */
@Deprecated
public interface Handler<T>
{
  public void handle(T object);
}
