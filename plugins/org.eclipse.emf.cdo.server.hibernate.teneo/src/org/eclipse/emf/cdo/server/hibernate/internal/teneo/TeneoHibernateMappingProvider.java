/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.hibernate.internal.teneo;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.etypes.EtypesPackage;
import org.eclipse.emf.cdo.server.hibernate.internal.teneo.bundle.OM;
import org.eclipse.emf.cdo.server.hibernate.teneo.CDOMappingGenerator;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateConstants;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateMappingProvider;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStore;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOBlobUserType;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOClobUserType;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.teneo.Constants;
import org.eclipse.emf.teneo.PackageRegistryProvider;
import org.eclipse.emf.teneo.PersistenceOptions;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

/**
 * Uses the ecore string in the ePackages of the store to generate a mapping.
 * 
 * @author Martin Taal
 * @author Eike Stepper
 * @since 3.0
 */
public class TeneoHibernateMappingProvider extends HibernateMappingProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, TeneoHibernateMappingProvider.class);

  private Map<String, String> extensions = new HashMap<String, String>();

  private Properties mappingProviderProperties = new Properties();

  public TeneoHibernateMappingProvider()
  {
  }

  public void putExtension(String extensionClassName, String extendingClassName)
  {
    extensions.put(extensionClassName, extendingClassName);
  }

  @Override
  public HibernateStore getHibernateStore()
  {
    return (HibernateStore)super.getHibernateStore();
  }

  public String getMapping()
  {
    final String mapping = generateMapping();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Generated hibernate mapping:"); //$NON-NLS-1$
      TRACER.trace(mapping);
    }

    return mapping;
  }

  // the passed modelObjects collection is defined as a collection of Objects
  // to prevent binary dependency on emf.
  public String generateMapping()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Generating Hibernate Mapping"); //$NON-NLS-1$
    }

    final Properties storeProperties = getHibernateStore().getProperties();

    // merge the store properties with the mapping provider properties
    // the mapping provider props take precedence
    final Properties properties = new Properties();

    properties.putAll(storeProperties);
    properties.putAll(mappingProviderProperties);

    PackageRegistryProvider.getInstance().setThreadPackageRegistry(
        getHibernateStore().getRepository().getPackageRegistry());

    // translate the list of EPackages to an array
    final List<EPackage> epacks = getHibernateStore().getPackageHandler().getEPackages();
    final ListIterator<EPackage> iterator = epacks.listIterator();
    while (iterator.hasNext())
    {
      final EPackage epack = iterator.next();
      if (CDOModelUtil.isSystemPackage(epack))
      {
        iterator.remove();
      }
    }

    addUniqueConstraintAnnotation();

    final EPackage[] ePackageArray = epacks.toArray(new EPackage[epacks.size()]);
    // remove the persistence xml if no epackages as this won't work without
    // epackages
    if (ePackageArray.length == 0 && properties.getProperty(PersistenceOptions.PERSISTENCE_XML) != null)
    {
      properties.remove(PersistenceOptions.PERSISTENCE_XML);
    }

    // add some annotations to the CDO model so that the mapping gets generated correctly
    addTypeAnnotationToEDataType(EtypesPackage.eINSTANCE.getBlob(), CDOBlobUserType.class.getName());
    addTypeAnnotationToEDataType(EtypesPackage.eINSTANCE.getClob(), CDOClobUserType.class.getName());

    final CDOMappingGenerator mappingGenerator = new CDOMappingGenerator();
    mappingGenerator.getExtensions().putAll(extensions);
    String hbm = mappingGenerator.generateMapping(ePackageArray, properties);
    // to solve an issue with older versions of teneo
    hbm = hbm.replaceAll("_cont", "cont"); //$NON-NLS-1$ //$NON-NLS-2$

    return hbm;
  }

  private void addTypeAnnotationToEDataType(EDataType eDataType, String type)
  {
    if (eDataType.getEAnnotation(Constants.ANNOTATION_SOURCE_TENEO_JPA) != null)
    {
      return;
    }

    final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
    eAnnotation.setSource(Constants.ANNOTATION_SOURCE_TENEO_JPA);
    final String typeAnnotation = "@Type(type=\"" + type + "\")";
    eAnnotation.getDetails().put("value", typeAnnotation);
    eDataType.getEAnnotations().add(eAnnotation);
  }

  // see the CDOEntityMapper, there an explicit unique-key is added to
  // a column also
  private void addUniqueConstraintAnnotation()
  {
    final EClass eClass = EresourcePackage.eINSTANCE.getCDOResourceNode();
    // already been here
    if (eClass.getEAnnotation("teneo.jpa") != null) //$NON-NLS-1$
    {
      return;
    }

    final EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("teneo.jpa"); //$NON-NLS-1$
    final String tableAnnotation = "@Table(uniqueConstraints={@UniqueConstraint(columnNames={\"" //$NON-NLS-1$
        + CDOHibernateConstants.CONTAINER_PROPERTY_COLUMN + "\", \"" //$NON-NLS-1$
        + EresourcePackage.eINSTANCE.getCDOResourceNode_Name().getName() + "\"})})"; //$NON-NLS-1$
    annotation.getDetails().put("value", tableAnnotation); //$NON-NLS-1$
    eClass.getEAnnotations().add(annotation);
  }

  public Properties getMappingProviderProperties()
  {
    return mappingProviderProperties;
  }

  public void setMappingProviderProperties(Properties mappingProviderProperties)
  {
    this.mappingProviderProperties = mappingProviderProperties;
  }
}
