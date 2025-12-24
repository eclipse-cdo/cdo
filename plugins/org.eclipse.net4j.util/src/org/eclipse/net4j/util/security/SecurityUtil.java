/*
 * Copyright (c) 2007, 2009-2012, 2015, 2016, 2020, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class SecurityUtil
{
  public static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES"; //$NON-NLS-1$

  /**
   * @since 2.0
   */
  public static final byte[] DEFAULT_SALT = { (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99 };

  /**
   * @since 2.0
   */
  public static final int DEFAULT_ITERATION_COUNT = 20;

  private static final ThreadLocal<Map<String, Object>> AUTHORIZATION_CONTEXT = new ThreadLocal<>();

  private SecurityUtil()
  {
  }

  /**
   * @since 3.13
   */
  public static byte[] pbe(byte[] data, char[] password, String algorithmName, byte[] salt, int count, int mode) throws NoSuchAlgorithmException,
      InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    // Create PBE parameter set
    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
    PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
    SecretKeyFactory keyFac = SecretKeyFactory.getInstance(algorithmName);
    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

    // Create PBE Cipher
    Cipher pbeCipher = Cipher.getInstance(algorithmName);

    // Initialize PBE Cipher with key and parameters
    pbeCipher.init(mode, pbeKey, pbeParamSpec);

    return pbeCipher.doFinal(data);
  }

  /**
   * @since 3.13
   */
  public static byte[] pbeDecrypt(byte[] data, char[] password, String algorithmName, byte[] salt, int count) throws NoSuchAlgorithmException,
      InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    return pbe(data, password, algorithmName, salt, count, Cipher.DECRYPT_MODE);
  }

  /**
   * @since 3.13
   */
  public static byte[] pbeEncrypt(byte[] data, char[] password, String algorithmName, byte[] salt, int count) throws NoSuchAlgorithmException,
      InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    return pbe(data, password, algorithmName, salt, count, Cipher.ENCRYPT_MODE);
  }

  /**
   * @since 3.14
   */
  public static String toString(char[] chars)
  {
    if (chars == null || chars.length == 0)
    {
      return null;
    }

    return new String(chars);
  }

  /**
   * @since 3.14
   */
  public static char[] toCharArray(String str)
  {
    if (str == null || str.length() == 0)
    {
      return null;
    }

    return str.toCharArray();
  }

  /**
   * @since 3.23
   */
  public static Map<String, Object> getAuthorizationContext()
  {
    return AUTHORIZATION_CONTEXT.get();
  }

  /**
   * @since 3.23
   */
  public static void setAuthorizationContext(Map<String, Object> authorizationContext)
  {
    if (authorizationContext == null)
    {
      AUTHORIZATION_CONTEXT.remove();
    }
    else
    {
      AUTHORIZATION_CONTEXT.set(authorizationContext);
    }
  }

  /**
   * @since 3.23
   */
  public static void withAuthorizationContext(Map<String, Object> authorizationContext, Runnable runnable)
  {
    Map<String, Object> oldAuthorizationContext = getAuthorizationContext();
    if (authorizationContext != oldAuthorizationContext)
    {
      setAuthorizationContext(authorizationContext);
    }

    try
    {
      runnable.run();
    }
    finally
    {
      if (authorizationContext != oldAuthorizationContext)
      {
        setAuthorizationContext(oldAuthorizationContext);
      }
    }
  }

  /**
   * @since 2.0
   * @deprecated As of 3.3. use {@link #pbeEncrypt(byte[], char[], String, byte[], int)}.
   */
  @Deprecated
  public static byte[] encrypt(byte[] data, char[] password, String algorithmName, byte[] salt, int count) throws NoSuchAlgorithmException,
      InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    return pbeEncrypt(data, password, algorithmName, salt, count);
  }
}
