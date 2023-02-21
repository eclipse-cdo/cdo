/*
 * Copyright (c) 2012-2016, 2018, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Executes the Diffie-Hellman key agreement protocol between 2 parties: {@link Server} and {@link Client}.
 *
 * @author Eike Stepper
 * @since 3.3
 */
public class DiffieHellman
{
  /**
   * Executes the server-side of the Diffie-Hellman key agreement protocol.
   *
   * @author Eike Stepper
   */
  public static class Server
  {
    /**
     * @deprecated As of 3.8 the default secret algorithm is "AES".
     */
    @Deprecated
    public static final String DEFAULT_SECRET_ALGORITHM = "DES";

    /**
     * @deprecated As of 3.8 the default cipher transformation is "AES/CBC/PKCS5Padding".
     */
    @Deprecated
    public static final String DEFAULT_CYPHER_TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private static final int DIFFIE_HELLMAN_KEY_SIZE = 2048;

    private static final int UNKNOWN_SECRET_ALGORITHM_KEY_LEN = -1;

    private static final String SECRET_ALGORITHM = "AES";

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final String realm;

    private final PrivateKey privateKey;

    private final Challenge challenge;

    /**
     * @since 3.8
     */
    public Server(String realm, KeyPairGenerator keyPairGenerator, int secretAlgorithmKeyLen, String secretAlgorithm, String cipherTransformation)
    {
      this.realm = realm;

      // Create DH key pair, using the passed DH parameters.
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      privateKey = keyPair.getPrivate();

      // Create and remember Challenge object.
      challenge = new Challenge(realm, secretAlgorithmKeyLen, secretAlgorithm, cipherTransformation, keyPair);
    }

    /**
     * @since 3.8
     */
    public Server(String realm, int diffieHellmanKeySize, int secretAlgorithmKeyLen, String secretAlgorithm, String cipherTransformation)
    {
      this(realm, createKeyPairGenerator(diffieHellmanKeySize), secretAlgorithmKeyLen, secretAlgorithm, cipherTransformation);
    }

    /**
     * @since 3.8
     */
    public Server(String realm, int diffieHellmanKeySize, String secretAlgorithm, String cipherTransformation)
    {
      this(realm, createKeyPairGenerator(diffieHellmanKeySize), UNKNOWN_SECRET_ALGORITHM_KEY_LEN, secretAlgorithm, cipherTransformation);
    }

    /**
     * @since 3.8
     */
    public Server(String realm, int diffieHellmanKeySize)
    {
      this(realm, diffieHellmanKeySize, SECRET_ALGORITHM, CIPHER_TRANSFORMATION);
    }

    public Server(String realm)
    {
      this(realm, DIFFIE_HELLMAN_KEY_SIZE);
    }

    /**
     * @since 3.8
     */
    public Server(String realm, DHParameterSpec dhParamSpec, int secretAlgorithmKeyLen, String secretAlgorithm, String cipherTransformation)
    {
      this(realm, createKeyPairGenerator(dhParamSpec), secretAlgorithmKeyLen, secretAlgorithm, cipherTransformation);
    }

    public Server(String realm, DHParameterSpec dhParamSpec, String secretAlgorithm, String cipherTransformation)
    {
      this(realm, createKeyPairGenerator(dhParamSpec), UNKNOWN_SECRET_ALGORITHM_KEY_LEN, secretAlgorithm, cipherTransformation);
    }

    public Server(String realm, DHParameterSpec dhParamSpec)
    {
      this(realm, dhParamSpec, SECRET_ALGORITHM, CIPHER_TRANSFORMATION);
    }

    public final String getRealm()
    {
      return realm;
    }

    public final Challenge getChallenge()
    {
      return challenge;
    }

    public byte[] handleResponse(Client.Response response)
    {
      try
      {
        // Instantiate a DH public key from the client's encoded key material.
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(response.getClientPubKeyEnc());
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        // Create and initialize DH KeyAgreement object.
        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(privateKey);

        // Use Client's public key for the first (and only) phase of her version of the DH protocol.
        keyAgree.doPhase(pubKey, true);

        byte[] sharedSecret = keyAgree.generateSecret();
        SecretKeySpec secretKeySpec = new SecretKeySpec(sharedSecret, 0, challenge.getSecretAlgorithmKeyLen(), challenge.getSecretAlgorithm());

        // Prepare the cipher used to decrypt.
        Cipher serverCipher = Cipher.getInstance(challenge.getCipherTransformation());

        byte[] encodedParams = response.getParamsEnc();
        if (encodedParams == null)
        {
          serverCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        }
        else
        {
          // Instantiate AlgorithmParameters object from parameter encoding obtained from client.
          AlgorithmParameters params = AlgorithmParameters.getInstance(challenge.getSecretAlgorithm());
          params.init(encodedParams);

          serverCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, params);
        }

        // Decrypt.
        return serverCipher.doFinal(response.getCipherText());
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    private static final KeyPairGenerator createKeyPairGenerator(int keySize)
    {
      try
      {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator;
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }
    }

    private static final KeyPairGenerator createKeyPairGenerator(DHParameterSpec dhParamSpec)
    {
      try
      {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(dhParamSpec);
        return keyPairGenerator;
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }
    }

    private static final int determineSecretAlgorithmKeyLen(String secretAlgorithm, String cipherTransformation, KeyPair keyPair)
    {
      byte[] secret;

      try
      {
        KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());
        keyAgree.doPhase(keyPair.getPublic(), true);

        secret = keyAgree.generateSecret();
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }

      for (int keyLen = secret.length; keyLen > 0; --keyLen)
      {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret, 0, keyLen, secretAlgorithm);

        try
        {
          Cipher clientCipher = Cipher.getInstance(cipherTransformation);
          clientCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
          return keyLen;
        }
        catch (GeneralSecurityException ex)
        {
          //$FALL-THROUGH$
        }
      }

      throw new SecurityException("Key length could not be determined for " + secretAlgorithm + " [" + cipherTransformation + "]");
    }

    /**
     * @author Eike Stepper
     */
    public static final class Challenge
    {
      private final String serverRealm;

      private final int secretAlgorithmKeyLen;

      private final String secretAlgorithm;

      private final String cipherTransformation;

      private final byte[] serverPubKeyEnc;

      private Challenge(String serverRealm, int secretAlgorithmKeyLen, String secretAlgorithm, String cipherTransformation, KeyPair keyPair)
      {
        this.serverRealm = serverRealm;

        if (secretAlgorithmKeyLen == UNKNOWN_SECRET_ALGORITHM_KEY_LEN)
        {
          this.secretAlgorithmKeyLen = determineSecretAlgorithmKeyLen(secretAlgorithm, cipherTransformation, keyPair);
        }
        else
        {
          this.secretAlgorithmKeyLen = secretAlgorithmKeyLen;
        }

        this.secretAlgorithm = secretAlgorithm;
        this.cipherTransformation = cipherTransformation;
        serverPubKeyEnc = keyPair.getPublic().getEncoded();
      }

      /**
       * @deprecated As of 3.8 public construction is no longer supported.
       */
      @Deprecated
      public Challenge(String serverRealm, String secretAlgorithm, String cipherTransformation, byte[] serverPubKeyEnc)
      {
        throw new UnsupportedOperationException();
      }

      public Challenge(ExtendedDataInput in) throws IOException
      {
        serverRealm = in.readString();
        secretAlgorithmKeyLen = in.readInt();
        secretAlgorithm = in.readString();
        cipherTransformation = in.readString();
        serverPubKeyEnc = in.readByteArray();
      }

      /**
       * @since 3.21
       */
      public Challenge(byte[] data) throws IOException
      {
        this(new ExtendedDataInputStream(new ByteArrayInputStream(data)));
      }

      /**
       * @since 3.21
       */
      public Challenge(String hex) throws IOException
      {
        this(HexUtil.hexToBytes(hex));
      }

      public String getServerRealm()
      {
        return serverRealm;
      }

      /**
       * @since 3.8
       */
      public int getSecretAlgorithmKeyLen()
      {
        return secretAlgorithmKeyLen;
      }

      public String getSecretAlgorithm()
      {
        return secretAlgorithm;
      }

      /**
       * @since 3.8
       */
      public String getCipherTransformation()
      {
        return cipherTransformation;
      }

      /**
       * @deprecated As of 3.8 use {@link #getCipherTransformation()}.
       */
      @Deprecated
      public String getCypherTransformation()
      {
        return cipherTransformation;
      }

      public byte[] getServerPubKeyEnc()
      {
        return serverPubKeyEnc;
      }

      public void write(ExtendedDataOutput out) throws IOException
      {
        out.writeString(serverRealm);
        out.writeInt(secretAlgorithmKeyLen);
        out.writeString(secretAlgorithm);
        out.writeString(cipherTransformation);
        out.writeByteArray(serverPubKeyEnc);
      }

      /**
       * @since 3.21
       */
      public byte[] toByteArray()
      {
        try
        {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          write(new ExtendedDataOutputStream(baos));
          return baos.toByteArray();
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }
      }

      /**
       * @since 3.21
       */
      @Override
      public String toString()
      {
        return HexUtil.bytesToHex(toByteArray());
      }
    }
  }

  /**
   * Executes the client-side of the Diffie-Hellman key agreement protocol.
   *
   * @author Eike Stepper
   */
  public static class Client
  {
    public Client()
    {
    }

    public Response handleChallenge(Server.Challenge challenge, byte[] clearText)
    {
      try
      {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(challenge.getServerPubKeyEnc());
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        // Use the DH parameters associated with the server's public key to generate own key pair
        DHParameterSpec dhParamSpec = ((DHPublicKey)pubKey).getParams();

        // Create own DH key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(dhParamSpec);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Create and initialize DH KeyAgreement object
        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
        clientKeyAgree.init(keyPair.getPrivate());

        // Encode public key
        byte[] pubKeyEnc = keyPair.getPublic().getEncoded();

        // Use server's public key for the first (and only) phase of his version of the DH protocol
        clientKeyAgree.doPhase(pubKey, true);

        // SecretKey sharedSecret = clientKeyAgree.generateSecret(challenge.getSecretAlgorithm());
        byte[] sharedSecret = clientKeyAgree.generateSecret();

        SecretKeySpec secretKeySpec = new SecretKeySpec(sharedSecret, 0, challenge.getSecretAlgorithmKeyLen(), challenge.getSecretAlgorithm());

        // Encrypt
        Cipher clientCipher = Cipher.getInstance(challenge.getCipherTransformation());
        clientCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] ciphertext = clientCipher.doFinal(clearText);

        // Retrieve the parameter that was used, and transfer it to the server in encoded format
        AlgorithmParameters params = clientCipher.getParameters();
        byte[] paramsEnc = params == null ? null : params.getEncoded();

        return new Response(pubKeyEnc, ciphertext, paramsEnc);
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Response
    {
      private final byte[] clientPubKeyEnc;

      private final byte[] cipherText;

      private final byte[] paramsEnc;

      public Response(byte[] clientPubKeyEnc, byte[] cipherText, byte[] paramsEnc)
      {
        this.clientPubKeyEnc = clientPubKeyEnc;
        this.cipherText = cipherText;
        this.paramsEnc = paramsEnc;
      }

      public Response(ExtendedDataInput in) throws IOException
      {
        clientPubKeyEnc = in.readByteArray();
        cipherText = in.readByteArray();
        paramsEnc = in.readByteArray();
      }

      /**
       * @since 3.21
       */
      public Response(byte[] data) throws IOException
      {
        this(new ExtendedDataInputStream(new ByteArrayInputStream(data)));
      }

      /**
       * @since 3.21
       */
      public Response(String hex) throws IOException
      {
        this(HexUtil.hexToBytes(hex));
      }

      public byte[] getClientPubKeyEnc()
      {
        return clientPubKeyEnc;
      }

      public byte[] getCipherText()
      {
        return cipherText;
      }

      public byte[] getParamsEnc()
      {
        return paramsEnc;
      }

      public void write(ExtendedDataOutput out) throws IOException
      {
        out.writeByteArray(clientPubKeyEnc);
        out.writeByteArray(cipherText);
        out.writeByteArray(paramsEnc);
      }

      /**
       * @since 3.21
       */
      public byte[] toByteArray()
      {
        try
        {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          write(new ExtendedDataOutputStream(baos));
          return baos.toByteArray();
        }
        catch (IOException ex)
        {
          throw new IORuntimeException(ex);
        }
      }

      /**
       * @since 3.21
       */
      @Override
      public String toString()
      {
        return HexUtil.bytesToHex(toByteArray());
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SkipParameterSpec
  {
    /**
     * The 1024 bit Diffie-Hellman modulus values used by SKIP
     */
    private static final byte modulusBytes[] = { (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58, (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD, (byte)0x20,
        (byte)0xB4, (byte)0x9D, (byte)0xE4, (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B, (byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D, (byte)0x45,
        (byte)0x1D, (byte)0x0F, (byte)0x7C, (byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C, (byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6, (byte)0xF3,
        (byte)0xC9, (byte)0x23, (byte)0xC0, (byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B, (byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB, (byte)0x55,
        (byte)0x8C, (byte)0xB8, (byte)0x5D, (byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD, (byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43, (byte)0xA3,
        (byte)0x1D, (byte)0x18, (byte)0x6C, (byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C, (byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C, (byte)0xE1,
        (byte)0xB1, (byte)0x29, (byte)0x40, (byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C, (byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72, (byte)0xD6,
        (byte)0x86, (byte)0xC4, (byte)0x03, (byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29, (byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C, (byte)0xD9,
        (byte)0x96, (byte)0x9F, (byte)0xAB, (byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B, (byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08, (byte)0x3D,
        (byte)0x66, (byte)0xA4, (byte)0x5D, (byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C, (byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22, (byte)0x19,
        (byte)0x26, (byte)0xBA, (byte)0xAB, (byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55, (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7 };

    /**
     * The SKIP 1024 bit modulus
     */
    private static final BigInteger modulus = new BigInteger(1, modulusBytes);

    /**
     * The base used with the SKIP 1024 bit modulus
     */
    private static final BigInteger base = BigInteger.valueOf(2);

    public static final DHParameterSpec INSTANCE = new DHParameterSpec(modulus, base);
  }

  /**
   * Creates Diffie-Hellman parameters.
   *
   * @author Eike Stepper
   */
  public static final class ParameterSpecGenerator
  {
    /**
     * Create Diffie-Hellman parameters.
     * <p>
     * Takes VERY long...
     */
    public static DHParameterSpec generate(int bits)
    {
      try
      {
        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
        paramGen.init(bits);

        AlgorithmParameters params = paramGen.generateParameters();
        return params.getParameterSpec(DHParameterSpec.class);
      }
      catch (GeneralSecurityException ex)
      {
        throw new SecurityException(ex);
      }
    }
  }
}
