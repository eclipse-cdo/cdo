/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

/**
 * @author Eike Stepper
 */
public interface IRandomizer
{
  public byte[] generateSeed(int numBytes);

  public boolean nextBoolean();

  public double nextDouble();

  public float nextFloat();

  public double nextGaussian();

  public int nextInt();

  public int nextInt(int n);

  public long nextLong();

  public void nextBytes(byte[] bytes);

  public String nextString(int length, String alphabet);
}
