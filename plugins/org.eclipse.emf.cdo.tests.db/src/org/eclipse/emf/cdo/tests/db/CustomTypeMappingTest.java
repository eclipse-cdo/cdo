/*
 * Copyright (c) 2010-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.AbstractTypeMappingFactory;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Stefan Winkler
 */
public class CustomTypeMappingTest extends AbstractCDOTest
{
  public void testCustomTypeMapping() throws CommitException
  {
    // Manually register type mapping
    MyIntToVarcharTypeMapping.Factory factory = new MyIntToVarcharTypeMapping.Factory();
    IPluginContainer.INSTANCE.registerFactory(factory);

    try
    {
      final EPackage pkg = createUniquePackage();

      EClass foo = EMFUtil.createEClass(pkg, "foo", false, false);
      EAttribute bar = EMFUtil.createEAttribute(foo, "bar", EcorePackage.eINSTANCE.getEInt());

      // Annotate type mapping and column type
      EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
      annotation.setSource("http://www.eclipse.org/CDO/DBStore");
      annotation.getDetails().put("typeMapping", "org.eclipse.emf.cdo.tests.db.EIntToVarchar");
      annotation.getDetails().put("columnType", DBType.VARCHAR.getKeyword());
      bar.getEAnnotations().add(annotation);

      if (!isConfig(LEGACY))
      {
        CDOUtil.prepareDynamicEPackage(pkg);
      }

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test"));

      EObject obj = EcoreUtil.create(foo);
      obj.eSet(bar, 42);

      resource.getContents().add(obj);
      transaction.commit();
      transaction.close();
      session.close();

      msg("Check if type was mapped to string...");
      new DBStoreVerifier(getRepository())
      {
        @Override
        protected void doVerify() throws Exception
        {
          Statement stmt = null;
          ResultSet rset = null;

          try
          {
            stmt = getStatement();
            rset = stmt.executeQuery("SELECT " + DBUtil.quoted("bar") + " FROM " + DBUtil.quoted(pkg.getName() + "_foo"));
            assertEquals("java.lang.String", rset.getMetaData().getColumnClassName(1));

            rset.next();
            assertEquals("2a", rset.getString(1));
          }
          finally
          {
            DBUtil.close(rset);
            DBUtil.close(stmt);
          }
        }
      }.verify();
    }
    finally
    {
      IPluginContainer.INSTANCE.getFactoryRegistry().remove(factory.getKey());
    }
  }

  /**
   * @author Stefan Winkler
   */
  public static class MyIntToVarcharTypeMapping extends AbstractTypeMapping
  {
    public static final ITypeMapping.Descriptor DESCRIPTOR = TypeMappingUtil.createDescriptor("org.eclipse.emf.cdo.tests.db.EIntToVarchar",
        EcorePackage.eINSTANCE.getEInt(), DBType.VARCHAR);

    public MyIntToVarcharTypeMapping()
    {
    }

    @Override
    protected void doSetValue(PreparedStatement stmt, int index, Object value) throws SQLException
    {
      Integer val = (Integer)value;
      stmt.setString(index, Integer.toHexString(val));
    }

    @Override
    protected Object getResultSetValue(ResultSet resultSet) throws SQLException
    {
      String stringVal = resultSet.getString(getField().getName());
      return Integer.parseInt(stringVal, 16);
    }

    /**
     * @author Stefan Winkler
     */
    public static class Factory extends AbstractTypeMappingFactory
    {
      public Factory()
      {
        super(DESCRIPTOR);
      }

      @Override
      public ITypeMapping create(String description)
      {
        return new MyIntToVarcharTypeMapping();
      }
    }
  }
}
