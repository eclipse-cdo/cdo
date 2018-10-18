/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.INamingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DefaultNamingStrategy implements INamingStrategy
{
  public static final String NAME_SEPARATOR = "_"; //$NON-NLS-1$

  public static final String TYPE_PREFIX_FEATURE = "F"; //$NON-NLS-1$

  public static final String TYPE_PREFIX_CLASS = "C"; //$NON-NLS-1$

  public static final String TYPE_PREFIX_PACKAGE = "P"; //$NON-NLS-1$

  public static final String GENERAL_PREFIX = "X"; //$NON-NLS-1$

  public static final String GENERAL_SUFFIX = "0"; //$NON-NLS-1$

  /**
   * Prefix for unsettable feature helper columns.
   */
  public static final String CDO_SET_PREFIX = "cdo_set_"; //$NON-NLS-1$

  public static final String FEATURE_TABLE_SUFFIX = "_list"; //$NON-NLS-1$

  private IMappingStrategy mappingStrategy;

  public DefaultNamingStrategy()
  {
  }

  public void initialize(IMappingStrategy mappingStrategy)
  {
    this.mappingStrategy = mappingStrategy;
  }

  public String getTableName(ENamedElement element)
  {
    String name = null;
    String typePrefix = null;

    if (element instanceof EClass)
    {
      typePrefix = TYPE_PREFIX_CLASS;
      name = DBAnnotation.TABLE_NAME.getValue(element);
      if (name == null)
      {
        name = isQualifiedNames() ? EMFUtil.getQualifiedName((EClass)element, NAME_SEPARATOR) : element.getName();
      }
    }
    else if (element instanceof EPackage)
    {
      typePrefix = TYPE_PREFIX_PACKAGE;
      name = DBAnnotation.TABLE_NAME.getValue(element);
      if (name == null)
      {
        name = isQualifiedNames() ? EMFUtil.getQualifiedName((EPackage)element, NAME_SEPARATOR) : element.getName();
      }
    }
    else
    {
      throw new IllegalArgumentException("Unknown element: " + element); //$NON-NLS-1$
    }

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    prefix += getTableNamePrefix(element);

    String suffix = typePrefix + getUniqueID(element);
    int maxTableNameLength = getMaxTableNameLength();

    return getName(prefix + name, suffix, maxTableNameLength);
  }

  public String getTableName(EClass eClass, EStructuralFeature feature)
  {
    String name = DBAnnotation.TABLE_NAME.getValue(eClass);
    if (name == null)
    {
      name = isQualifiedNames() ? EMFUtil.getQualifiedName(eClass, NAME_SEPARATOR) : eClass.getName();
    }

    name += NAME_SEPARATOR;
    name += feature.getName();
    name += FEATURE_TABLE_SUFFIX;

    String prefix = getTableNamePrefix();
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    prefix += getTableNamePrefix(feature);

    String suffix = TYPE_PREFIX_FEATURE + getUniqueID(feature);
    int maxTableNameLength = getMaxTableNameLength();

    return getName(prefix + name, suffix, maxTableNameLength);
  }

  public String getFieldName(EStructuralFeature feature)
  {
    String name = DBAnnotation.COLUMN_NAME.getValue(feature);
    if (name == null)
    {
      name = getName(feature.getName(), TYPE_PREFIX_FEATURE + getUniqueID(feature), getMaxFieldNameLength());
    }

    return name;
  }

  public String getUnsettableFieldName(EStructuralFeature feature)
  {
    String name = DBAnnotation.COLUMN_NAME.getValue(feature);
    if (name != null)
    {
      return CDO_SET_PREFIX + name;
    }

    return getName(CDO_SET_PREFIX + feature.getName(), TYPE_PREFIX_FEATURE + getUniqueID(feature), getMaxFieldNameLength());
  }

  protected int getMaxTableNameLength()
  {
    String value = mappingStrategy.getProperties().get(IMappingStrategy.Props.MAX_TABLE_NAME_LENGTH);
    return value == null ? mappingStrategy.getStore().getDBAdapter().getMaxTableNameLength() : Integer.valueOf(value);
  }

  protected int getMaxFieldNameLength()
  {
    String value = mappingStrategy.getProperties().get(IMappingStrategy.Props.MAX_FIELD_NAME_LENGTH);
    return value == null ? mappingStrategy.getStore().getDBAdapter().getMaxFieldNameLength() : Integer.valueOf(value);
  }

  protected boolean isQualifiedNames()
  {
    String value = mappingStrategy.getProperties().get(IMappingStrategy.Props.QUALIFIED_NAMES);
    return value == null ? false : Boolean.valueOf(value);
  }

  protected boolean isForceNamesWithID()
  {
    String value = mappingStrategy.getProperties().get(IMappingStrategy.Props.FORCE_NAMES_WITH_ID);
    return value == null ? false : Boolean.valueOf(value);
  }

  protected String getTableNamePrefix()
  {
    String value = mappingStrategy.getProperties().get(IMappingStrategy.Props.TABLE_NAME_PREFIX);
    return StringUtil.safe(value);
  }

  protected String getTableNamePrefix(EModelElement element)
  {
    String prefix = StringUtil.safe(DBAnnotation.TABLE_NAME_PREFIX.getValue(element));
    if (prefix.length() != 0 && !prefix.endsWith(NAME_SEPARATOR))
    {
      prefix += NAME_SEPARATOR;
    }

    EObject eContainer = element.eContainer();
    if (eContainer instanceof EModelElement)
    {
      EModelElement parent = (EModelElement)eContainer;
      prefix = getTableNamePrefix(parent) + prefix;
    }

    return prefix;
  }

  protected String getName(String name, String suffix, int maxLength)
  {
    if (!isValidFirstChar(name))
    {
      name = GENERAL_PREFIX + name;
    }

    boolean forceNamesWithID = isForceNamesWithID();
    if (!forceNamesWithID && isReservedWord(name))
    {
      name = name + GENERAL_SUFFIX;
    }

    if (name.length() > maxLength || forceNamesWithID)
    {
      suffix = NAME_SEPARATOR + suffix.replace('-', 'S');
      int length = Math.min(name.length(), maxLength - suffix.length());
      if (length < 0)
      {
        // Most likely CDOIDs are client side-assigned, i.e., meta IDs are extrefs. See getUniqueID()
        throw new IllegalStateException("Suffix is too long: " + suffix);
      }

      name = name.substring(0, length) + suffix;
    }

    return name;
  }

  protected String getUniqueID(ENamedElement element)
  {
    long timeStamp;
    CommitContext commitContext = StoreThreadLocal.getCommitContext();
    if (commitContext != null)
    {
      timeStamp = commitContext.getBranchPoint().getTimeStamp();
    }
    else
    {
      // This happens outside a commit, i.e. at system init time.
      // Ensure that resulting ext refs are not replicated!
      timeStamp = CDOBranchPoint.INVALID_DATE;
      // timeStamp = getStore().getRepository().getTimeStamp();
    }

    CDOID result = getMetaID(element, timeStamp);

    StringBuilder builder = new StringBuilder();
    CDOIDUtil.write(builder, result);
    return builder.toString();
  }

  protected boolean isValidFirstChar(String name)
  {
    return mappingStrategy.getStore().getDBAdapter().isValidFirstChar(name.charAt(0));
  }

  protected boolean isReservedWord(String name)
  {
    return mappingStrategy.getStore().getDBAdapter().isReservedWord(name);
  }

  protected CDOID getMetaID(ENamedElement element, long timeStamp)
  {
    return mappingStrategy.getStore().getMetaDataManager().getMetaID(element, timeStamp);
  }

  /**
   * @author Eike Stepper
   */
  public static final class Factory extends INamingStrategy.Factory
  {
    private static final String TYPE = "default";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public INamingStrategy create(Map<String, String> properties) throws ProductCreationException
    {
      return new DefaultNamingStrategy();
    }
  }
}
