/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.net4j.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DigestHelper
{

  /**
   * Encrypt byte array.
   */
  public static byte[] encrypt(byte[] source, String algorithm) throws NoSuchAlgorithmException
  {
    MessageDigest md = MessageDigest.getInstance(algorithm);
    md.reset();
    md.update(source);
    return md.digest();
  }

  /**
   * Encrypt string
   */
  public static String encrypt(String source, String algorithm) throws NoSuchAlgorithmException
  {
    byte[] resByteArray = encrypt(source.getBytes(), algorithm);
    return StringHelper.toHexString(resByteArray);
  }

  /**
   * Encrypt string using MD5 algorithm
   */
  public static String encryptMD5(String source)
  {
    if (source == null)
    {
      source = "";
    }

    String result = "";

    try
    {
      result = encrypt(source, "MD5");
    }
    catch (NoSuchAlgorithmException ex)
    {
      ex.printStackTrace();
    }

    return result;
  }

  /**
   * Encrypt string using SHA algorithm
   */
  public static String encryptSHA(String source)
  {
    if (source == null)
    {
      source = "";
    }

    String result = "";

    try
    {
      result = encrypt(source, "SHA");
    }
    catch (NoSuchAlgorithmException ex)
    {
      ex.printStackTrace();
    }

    return result;
  }

}