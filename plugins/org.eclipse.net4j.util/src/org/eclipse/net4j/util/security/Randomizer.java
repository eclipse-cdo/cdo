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

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.security.SecureRandom;

/**
 * @author Eike Stepper
 */
public class Randomizer extends Lifecycle implements IRandomizer
{
  public static final String ALGORITHM_SHA1PRNG = "SHA1PRNG";

  public static final String DEFAULT_ALGORITHM_NAME = ALGORITHM_SHA1PRNG;

  private String algorithmName = DEFAULT_ALGORITHM_NAME;

  private String providerName;

  private transient SecureRandom secureRandom;

  public String getAlgorithmName()
  {
    return algorithmName;
  }

  public void setAlgorithmName(String algorithmName)
  {
    this.algorithmName = algorithmName;
  }

  public String getProviderName()
  {
    return providerName;
  }

  public void setProviderName(String providerName)
  {
    this.providerName = providerName;
  }

  public boolean nextBoolean()
  {
    return secureRandom.nextBoolean();
  }

  public double nextDouble()
  {
    return secureRandom.nextDouble();
  }

  public float nextFloat()
  {
    return secureRandom.nextFloat();
  }

  public synchronized double nextGaussian()
  {
    return secureRandom.nextGaussian();
  }

  public int nextInt()
  {
    return secureRandom.nextInt();
  }

  public int nextInt(int n)
  {
    return secureRandom.nextInt(n);
  }

  public long nextLong()
  {
    return secureRandom.nextLong();
  }

  public byte[] generateSeed(int numBytes)
  {
    return secureRandom.generateSeed(numBytes);
  }

  public String getAlgorithm()
  {
    return secureRandom.getAlgorithm();
  }

  public synchronized void nextBytes(byte[] bytes)
  {
    secureRandom.nextBytes(bytes);
  }

  public String nextString(int length, String alphabet)
  {
    int n = alphabet.length();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++)
    {
      int pos = nextInt(n);
      char c = alphabet.charAt(pos);
      builder.append(c);
    }

    return builder.toString();
  }

  public synchronized void setSeed(byte[] seed)
  {
    secureRandom.setSeed(seed);
  }

  public void setSeed(long seed)
  {
    secureRandom.setSeed(seed);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (algorithmName == null)
    {
      throw new IllegalStateException("algorithmName == null");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (providerName == null)
    {
      secureRandom = SecureRandom.getInstance(algorithmName);
    }
    else
    {
      secureRandom = SecureRandom.getInstance(algorithmName, providerName);
    }

    secureRandom.setSeed(System.currentTimeMillis());
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    secureRandom = null;
    super.doDeactivate();
  }
}
