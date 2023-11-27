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

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.factory.SingletonFactory;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public final class Crypter implements ICrypter
{
  private final String type;

  private final String params;

  private final Function<byte[], byte[]> function;

  public Crypter(String type, String params, Function<byte[], byte[]> function)
  {
    this.type = type;
    this.params = params;
    this.function = function;
  }

  @Override
  public String getType()
  {
    return type;
  }

  @Override
  public String getParams()
  {
    return params;
  }

  @Override
  public byte[] apply(byte[] data)
  {
    return function.apply(data);
  }

  /**
   * @author Eike Stepper
   */
  public static final class MetaFactory extends org.eclipse.net4j.util.factory.MetaFactory
  {
    private static final String PG = ICrypter.PRODUCT_GROUP;

    private static final IFactory[] CHILDREN = createChildren();

    public MetaFactory()
    {
      super(PG);
    }

    @Override
    public IFactory[] create(String description) throws ProductCreationException
    {
      return CHILDREN;
    }

    private static IFactory[] createChildren()
    {
      List<IFactory> children = new ArrayList<>();
      children.add(new SingletonFactory(PG, IDENTITY.getType(), IDENTITY));

      for (Provider provider : Security.getProviders())
      {
        for (Service service : provider.getServices())
        {
          String type = service.getType();
          if (type.equals("MessageDigest"))
          {
            String algorithm = service.getAlgorithm();

            children.add(new Factory(ICrypter.PRODUCT_GROUP, algorithm)
            {
              @Override
              public Object create(String params) throws ProductCreationException
              {
                try
                {
                  return new MessageDigestCrypter(algorithm, params);
                }
                catch (NoSuchAlgorithmException ex)
                {
                  throw new ProductCreationException(ex);
                }
              }
            });
          }
        }
      }

      return children.toArray(new IFactory[children.size()]);
    }
  }
}
