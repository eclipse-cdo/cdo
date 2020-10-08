/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.DelegatingTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Provider;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

import javax.crypto.Cipher;

/**
 * @author Eike Stepper
 */
public class EncryptedPasswordFactory extends ITypeMapping.Provider.Factory
{
  private static final String TYPE = "encryptedPassword";

  private static final ITypeMapping.Provider PROVIDER = new Provider()
  {
    @Override
    public ITypeMapping createTypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature)
    {
      ITypeMapping typeMapping = ITypeMapping.Provider.INSTANCE.createTypeMapping(mappingStrategy, feature);
      if (feature == SecurityPackage.Literals.USER_PASSWORD__ENCRYPTED && typeMapping instanceof AbstractTypeMapping)
      {
        return new DelegatingTypeMapping()
        {
          @Override
          public AbstractTypeMapping getDelegate()
          {
            return (AbstractTypeMapping)typeMapping;
          }

          @Override
          protected Object encode(Object value)
          {
            return pbe(value, Cipher.ENCRYPT_MODE);
          }

          @Override
          protected Object decode(Object value)
          {
            return pbe(value, Cipher.DECRYPT_MODE);
          }

          private Object pbe(Object value, int mode)
          {
            try
            {
              byte[] data = ((String)value).getBytes();

              return SecurityUtil.pbe(data, //
                  "mypassword".toCharArray(), //
                  SecurityUtil.PBE_WITH_MD5_AND_DES, //
                  SecurityUtil.DEFAULT_SALT, //
                  SecurityUtil.DEFAULT_ITERATION_COUNT, mode);
            }
            catch (Exception ex)
            {
              throw WrappedException.wrap(ex);
            }
          }
        };
      }

      return typeMapping;
    }
  };

  public EncryptedPasswordFactory()
  {
    super(TYPE);
  }

  @Override
  public ITypeMapping.Provider create(String description) throws ProductCreationException
  {
    return PROVIDER;
  }
}
