/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingUtil.FactoryTypeParserException;
import org.eclipse.emf.cdo.server.internal.db.messages.Messages;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An implementation of both the Registry and Provider interfaces for type mappings. This class is a singleton which
 * keeps itself in sync with the global factory registry. It reads the available factoryTypes for the type mappings
 * product type and populates indexes which make it easier to determine and look up the correct factories for a needed
 * type mapping.
 * 
 * @author Stefan Winkler
 */
public class TypeMappingRegistry implements ITypeMapping.Registry, ITypeMapping.Provider
{
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
   * A set of all known mapped DBTypes. This is needed for the feature map mappings.
   */
  private Set<DBType> defaultFeatureMapDBTypes;

  /**
   * A populator which is used to keep the registry in sync with the registered factories of the
   * {@link IManagedContainer}.
   */
  private RegistryPopulator populator = null;

  public TypeMappingRegistry()
  {
    defaultFeatureMapDBTypes = new HashSet<DBType>();
    typeMappingsById = new HashMap<String, ITypeMapping.Descriptor>();
    typeMappingByTypes = new HashMap<Pair<EClassifier, DBType>, ITypeMapping.Descriptor>();
    classifierDefaultMapping = new HashMap<EClassifier, DBType>();

    registerCoreTypeMappings();

    // connect to extension registry
    populator = new RegistryPopulator();
    populator.connect();
  }

  /**
   * Register builtin type mapping factories
   */
  private void registerCoreTypeMappings()
  {
    // initialize default source and target type pairs
    IManagedContainer container = IPluginContainer.INSTANCE;

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEBigDecimal(), DBType.VARCHAR);
    container.registerFactory(CoreTypeMappings.TMBigDecimal.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEBigInteger(), DBType.VARCHAR);
    container.registerFactory(CoreTypeMappings.TMBigInteger.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEBoolean(), DBType.BOOLEAN);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEBooleanObject(), DBType.BOOLEAN);
    container.registerFactory(CoreTypeMappings.TMBoolean.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEByte(), DBType.SMALLINT);
    container.registerFactory(CoreTypeMappings.TMByte.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEByteObject(), DBType.SMALLINT);
    container.registerFactory(CoreTypeMappings.TMByte.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEByteArray(), DBType.BLOB);
    container.registerFactory(CoreTypeMappings.TMBytes.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEChar(), DBType.CHAR);
    container.registerFactory(CoreTypeMappings.TMCharacter.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getECharacterObject(), DBType.CHAR);
    container.registerFactory(CoreTypeMappings.TMCharacter.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEDataType(), DBType.VARCHAR);
    container.registerFactory(CoreTypeMappings.TMCustom.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMCustom.FACTORY_CLOB);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEDate(), DBType.TIMESTAMP);
    container.registerFactory(CoreTypeMappings.TMDate2Date.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDate2Time.FACTORY);
    container.registerFactory(CoreTypeMappings.TMDate2Timestamp.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEDouble(), DBType.DOUBLE);
    container.registerFactory(CoreTypeMappings.TMDouble.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEDoubleObject(), DBType.DOUBLE);
    container.registerFactory(CoreTypeMappings.TMDouble.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEEnum(), DBType.INTEGER);
    container.registerFactory(CoreTypeMappings.TMEnum.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEFloat(), DBType.FLOAT);
    container.registerFactory(CoreTypeMappings.TMFloat.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEFloatObject(), DBType.FLOAT);
    container.registerFactory(CoreTypeMappings.TMFloat.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEInt(), DBType.INTEGER);
    container.registerFactory(CoreTypeMappings.TMInteger.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEIntegerObject(), DBType.INTEGER);
    container.registerFactory(CoreTypeMappings.TMInteger.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getELong(), DBType.BIGINT);
    container.registerFactory(CoreTypeMappings.TMLong.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getELongObject(), DBType.BIGINT);
    container.registerFactory(CoreTypeMappings.TMLong.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEClass(), DBType.BIGINT);
    container.registerFactory(CoreTypeMappings.TMObject.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEShort(), DBType.SMALLINT);
    container.registerFactory(CoreTypeMappings.TMShort.FACTORY);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEShortObject(), DBType.SMALLINT);
    container.registerFactory(CoreTypeMappings.TMShort.FACTORY_OBJECT);

    classifierDefaultMapping.put(EcorePackage.eINSTANCE.getEString(), DBType.VARCHAR);
    container.registerFactory(CoreTypeMappings.TMString.FACTORY_VARCHAR);
    container.registerFactory(CoreTypeMappings.TMString.FACTORY_CLOB);
  }

  public void registerTypeMapping(ITypeMapping.Descriptor descriptor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering {0}", descriptor);
    }

    EClassifier eClassifier = descriptor.getEClassifier();
    DBType dbType = descriptor.getDBType();
    Pair<EClassifier, DBType> sourceTargetPair = new Pair<EClassifier, DBType>(eClassifier, dbType);

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

    defaultFeatureMapDBTypes.add(dbType);

    typeMappingByTypes.put(sourceTargetPair, descriptor);
  }

  public ITypeMapping createTypeMapping(IMappingStrategy mappingStrategy, EStructuralFeature feature)
  {
    EClassifier classifier = getEClassifier(feature);
    DBType dbType = getDBType(feature, mappingStrategy.getStore().getDBAdapter());

    String typeMappingID = DBAnnotation.TYPE_MAPPING.getValue(feature);

    ITypeMapping.Descriptor descriptor = null;

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
      throw new IllegalStateException(MessageFormat.format(Messages.getString("TypeMappingRegistry.1"),
          feature.getName(), classifier.getName(), dbType.getKeyword()));
    }

    IFactory factory = getManagedContainer()
        .getFactory(ITypeMapping.Factory.PRODUCT_GROUP, descriptor.getFactoryType());
    ITypeMapping typeMapping = (ITypeMapping)factory.create(null);
    typeMapping.setMappingStrategy(mappingStrategy);
    typeMapping.setFeature(feature);
    typeMapping.setDBType(dbType);
    return typeMapping;
  }

  private IManagedContainer getManagedContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  private EClassifier getEClassifier(EStructuralFeature feature)
  {
    EClassifier classifier = feature.getEType();
    if (classifier instanceof EEnum)
    {
      return EcorePackage.eINSTANCE.getEEnum();
    }

    if (classifier instanceof EClass)
    {
      return EcorePackage.eINSTANCE.getEClass();
    }

    if (!CDOModelUtil.isCorePackage(classifier.getEPackage()))
    {
      return EcorePackage.eINSTANCE.getEDataType();
    }

    return classifier;
  }

  private DBType getDBType(EStructuralFeature feature, IDBAdapter dbAdapter)
  {
    String typeKeyword = DBAnnotation.COLUMN_TYPE.getValue(feature);
    if (typeKeyword != null)
    {
      DBType dbType = DBType.getTypeByKeyword(typeKeyword);
      if (dbType == null)
      {
        throw new IllegalArgumentException("Unsupported columnType (" + typeKeyword + ") annotation of feature "
            + feature.getName());
      }

      return dbType;
    }

    // No annotation present - lookup default DB type.
    return getDefaultDBType(getEClassifier(feature), dbAdapter);
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
    ITypeMapping.Descriptor descriptor = typeMappingByTypes.get(new Pair<EClassifier, DBType>(feature.getEType(),
        dbType));

    if (descriptor == null)
    {
      // Second try: lookup general mapping
      descriptor = typeMappingByTypes.get(new Pair<EClassifier, DBType>(getEClassifier(feature), dbType));
      if (descriptor == null)
      {
        // Lookup failed. Give up
        return null;
      }
    }

    return descriptor;
  }

  public Collection<DBType> getDefaultFeatureMapDBTypes()
  {
    return defaultFeatureMapDBTypes;
  }

  /**
   * Keeps the {@link TypeMappingRegistry} in sync with {@link IManagedContainer#getFactoryRegistry()}.
   * 
   * @author Stefan Winkler
   */
  private class RegistryPopulator implements IListener
  {
    private IManagedContainer container;

    public RegistryPopulator()
    {
    }

    /**
     * Connect to the factory registry.
     */
    public void connect()
    {
      container = getManagedContainer();
      populateTypeMappingRegistry();
      container.getFactoryRegistry().addListener(this);
    }

    private void populateTypeMappingRegistry()
    {
      // get available factory types
      Set<String> factoryTypes = container.getFactoryTypes(ITypeMapping.Factory.PRODUCT_GROUP);

      // parse the descriptor of each factory type
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

    public void notifyEvent(IEvent event)
    {
      if (event instanceof ContainerEvent<?>)
      {
        @SuppressWarnings("unchecked")
        ContainerEvent<Map.Entry<IFactoryKey, IFactory>> ev = (ContainerEvent<Entry<IFactoryKey, IFactory>>)event;

        for (IContainerDelta<Map.Entry<IFactoryKey, IFactory>> delta : ev.getDeltas())
        {
          IFactoryKey key = delta.getElement().getKey();
          if (key.getProductGroup().equals(ITypeMapping.Factory.PRODUCT_GROUP))
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
