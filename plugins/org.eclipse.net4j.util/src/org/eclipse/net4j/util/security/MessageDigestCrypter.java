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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public class MessageDigestCrypter implements ICrypter
{
  private final MessageDigest messageDigest;

  private final byte[] salt;

  public MessageDigestCrypter(MessageDigest messageDigest, String salt)
  {
    this.messageDigest = messageDigest;
    this.salt = salt == null ? null : salt.getBytes(StandardCharsets.UTF_8);
  }

  public MessageDigestCrypter(String algorithm, String salt) throws NoSuchAlgorithmException
  {
    this(MessageDigest.getInstance(algorithm), salt);
  }

  @Override
  public String getType()
  {
    return messageDigest.getAlgorithm();
  }

  @Override
  public String getParams()
  {
    return salt == null ? null : new String(salt, StandardCharsets.UTF_8);
  }

  @Override
  public byte[] apply(byte[] data)
  {
    synchronized (messageDigest)
    {
      if (salt != null)
      {
        messageDigest.update(salt);
      }

      return messageDigest.digest(data);
    }
  }
}
