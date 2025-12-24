/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import java.util.function.Function;

/**
 * Transforms byte arrays to byte arrays.
 *
 * @author Eike Stepper
 * @since 3.23
 */
public interface ICrypter extends Function<byte[], byte[]>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.security.crypters"; //$NON-NLS-1$

  public static final ICrypter IDENTITY = new Crypter("identity", null, data -> data);

  public String getType();

  public String getParams();

  @Override
  public byte[] apply(byte[] data);
}
