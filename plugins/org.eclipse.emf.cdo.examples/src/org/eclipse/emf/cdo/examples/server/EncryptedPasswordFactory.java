/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.DelegatingTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Provider;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.security.SecurityUtil;

import org.eclipse.emf.ecore.EStructuralFeature;

import javax.crypto.Cipher;

/**
 * This is an example on how to use a custom {@link org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Provider type mapping provider} for the
 * {@link IMappingStrategy mapping strategy} of a {@link IDBStore DB store}.
 * <p>
 * This {@link IFactory factory} is registered in the <code>plugin.xml</code> as follows:
 * <pre>
 *    &lt;extension point="org.eclipse.net4j.util.factories">
 *       &lt;factory
 *          productGroup="org.eclipse.emf.cdo.server.db.typeMappingProviders"
 *          type="encryptedPassword"
 *          class="org.eclipse.emf.cdo.examples.server.EncryptedPasswordFactory"/>
 *    &lt;/extension></pre>
 * <p>
 * Then it can be used in the <code>cdo-server.xml</code> as follows:
 * <pre>
 *    &lt;mappingStrategy type="horizontal">
 *       &lt;property name="typeMappingProvider" value="encryptedPassword"/>
 *    &lt;/mappingStrategy></pre>
 *
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
