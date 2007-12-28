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

/**
 * @author Eike Stepper
 */
public final class SecurityUtil
{
  public static final String PBE_WITH_MD5_AND_DES = "PBEWithMD5AndDES";

  private SecurityUtil()
  {
  }

  public static byte[] encrypt(byte[] data, char[] password, String algorithmName) throws NoSuchAlgorithmException,
      InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
      IllegalBlockSizeException, BadPaddingException
  {
    // Salt
    final byte[] salt = { (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee,
        (byte)0x99 }; // TODO Make configurable

    // Iteration count
    final int count = 20; // TODO Make configurable

    // Create PBE parameter set
    PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
    PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
    SecretKeyFactory keyFac = SecretKeyFactory.getInstance(algorithmName);
    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

    // Create PBE Cipher
    Cipher pbeCipher = Cipher.getInstance(algorithmName);

    // Initialize PBE Cipher with key and parameters
    pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

    return pbeCipher.doFinal(data);
  }
}
