/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import java.util.function.Function;

/**
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
