/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.protocol;

/**
 * A {@link #getVersion() versioned} {@link IProtocol protocol}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface IProtocol2<INFRA_STRUCTURE> extends IProtocol<INFRA_STRUCTURE>
{
  public static final int UNSPECIFIED_VERSION = 0;

  public int getVersion();
}
