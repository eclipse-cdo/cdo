/*
 * Copyright (c) 2009-2013, 2015, 2016, 2018-2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Simon McDuff - maintenance
 *    Christian W. Damus (CEA) - support registered dynamic UML profiles
 *    Christian W. Damus (CEA) - don't process EAnnotations for proxy resolution
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.ZIPUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Various static helper methods for dealing with EMF meta models.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public final class EMFUtil
{
  /**
   * @since 3.0
   */
  public static final String CDO_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/CDO";

  /**
   * @since 3.0
   */
  public static final String CDO_ANNOTATION_KEY_PERSISTENT = "persistent";

  /**
   * @since 4.2
   */
  public static final EReference EOPERATION_EEXCEPTIONS = EcorePackage.eINSTANCE.getEOperation_EExceptions();

  /**
   * @since 4.2
   */
  public static final EReference ETYPED_ELEMENT_ETYPE = EcorePackage.eINSTANCE.getETypedElement_EType();

  /**
   * @since 4.2
   */
  public static final EReference ECLASS_ESUPER_TYPES = EcorePackage.eINSTANCE.getEClass_ESuperTypes();

  /**
   * @since 4.2
   */
  public static final EAttribute ECLASSIFIER_INSTANCE_CLASS_NAME = EcorePackage.eINSTANCE.getEClassifier_InstanceClassName();

  /**
   * @since 4.2
   */
  public static final EReference EOPERATION_EGENERIC_EXCEPTIONS = EcorePackage.eINSTANCE.getEOperation_EGenericExceptions();

  /**
   * @since 4.2
   */
  public static final EReference ETYPED_ELEMENT_EGENERIC_TYPE = EcorePackage.eINSTANCE.getETypedElement_EGenericType();

  /**
   * @since 4.2
   */
  public static final EReference ECLASS_EGENERIC_SUPER_TYPES = EcorePackage.eINSTANCE.getEClass_EGenericSuperTypes();

  /**
   * @since 4.2
   */
  public static final EAttribute ECLASSIFIER_INSTANCE_TYPE_NAME = EcorePackage.eINSTANCE.getEClassifier_InstanceTypeName();

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link EMFPredicates#ATTRIBUTES}.
   */
  @Deprecated
  public static final org.eclipse.net4j.util.Predicate<EStructuralFeature> ATTRIBUTES = new org.eclipse.net4j.util.Predicate<EStructuralFeature>()
  {
    @SuppressWarnings("deprecation")
    @Override
    public boolean apply(EStructuralFeature feature)
    {
      return feature instanceof EAttribute;
    }
  };

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link EMFPredicates#REFERENCES}.
   */
  @Deprecated
  public static final org.eclipse.net4j.util.Predicate<EStructuralFeature> REFERENCES = new org.eclipse.net4j.util.Predicate<EStructuralFeature>()
  {
    @SuppressWarnings("deprecation")
    @Override
    public boolean apply(EStructuralFeature feature)
    {
      return feature instanceof EReference;
    }
  };

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link EMFPredicates#CONTAINER_REFERENCES}.
   */
  @Deprecated
  public static final org.eclipse.net4j.util.Predicate<EStructuralFeature> CONTAINER_REFERENCES = new org.eclipse.net4j.util.Predicate<EStructuralFeature>()
  {
    @SuppressWarnings("deprecation")
    @Override
    public boolean apply(EStructuralFeature feature)
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        return reference.isContainer();
      }

      return false;
    }
  };

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link EMFPredicates#CROSS_REFERENCES}.
   */
  @Deprecated
  public static final org.eclipse.net4j.util.Predicate<EStructuralFeature> CROSS_REFERENCES = new org.eclipse.net4j.util.Predicate<EStructuralFeature>()
  {
    @SuppressWarnings("deprecation")
    @Override
    public boolean apply(EStructuralFeature feature)
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        return !(reference.isContainer() || reference.isContainment());
      }

      return false;
    }
  };

  /**
   * @since 4.2
   * @deprecated As of 4.9 use {@link EMFPredicates#CONTAINMENT_REFERENCES}.
   */
  @Deprecated
  public static final org.eclipse.net4j.util.Predicate<EStructuralFeature> CONTAINMENT_REFERENCES = new org.eclipse.net4j.util.Predicate<EStructuralFeature>()
  {
    @SuppressWarnings("deprecation")
    @Override
    public boolean apply(EStructuralFeature feature)
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        return reference.isContainment();
      }

      return false;
    }
  };

  private static final EPackage.Registry[] GLOBAL_REGISTRY_ARRAY = { EPackage.Registry.INSTANCE };

  private static final XMLResource.URIHandler ABSOLUTE_URI_PRESERVING_URI_HANDLER = new AbsoluteURIPreservingURIHandler();

  private static final boolean CONVERT_TO_RELATIVE_URIS = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.common.model.EMFUtil.CONVERT_TO_RELATIVE_URIS");

  private EMFUtil()
  {
  }

  /**
   * @since 4.26
   */
  public static <T extends EObject> T getNearestObject(EObject object, Class<T> type)
  {
    while (object != null)
    {
      if (type.isInstance(object))
      {
        return type.cast(object);
      }

      object = object.eContainer();
    }

    return null;
  }

  /**
   * @since 4.2
   */
  public static URI getPositionalURI(InternalEObject internalEObject)
  {
    List<String> uriFragmentPath = new ArrayList<>();
    Resource resource;
    for (InternalEObject container = internalEObject.eInternalContainer(); (resource = internalEObject.eDirectResource()) == null
        && container != null; container = internalEObject.eInternalContainer())
    {
      String segment = getPositionalURIFragmentSegment(container, internalEObject.eContainingFeature(), internalEObject);
      uriFragmentPath.add(segment);
      internalEObject = container;
    }

    StringBuilder builder = new StringBuilder("/");
    builder.append(resource.getContents().indexOf(internalEObject));
    for (int i = uriFragmentPath.size() - 1; i >= 0; --i)
    {
      builder.append('/');
      builder.append(uriFragmentPath.get(i));
    }

    return resource.getURI().appendFragment(builder.toString());
  }

  /**
   * @since 4.2
   */
  private static String getPositionalURIFragmentSegment(EObject container, EStructuralFeature eStructuralFeature, InternalEObject eObject)
  {
    StringBuilder builder = new StringBuilder();
    builder.append('@');
    builder.append(eStructuralFeature.getName());

    if (eStructuralFeature.isMany())
    {
      EList<?> eList = (EList<?>)container.eGet(eStructuralFeature, false);
      int index = eList.indexOf(eObject);
      builder.append('.');
      builder.append(index);
    }

    return builder.toString();
  }

  public static EPackage getGeneratedEPackage(EPackage ePackage)
  {
    String packageURI = ePackage.getNsURI();
    if (packageURI.equals(EcorePackage.eINSTANCE.getNsURI()))
    {
      return EcorePackage.eINSTANCE;
    }

    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    return registry.getEPackage(packageURI);
  }

  public static Map.Entry<String, Object>[] getSortedRegistryEntries(EPackage.Registry packageRegistry)
  {
    Set<Map.Entry<String, Object>> entries = packageRegistry.entrySet();
    @SuppressWarnings("unchecked")
    Map.Entry<String, Object>[] array = entries.toArray(new Map.Entry[entries.size()]);
    Arrays.sort(array, new Comparator<Map.Entry<String, Object>>()
    {
      @Override
      public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    return array;
  }

  /**
   * @since 4.6
   */
  public static EList<EAnnotation> getAnnotations(EClass eClass, String sourceURI)
  {
    EList<EAnnotation> annotations = new BasicEList<>();
    getAnnotations(eClass, sourceURI, annotations, new HashSet<>());
    return annotations;
  }

  private static void getAnnotations(EClass eClass, String sourceURI, EList<EAnnotation> annotations, Set<EClass> visited)
  {
    if (visited.add(eClass))
    {
      for (EAnnotation annotation : eClass.getEAnnotations())
      {
        if (sourceURI == null || sourceURI.equals(annotation.getSource()))
        {
          annotations.add(annotation);
        }
      }

      for (EClass superType : eClass.getESuperTypes())
      {
        getAnnotations(superType, sourceURI, annotations, visited);
      }
    }
  }

  public static EPackage getTopLevelPackage(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    return superPackage == null ? ePackage : getTopLevelPackage(superPackage);
  }

  /**
   * @since 2.0
   */
  public static EPackage createEPackage(String name, String nsPrefix, String nsURI)
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName(name);
    ePackage.setNsPrefix(nsPrefix);
    ePackage.setNsURI(nsURI);
    return ePackage;
  }

  /**
   * @since 4.27
   */
  public static EClass createEClass(EPackage ePackage, String name)
  {
    return createEClass(ePackage, name, false, false);
  }

  /**
   * @since 2.0
   */
  public static EClass createEClass(EPackage ePackage, String name, boolean isAbstract, boolean isInterface)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    eClass.setAbstract(isAbstract);
    eClass.setInterface(isInterface);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  /**
   * @since 2.0
   */
  public static EAttribute createEAttribute(EClass eClass, String name, EClassifier type)
  {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(name);
    eAttribute.setEType(type);
    eClass.getEStructuralFeatures().add(eAttribute);
    return eAttribute;
  }

  /**
   * @since 4.27
   */
  public static EReference createEReference(EClass eClass, String name, EClassifier type)
  {
    return createEReference(eClass, name, type, false, false);
  }

  /**
   * @since 2.0
   */
  public static EReference createEReference(EClass eClass, String name, EClassifier type, boolean isRequired, boolean isMany)
  {
    EReference eReference = EcoreFactory.eINSTANCE.createEReference();
    eReference.setName(name);
    eReference.setEType(type);
    eReference.setLowerBound(isRequired ? 1 : 0);
    eReference.setUpperBound(isMany ? -1 : 0);
    eClass.getEStructuralFeatures().add(eReference);
    return eReference;
  }

  /**
   * @since 4.27
   */
  public static EEnum createEEnum(EPackage ePackage, String name)
  {
    EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
    eEnum.setName(name);
    ePackage.getEClassifiers().add(eEnum);
    return eEnum;
  }

  /**
   * @since 4.27
   */
  public static EEnum createEEnum(EPackage ePackage, String name, String... literals)
  {
    EEnum eEnum = createEEnum(ePackage, name);
    for (int i = 0; i < literals.length; i++)
    {
      createEEnumLiteral(eEnum, literals[i], i);
    }

    return eEnum;
  }

  /**
   * @since 4.27
   */
  public static EEnumLiteral createEEnumLiteral(EEnum eEnum, String name)
  {
    int value = eEnum.getELiterals().stream().mapToInt(EEnumLiteral::getValue).max().orElse(-1) + 1;
    return createEEnumLiteral(eEnum, name, value);
  }

  /**
   * @since 4.27
   */
  public static EEnumLiteral createEEnumLiteral(EEnum eEnum, String name, int value)
  {
    EEnumLiteral eEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
    eEnumLiteral.setName(name);
    eEnumLiteral.setValue(value);
    eEnum.getELiterals().add(eEnumLiteral);
    return eEnumLiteral;
  }

  /**
   * @since 4.2
   */
  public static EClass[] getConcreteClasses(EPackage ePackage)
  {
    return getConcreteClasses(ePackage, false);
  }

  /**
   * @since 4.10
   */
  public static EClass[] getConcreteClasses(EPackage ePackage, boolean includeSubPackages)
  {
    List<EClass> result = new ArrayList<>(0);
    forAllConcreteClasses(ePackage, includeSubPackages, c -> result.add(c));
    return result.toArray(new EClass[result.size()]);
  }

  /**
   * @since 4.10
   */
  public static void forAllConcreteClasses(EPackage ePackage, boolean includeSubPackages, Consumer<EClass> consumer)
  {
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        if (!eClass.isAbstract() && !eClass.isInterface())
        {
          consumer.accept(eClass);
        }
      }
    }

    if (includeSubPackages)
    {
      for (EPackage subPackage : ePackage.getESubpackages())
      {
        forAllConcreteClasses(subPackage, true, consumer);
      }
    }
  }

  /**
   * @since 4.13
   */
  public static EClass getAnyConcreteEClass(EPackage ePackage, boolean includeSubPackages)
  {
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        if (!(eClass.isAbstract() || eClass.isInterface()))
        {
          return eClass;
        }
      }
    }

    if (includeSubPackages)
    {
      for (EPackage subpackage : ePackage.getESubpackages())
      {
        EClass eClass = getAnyConcreteEClass(subpackage, true);
        if (eClass != null)
        {
          return eClass;
        }
      }
    }

    return null;
  }

  public static EClass[] getPersistentClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList<>();
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        result.add((EClass)classifier);
      }
    }

    return result.toArray(new EClass[result.size()]);
  }

  /**
   * @since 3.0
   * @deprecated This method is expensive and will be removed in the future.
   * @see #isPersistent(EStructuralFeature)
   */
  @Deprecated
  public static List<EStructuralFeature> getPersistentFeatures(EList<EStructuralFeature> eFeatures)
  {
    List<EStructuralFeature> result = new ArrayList<>();
    for (EStructuralFeature feature : eFeatures)
    {
      if (isPersistent(feature))
      {
        result.add(feature);
      }
    }

    return result;
  }

  /**
   * Returns <code>true</code> if CDO considers the given feature <i>persistent</i>, <code>false</code> otherwise.
   * <p>
   * Note that CDO <i>persistent</i> is not identical to {@link EStructuralFeature#isTransient() non-transient} because that can be
   * overridden with {@link #CDO_ANNOTATION_KEY_PERSISTENT}. Another reason for possible deviations is that CDO considers transient
   * {@link EReference references} <i>persistent</i> if they have a <i>persistent</i> {@link EReference#getEOpposite() opposite}.
   * <p>
   * Note also that the checks for the aforementioned deviations from {@link EStructuralFeature#isTransient()} make this method somewhat
   * expensive. Whenever possible {@link CDOClassInfo#isPersistent(int) CDOClassInfo.isPersistent()} should be called instead.
   *
   * @since 3.0
   */
  public static boolean isPersistent(EStructuralFeature feature)
  {
    if (feature == ECLASS_ESUPER_TYPES || feature == ETYPED_ELEMENT_ETYPE || feature == EOPERATION_EEXCEPTIONS || feature == ECLASSIFIER_INSTANCE_CLASS_NAME)
    {
      // http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
      return false;
    }

    String persistent = EcoreUtil.getAnnotation(feature, CDO_ANNOTATION_SOURCE, CDO_ANNOTATION_KEY_PERSISTENT);
    if (persistent != null)
    {
      return "true".equalsIgnoreCase(persistent);
    }

    if (feature.isTransient())
    {
      // Bug 333950: Transient eRefs with a persistent eOpposite, must be considered persistent
      if (feature instanceof EReference)
      {
        EReference eOpposite = ((EReference)feature).getEOpposite();
        if (eOpposite != null && !eOpposite.isTransient())
        {
          return true;
        }
      }

      return false;
    }

    return true;
  }

  public static boolean isDynamicEPackage(Object value)
  {
    return value.getClass() == EPackageImpl.class;
  }

  public static String getParentURI(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    return superPackage == null ? null : superPackage.getNsURI();
  }

  public static void registerPackage(EPackage ePackage, EPackage.Registry... packageRegistries)
  {
    ePackage.getClass(); // Initialize package in standalone mode

    if (packageRegistries.length == 0)
    {
      packageRegistries = GLOBAL_REGISTRY_ARRAY;
    }

    for (EPackage.Registry packageRegistry : packageRegistries)
    {
      packageRegistry.put(ePackage.getNsURI(), ePackage);
    }
  }

  public static byte[] getEPackageBytes(EPackage ePackage, boolean zipped, EPackage.Registry packageRegistry)
  {
    try
    {
      // The package may be nested in other content (e.g., a UML Profile).
      // Or, maybe it is just a dynamic package that was not loaded from a resource
      Resource resource = ((InternalEObject)ePackage).eDirectResource();
      if (resource == null)
      {
        ResourceSet resourceSet = newEcoreResourceSet(packageRegistry);
        resource = resourceSet.createResource(URI.createURI(ePackage.getNsURI()));

        // If the package is nested in some container, then copy it into the temporary
        // resource so that we don't send content that the server doesn't need
        resource.getContents().add(ePackage.eContainer() == null ? ePackage : EcoreUtil.copy(ePackage));
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      resource.save(baos, createResourceOptions(zipped));
      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * Creates an {@link EPackage} from the given byte array by loading it into a resource in the given resource set.
   * <p>
   * If <code>lookForResource</code> is <code>true</code>, then an attempt is made to find an existing resource with the given URI
   * before creating a new one.
   * <p>
   * The method automatically detects whether the byte array is zipped.
   * <p>
   * Note that the byte array is not copied, so it must not be modified while the resource is in use.
   *
   * @see #createEPackage(String, byte[], boolean, ResourceSet, boolean)
   * @since 4.27
   */
  public static EPackage createEPackage(String uri, byte[] bytes, ResourceSet resourceSet, boolean lookForResource)
  {
    boolean zipped = ZIPUtil.isZip(bytes);
    return createEPackage(uri, bytes, zipped, resourceSet, lookForResource);
  }

  /**
   * Creates an {@link EPackage} from the given byte array by loading it into a resource in the given resource set.
   * <p>
   * If <code>lookForResource</code> is <code>true</code>, then an attempt is made to find an existing resource with the given URI
   * before creating a new one.
   * <p>
   * If the byte array is zipped, then set <code>zipped</code> to <code>true</code>.
   * <p>
   * Note that the byte array is not copied, so it must not be modified while the resource is in use.
   *
   * @see #createEPackage(String, byte[], ResourceSet, boolean)
   * @since 3.0
   */
  public static EPackage createEPackage(String uri, byte[] bytes, boolean zipped, ResourceSet resourceSet, boolean lookForResource)
  {
    try
    {
      Resource resource = null;
      if (lookForResource)
      {
        resource = resourceSet.getResource(URI.createURI(uri), true);
      }

      if (resource == null)
      {
        resource = resourceSet.createResource(URI.createURI(uri));
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      resource.load(bais, createResourceOptions(zipped));

      EList<EObject> contents = resource.getContents();
      return (EPackage)contents.get(0);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private static Map<String, Object> createResourceOptions(boolean zipped)
  {
    Map<String, Object> options = new HashMap<>();
    options.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, true);

    if (!CONVERT_TO_RELATIVE_URIS)
    {
      options.put(XMLResource.OPTION_URI_HANDLER, ABSOLUTE_URI_PRESERVING_URI_HANDLER);
    }

    if (zipped)
    {
      options.put(Resource.OPTION_ZIP, true);
    }

    return options;
  }

  /**
   * @since 4.27
   */
  public static String getXMI(EPackage ePackage)
  {
    byte[] xmi = getEPackageBytes(ePackage, false, EPackage.Registry.INSTANCE);
    return new String(xmi, StandardCharsets.UTF_8);
  }

  /**
   * @since 4.2
   */
  public static <T> T getAdapter(Notifier notifier, Class<T> type)
  {
    @SuppressWarnings("unchecked")
    T adapter = (T)EcoreUtil.getAdapter(notifier.eAdapters(), type);

    return adapter;
  }

  public static void addAdapter(Notifier notifier, Adapter adapter)
  {
    synchronized (notifier)
    {
      EList<Adapter> adapters = notifier.eAdapters();
      if (!adapters.contains(adapter))
      {
        adapters.add(adapter);
      }
    }
  }

  public static EPackage[] getAllPackages(EPackage ePackage)
  {
    List<EPackage> result = new ArrayList<>();
    getAllPackages(ePackage, result::add);
    return result.toArray(new EPackage[result.size()]);
  }

  /**
   * @since 4.27
   */
  public static void getAllPackages(EPackage ePackage, Consumer<EPackage> consumer)
  {
    consumer.accept(ePackage);

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      getAllPackages(subPackage, consumer);
    }
  }

  public static String getQualifiedName(EPackage ePackage, String separator)
  {
    return getFullyQualifiedName(ePackage, separator);
  }

  public static String getQualifiedName(EClassifier classifier, String separator)
  {
    return getFullyQualifiedName(classifier, separator);
  }

  /**
   * @since 4.27
   */
  public static String getQualifiedName(EStructuralFeature feature, String separator)
  {
    return getFullyQualifiedName(feature, separator);
  }

  /**
   * @since 4.27
   */
  public static String getFullyQualifiedName(EObject modelElement)
  {
    return getFullyQualifiedName(modelElement, "."); //$NON-NLS-1$
  }

  /**
   * @since 4.27
   */
  public static String getFullyQualifiedName(EObject modelElement, String separator)
  {
    StringJoiner joiner = new StringJoiner(separator);
    getFullyQualifiedName(modelElement, joiner);
    return joiner.toString();
  }

  private static void getFullyQualifiedName(EObject modelElement, StringJoiner joiner)
  {
    if (modelElement instanceof ENamedElement)
    {
      String name = ((ENamedElement)modelElement).getName();
      getFullyQualifiedName(modelElement.eContainer(), joiner);
      joiner.add(name);
    }
  }

  public static ResourceSet newResourceSet(Resource.Factory resourceFactory)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet(EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newResourceSet(new EcoreResourceFactoryImpl());

    if (packageRegistry != null)
    {
      resourceSet.setPackageRegistry(packageRegistry);
    }

    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet()
  {
    return newEcoreResourceSet(EPackage.Registry.INSTANCE);
  }

  /**
   * @since 3.0
   */
  public static EObject safeResolve(EObject proxy, ResourceSet resourceSet)
  {
    if (!proxy.eIsProxy())
    {
      return proxy;
    }

    EObject resolved = EcoreUtil.resolve(proxy, resourceSet);
    if (resolved == proxy)
    {
      throw new IllegalStateException("Unresolvable proxy: " + ((InternalEObject)proxy).eProxyURI());
    }

    return resolved;
  }

  /**
   * @since 3.0
   */
  public static void safeResolveAll(ResourceSet resourceSet)
  {
    TreeIterator<Notifier> it = resourceSet.getAllContents();
    while (it.hasNext())
    {
      Notifier notifier = it.next();
      if (notifier instanceof EObject)
      {
        safeResolve((EObject)notifier, resourceSet);

        if (notifier instanceof EAnnotation)
        {
          // we don't need to validate the structure of annotations. The applications that
          // define annotations will have to take what they can get
          it.prune();
        }
        else
        {
          Iterator<EObject> it2 = ((EObject)notifier).eCrossReferences().iterator();
          while (it2.hasNext())
          {
            safeResolve(it2.next(), resourceSet);
          }
        }
      }
    }
  }

  /**
   * @see ExtResourceSet
   * @since 4.0
   */
  public static ExtResourceSet createExtResourceSet(InternalCDOPackageRegistry packageRegistry, boolean delegating, boolean demandLoading)
  {
    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();

    ExtResourceSet resourceSet = new ExtResourceSet(delegating, demandLoading);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  /**
   * An extension of {@link ResourceSetImpl} that allows demandLoading of resources and delegation of resource lookups,
   * to be switched on/off as desired.
   *
   * @since 4.0
   */
  public static class ExtResourceSet extends ResourceSetImpl
  {
    private boolean delegating;

    private boolean demandLoading;

    /**
     * @since 4.7
     */
    public ExtResourceSet(boolean delegating, boolean demandLoading)
    {
      this.delegating = delegating;
      this.demandLoading = demandLoading;
    }

    public boolean isDelegating()
    {
      return delegating;
    }

    public void setDelegating(boolean delegating)
    {
      this.delegating = delegating;
    }

    public boolean isDemandLoading()
    {
      return demandLoading;
    }

    public void setDemandLoading(boolean demandLoading)
    {
      this.demandLoading = demandLoading;
    }

    @Override
    protected void demandLoad(Resource resource) throws IOException
    {
      if (demandLoading)
      {
        super.demandLoad(resource);
      }
    }

    @Override
    protected Resource delegatedGetResource(URI uri, boolean loadOnDemand)
    {
      if (delegating)
      {
        return super.delegatedGetResource(uri, loadOnDemand);
      }

      return null;
    }
  }

  /**
   * A mapping between two {@link EObject}s and their contents based on their URIs
   * as returned by {@link EcoreUtil#getURI(EObject)}.
   *
   * @author Eike Stepper
   * @since 4.27
   */
  public static final class TreeMapping<T extends EObject> extends HashMap<T, T>
  {
    private static final long serialVersionUID = 1L;

    private final Class<T> type;

    private final Map<String, T> fromObjectsByURI = new HashMap<>();

    private final Map<String, T> toObjectsByURI = new HashMap<>();

    public TreeMapping(Class<T> type)
    {
      this.type = type;
    }

    public Map<String, T> getFromObjectsByURI()
    {
      return fromObjectsByURI;
    }

    public Map<String, T> getToObjectsByURI()
    {
      return toObjectsByURI;
    }

    public void map(EObject fromObject, EObject toObject)
    {
      map(fromObject, toObject, false);
    }

    public void map(EObject fromObject, EObject toObject, boolean allContents)
    {
      fillURIMap(toObject, allContents, toObjectsByURI, null);

      fillURIMap(fromObject, allContents, fromObjectsByURI, (uri, fObj) -> {
        T tObj = toObjectsByURI.get(uri);
        put(fObj, tObj);
      });
    }

    protected T filter(EObject object)
    {
      if (type.isInstance(object))
      {
        return type.cast(object);
      }

      return null;
    }

    private void fillURIMap(EObject object, boolean allContents, Map<String, T> map, BiConsumer<String, T> registrationConsumer)
    {
      if (object != null)
      {
        registerElement(object, map, registrationConsumer);

        if (allContents)
        {
          for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
          {
            EObject content = it.next();
            registerElement(content, map, registrationConsumer);
          }
        }
      }
    }

    private void registerElement(EObject object, Map<String, T> map, BiConsumer<String, T> registrationConsumer)
    {
      T t = filter(object);
      if (t != null)
      {
        String uri = EcoreUtil.getURI(t).toString();
        map.put(uri, t);

        if (registrationConsumer != null)
        {
          registrationConsumer.accept(uri, t);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class AbsoluteURIPreservingURIHandler extends URIHandlerImpl
  {
    public AbsoluteURIPreservingURIHandler()
    {
    }

    @Override
    public URI deresolve(URI uri)
    {
      if (uri.trimFragment() == baseURI)
      {
        return super.deresolve(uri);
      }

      return uri;
    }
  }
}
