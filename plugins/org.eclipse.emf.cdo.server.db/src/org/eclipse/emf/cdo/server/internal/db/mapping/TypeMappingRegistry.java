/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Stefan Winkler - Bug 285426: [DB] Implement user-defined typeMapping support
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.db.IIDHandler;
import org.eclipse.emf.cdo.server.db.mapping.ColumnTypeModifier;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Provider;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingUtil.FactoryTypeParserException;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.FactoryNotFoundException;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of both the Registry and Provider interfaces for type mappings. This class is a singleton which
 * keeps itself in sync with the global factory registry. It reads the available factoryTypes for the type mappings
 * product type and populates indexes which make it easier to determine and look up the correct factories for a needed
 * type mapping.
 *
 * @author Stefan Winkler
 */
public class TypeMappingRegistry implements ITypeMapping.Registry, ITypeMapping.Provider, IManagedContainerProvider
{
  public static final EClass CUSTOM_DATA_TYPE = EcorePackage.Literals.EDATA_TYPE;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TypeMappingRegistry.class);

  /**
   * Contains a map from model types to db types which represent default mappings. (I.e., if a model element without db
   * type annotation is encountered, this map is consulted to retrieve the default type mapping. This map is populated
   * on a come-first basis. The first mapping for a particular {@link EClassifier} is set as default.
   */
  private Map<EClassifier, DBType> classifierDefaultMapping;

  /**
   * The main TypeMapping index. For any known pair of model and db types the {@link ITypeMapping.Descriptor} is
   * registered here.
   */
  private Map<Pair<EClassifier, DBType>, ITypeMapping.Descriptor> typeMappingByTypes;

  /**
   * ID-based index. Can be used to lookup an {@link ITypeMapping.Descriptor} for a given ID.
   */
  private Map<String, ITypeMapping.Descriptor> typeMappingsById;

  /**
   * A populator which is used to keep the registry in sync with the registered factories of the
   * {@link IManagedContainer}.
   */
  private RegistryPopulator populator = new RegistryPopulator();

  public TypeMappingRegistry()
  {
    init();
  }

  public void init()
  {
    populator.disconnect();

    registerColumnTypeModifier("postgresql", new ColumnTypeModifier()
    {
      @Override
      public DBType modify(Provider provider, IMappingStrategy mappingStrategy, EStructuralFeature feature, DBType dbType)
      {
        EClassifier classifier = feature.getEType();
        if (classifier == EcorePackage.Literals.ECHAR)
        {
          return DBType.INTEGER;
        }

        if (classifier == EcorePackage.Literals.ECHARACTER_OBJECT)
        {
          return DBType.INTEGER;
        }

        return dbType;
      }
    });

    typeMappingsById = new HashMap<>();
    typeMappingByTypes = new HashMap<>();
    classifierDefaultMapping = new HashMap<>();

    registerCoreTypeMappings();
    populator.connect();
  }

  public void registerColumnTypeModifier(String factoryType, final ColumnTypeModifier columnTypeModifier)
  {
    getContainer().registerFactory(new ColumnTypeModifier.Factory(factoryType)
    {
      @Override
      public ColumnTypeModifier create(String description) throws ProductCreationException
      {
        return columnTypeModifier;
      }
    });
  }

  /**
   * Register builtin type mapping factories
   */
  private void registerCoreTypeMappings()
  {
    IManagedContainer container = getContainer();
    container.registerFactory(CoreTypeMappings.TMBigDecimal.FACTORY);
    container.registerFactory(CoreTypeMappings.TMBigDecimal.FACTORY_LONG_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMBigInteger.FACTORY);
    container.registerFactory(CoreTypeMappings.TMBigInteger.FACTORY_LONG_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY_SMALLINT);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY_OBJECT_SMALLINT);
    container.registerFactory(CoreTypeMappings.TMByte.FACTORY);
    container.registerFactory(CoreTypeMappings.TMByte.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMBytes.FACTORY);
    container.registerFactory(CoreTypeMappings.TMBytesVarbinary.FACTORY);
    container.registerFactory(CoreTypeMappings.TMCharacter.FACTORY);
    container.registerFactory(CoreTypeMappings.TMCharacter.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMCharacter2Integer.FACTORY);
    container.registerFactory(CoreTypeMappings.TMCharacter2Integer.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMCustom.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMCustom.FACTORY_CLOB);
    container.registerFactory(CoreTypeMappings.TMCustom.FACTORY_LONG_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMDate2Date.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDate2Time.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDate2Timestamp.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDouble.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDouble.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMEnum.FACTORY);
    container.registerFactory(CoreTypeMappings.TMFloat.FACTORY);
    container.registerFactory(CoreTypeMappings.TMFloat.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMInteger.FACTORY);
    container.registerFactory(CoreTypeMappings.TMInteger.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMLong.FACTORY);
    container.registerFactory(CoreTypeMappings.TMLong.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMShort.FACTORY);
    container.registerFactory(CoreTypeMappings.TMShort.FACTORY_OBJECT);
    container.registerFactory(CoreTypeMappings.TMString.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMString.FACTORY_CLOB);
    container.registerFactory(CoreTypeMappings.TMString.FACTORY_LONG_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMJavaClass.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMJavaObject.FACTORY);
    container.registerFactory(CoreTypeMappings.TMBlob.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMBlob.FACTORY_LONG_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMClob.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMClob.FACTORY_LONG_VARCHAR);

    classifierDefaultMapping.put(CUSTOM_DATA_TYPE, DBType.VARCHAR);

    classifierDefaultMapping.put(EcorePackage.Literals.EBIG_DECIMAL, DBType.VARCHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.EBIG_INTEGER, DBType.VARCHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.EBOOLEAN, DBType.BOOLEAN);
    classifierDefaultMapping.put(EcorePackage.Literals.EBOOLEAN_OBJECT, DBType.BOOLEAN);
    classifierDefaultMapping.put(EcorePackage.Literals.EBYTE, DBType.SMALLINT);
    classifierDefaultMapping.put(EcorePackage.Literals.EBYTE_OBJECT, DBType.SMALLINT);
    classifierDefaultMapping.put(EcorePackage.Literals.EBYTE_ARRAY, DBType.BLOB);
    classifierDefaultMapping.put(EcorePackage.Literals.ECHAR, DBType.CHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.ECHARACTER_OBJECT, DBType.CHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.EDATE, DBType.TIMESTAMP);
    classifierDefaultMapping.put(EcorePackage.Literals.EDOUBLE, DBType.DOUBLE);
    classifierDefaultMapping.put(EcorePackage.Literals.EDOUBLE_OBJECT, DBType.DOUBLE);
    classifierDefaultMapping.put(EcorePackage.Literals.EENUM, DBType.INTEGER);
    classifierDefaultMapping.put(EcorePackage.Literals.EFLOAT, DBType.FLOAT);
    classifierDefaultMapping.put(EcorePackage.Literals.EFLOAT_OBJECT, DBType.FLOAT);
    classifierDefaultMapping.put(EcorePackage.Literals.EINT, DBType.INTEGER);
    classifierDefaultMapping.put(EcorePackage.Literals.EINTEGER_OBJECT, DBType.INTEGER);
    classifierDefaultMapping.put(EcorePackage.Literals.ELONG, DBType.BIGINT);
    classifierDefaultMapping.put(EcorePackage.Literals.ELONG_OBJECT, DBType.BIGINT);
    classifierDefaultMapping.put(EcorePackage.Literals.ESHORT, DBType.SMALLINT);
    classifierDefaultMapping.put(EcorePackage.Literals.ESHORT_OBJECT, DBType.SMALLINT);
    classifierDefaultMapping.put(EcorePackage.Literals.ESTRING, DBType.VARCHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.EJAVA_CLASS, DBType.VARCHAR);
    classifierDefaultMapping.put(EcorePackage.Literals.EJAVA_OBJECT, DBType.BLOB);

    classifierDefaultMapping.put(EtypesPackage.Literals.BLOB, DBType.VARCHAR); // TODO Should be DBType.BLOB?
    classifierDefaultMapping.put(EtypesPackage.Literals.CLOB, DBType.VARCHAR); // TODO Should be DBType.CLOB?
  }

  @Override
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  public void registerTypeMapping(ITypeMapping.Descriptor descriptor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", descriptor);
    }

    EClassifier eClassifier = descriptor.getEClassifier();
    DBType dbType = descriptor.getDBType();
    Pair<EClassifier, DBType> sourceTargetPair = Pair.create(eClassifier, dbType);

    // currently we do not support more than one typeMapping per source-target type pair
    if (typeMappingByTypes.containsKey(sourceTargetPair))
    {
      OM.LOG.error(Messages.getString("TypeMappingRegistry.4"));
      return;
    }

    if (typeMappingsById.containsKey(descriptor.getID()))
    {
      OM.LOG.error(MessageFormat.format(Messages.getString("TypeMappingRegistry.5"), descriptor.getID()));
      return;
    }

    typeMappingsById.put(descriptor.getID(), descriptor);

    // register first dbType for classifier as default
    if (!classifierDefaultMapping.containsKey(eClassifier))
    {
      classifierDefaultMapping.put(eClassifier, dbType);
    }

    typeMappingByTypes.put(sourceTargetPair, descriptor);
  }

  @Override
  public ITypeMapping createTypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature)
  {
    ITypeMapping typeMapping = null;
    if (feature instanceof EReference)
    {
      IIDHandler idHandler = mappingStrategy.getStore().getIDHandler();
      typeMapping = idHandler.getObjectTypeMapping();
      typeMapping.setDBType(idHandler.getDBType());
    }
    else
    {
      DBType dbType = getDBType(mappingStrategy, feature);

      ITypeMapping.Descriptor descriptor = null;

      String typeMappingID = DBAnnotation.TYPE_MAPPING.getValue(feature);
      if (typeMappingID != null)
      {
        // lookup annotated mapping
        descriptor = typeMappingsById.get(typeMappingID);

        if (descriptor == null)
        {
          OM.LOG.warn(MessageFormat.format(Messages.getString("TypeMappingRegistry.2"), //
              typeMappingID, feature.toString()));
        }
      }

      if (descriptor == null)
      {
        // try to find suitable mapping by type
        descriptor = getMappingByType(feature, dbType);
      }

      if (descriptor == null)
      {
        EClassifier type = getEType(feature);
        throw new IllegalStateException(MessageFormat.format(Messages.getString("TypeMappingRegistry.1"),
            feature.getEContainingClass().getName() + "." + feature.getName(), type.getEPackage().getName() + "." + type.getName(), dbType.getKeyword()));
      }

      IFactory factory = getContainer().getFactory(ITypeMapping.Factory.PRODUCT_GROUP, descriptor.getFactoryType());
      typeMapping = (ITypeMapping)factory.create(null);
      typeMapping.setDBType(dbType);
    }

    typeMapping.setMappingStrategy(mappingStrategy);
    typeMapping.setFeature(feature);

    return typeMapping;
  }

  private EClassifier getEType(EStructuralFeature feature)
  {
    EClassifier classifier = feature.getEType();
    if (classifier instanceof EEnum)
    {
      return EcorePackage.Literals.EENUM;
    }

    if (classifier instanceof EClass)
    {
      return EcorePackage.Literals.ECLASS;
    }

    EPackage ePackage = classifier.getEPackage();
    if (CDOModelUtil.isCorePackage(ePackage))
    {
      return classifier;
    }

    if (CDOModelUtil.isTypesPackage(ePackage))
    {
      String name = classifier.getName();
      if (CDOModelUtil.BLOB_CLASS_NAME.equals(name))
      {
        return classifier;
      }

      if (CDOModelUtil.CLOB_CLASS_NAME.equals(name))
      {
        return classifier;
      }
    }

    return CUSTOM_DATA_TYPE;
  }

  private DBType getDBType(IMappingStrategy mappingStrategy, EStructuralFeature feature)
  {
    DBType dbType;

    String typeKeyword = DBAnnotation.COLUMN_TYPE.getValue(feature);
    if (typeKeyword != null)
    {
      dbType = DBType.getTypeByKeyword(typeKeyword);
      if (dbType == null)
      {
        throw new IllegalArgumentException("Unsupported columnType (" + typeKeyword + ") annotation of feature " + feature.getName());
      }
    }
    else
    {
      // No annotation present - lookup default DB type.
      IDBAdapter dbAdapter = mappingStrategy.getStore().getDBAdapter();
      dbType = getDefaultDBType(getEType(feature), dbAdapter);
    }

    ColumnTypeModifier columnTypeModifier = getColumnTypeModifier(mappingStrategy);
    if (columnTypeModifier != null)
    {
      dbType = columnTypeModifier.modify(this, mappingStrategy, feature, dbType);
    }

    return dbType;
  }

  private ColumnTypeModifier getColumnTypeModifier(IMappingStrategy mappingStrategy)
  {
    String factoryType = mappingStrategy.getProperties().get(IMappingStrategy.Props.COLUMN_TYPE_MODIFIER);
    if (factoryType == null)
    {
      factoryType = mappingStrategy.getStore().getDBAdapter().getName();
    }

    ColumnTypeModifier columnTypeModifier = null;

    try
    {
      columnTypeModifier = (ColumnTypeModifier)IPluginContainer.INSTANCE.getElement(ColumnTypeModifier.Factory.PRODUCT_GROUP, factoryType, null);
    }
    catch (FactoryNotFoundException ex)
    {
      //$FALL-THROUGH$
    }
    catch (ProductCreationException ex)
    {
      //$FALL-THROUGH$
    }
    return columnTypeModifier;
  }

  private DBType getDefaultDBType(EClassifier type, IDBAdapter dbAdapter)
  {
    DBType result = classifierDefaultMapping.get(type);

    if (result == null)
    {
      result = DBType.VARCHAR;
    }

    // Give the DBAdapter a chance to override the default type, if it's not supported
    return dbAdapter.adaptType(result);
  }

  private ITypeMapping.Descriptor getMappingByType(EStructuralFeature feature, DBType dbType)
  {
    // First try: lookup specific mapping for the immediate type.
    ITypeMapping.Descriptor descriptor = typeMappingByTypes.get(Pair.create(feature.getEType(), dbType));

    if (descriptor == null)
    {
      // Second try: lookup general mapping
      descriptor = typeMappingByTypes.get(Pair.create(getEType(feature), dbType));
      if (descriptor == null)
      {
        // Lookup failed. Give up
        return null;
      }
    }

    return descriptor;
  }

  @Deprecated
  @Override
  public Collection<DBType> getDefaultFeatureMapDBTypes()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends ITypeMapping.Provider.Factory
  {
    private static final String TYPE = "registry";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public ITypeMapping.Provider create(String description) throws ProductCreationException
    {
      return ITypeMapping.Provider.INSTANCE;
    }
  }

  /**
   * Keeps the {@link TypeMappingRegistry} in sync with {@link IManagedContainer#getFactoryRegistry()}.
   *
   * @author Stefan Winkler
   */
  private class RegistryPopulator implements IListener
  {
    private IManagedContainer container = getContainer();

    public RegistryPopulator()
    {
    }

    /**
     * Connect to the factory registry.
     */
    public void connect()
    {
      populateTypeMappingRegistry();
      container.getFactoryRegistry().addListener(this);
    }

    public void disconnect()
    {
      container.getFactoryRegistry().removeListener(this);
    }

    private void populateTypeMappingRegistry()
    {
      // Get available factory types
      Set<String> factoryTypes = container.getFactoryTypes(ITypeMapping.Factory.PRODUCT_GROUP);

      // Parse the descriptor of each factory type
      for (String factoryType : factoryTypes)
      {
        registerFactoryType(factoryType);
      }
    }

    private void registerFactoryType(String factoryType)
    {
      ITypeMapping.Descriptor desc;

      try
      {
        desc = TypeMappingUtil.descriptorFromFactoryType(factoryType);
        registerTypeMapping(desc);
      }
      catch (FactoryTypeParserException ex)
      {
        OM.LOG.warn(ex);
      }
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof ContainerEvent<?>)
      {
        @SuppressWarnings("unchecked")
        ContainerEvent<Map.Entry<IFactoryKey, IFactory>> ev = (ContainerEvent<Map.Entry<IFactoryKey, IFactory>>)event;

        for (IContainerDelta<Map.Entry<IFactoryKey, IFactory>> delta : ev.getDeltas())
        {
          IFactoryKey key = delta.getElement().getKey();
          String productGroup = key.getProductGroup();
          if (productGroup.equals(ITypeMapping.Factory.PRODUCT_GROUP))
          {
            if (delta.getKind() == Kind.ADDED)
            {
              String factoryType = delta.getElement().getKey().getType();
              registerFactoryType(factoryType);
            }
            else
            // delta.getKind() == Kind.REMOVED
            {
              // XXX Runtime removal of typeMappingFactories removal of type mappings is currently not supported.
              OM.LOG.warn(Messages.getString("TypeMappingRegistry.3"));
            }
          }
        }
      }
    }
  }
}
