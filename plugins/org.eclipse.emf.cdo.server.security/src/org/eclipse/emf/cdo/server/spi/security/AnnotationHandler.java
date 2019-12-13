/*
 * Copyright (c) 2012-2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.spi.security;

import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager.RealmOperation;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager.CommitHandler;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.StringTokenizer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 */
public class AnnotationHandler implements InternalSecurityManager.CommitHandler
{
  public static final String SOURCE_URI = "http://www.eclipse.org/CDO/Security";

  public static final String READ_KEY = "read";

  public static final String WRITE_KEY = "write";

  public static final String DELIMITERS = " ,;|";

  public AnnotationHandler()
  {
  }

  @Override
  public void init(InternalSecurityManager securityManager, boolean firstTime)
  {
    if (firstTime)
    {
      CDOPackageRegistry packageRegistry = securityManager.getRepository().getPackageRegistry();
      handlePackageUnits(securityManager, packageRegistry.getPackageUnits());
    }
  }

  @Override
  public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user)
  {
    handlePackageUnits(securityManager, commitContext.getNewPackageUnits());
  }

  protected void handlePackageUnits(InternalSecurityManager securityManager, final CDOPackageUnit[] packageUnits)
  {
    securityManager.modify(new RealmOperation()
    {
      @Override
      public void execute(Realm realm)
      {
        if (packageUnits != null && packageUnits.length != 0)
        {
          for (CDOPackageUnit packageUnit : packageUnits)
          {
            for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
            {
              EPackage ePackage = packageInfo.getEPackage();
              handlePackage(realm, ePackage);
            }
          }
        }
      }
    });
  }

  protected void handlePackage(Realm realm, EPackage ePackage)
  {
    handlePackagePermission(realm, ePackage, READ_KEY, Access.READ);
    handlePackagePermission(realm, ePackage, WRITE_KEY, Access.WRITE);

    for (EClassifier eClassifier : ePackage.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        handleClassPermission(realm, eClass, READ_KEY, Access.READ);
        handleClassPermission(realm, eClass, WRITE_KEY, Access.WRITE);
      }
    }
  }

  protected void handlePackagePermission(Realm realm, EPackage ePackage, String key, Access access)
  {
    EClass filterClass = SecurityPackage.Literals.PACKAGE_FILTER;
    EReference filterFeature = SecurityPackage.Literals.PACKAGE_FILTER__APPLICABLE_PACKAGE;
    handlePermission(realm, ePackage, key, access, filterClass, filterFeature);
  }

  protected void handleClassPermission(Realm realm, EClass eClass, String key, Access access)
  {
    EClass filterClass = SecurityPackage.Literals.CLASS_FILTER;
    EReference filterFeature = SecurityPackage.Literals.CLASS_FILTER__APPLICABLE_CLASS;
    handlePermission(realm, eClass, key, access, filterClass, filterFeature);
  }

  protected void handlePermission(Realm realm, EModelElement modelElement, String key, Access access, EClass filterClass, EReference filterFeature)
  {
    String annotation = EcoreUtil.getAnnotation(modelElement, SOURCE_URI, key);
    if (annotation == null || annotation.length() == 0)
    {
      return;
    }

    EList<SecurityItem> items = realm.getItems();

    StringTokenizer tokenizer = new StringTokenizer(annotation, DELIMITERS);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token != null && token.length() != 0)
      {
        PermissionFilter filter = (PermissionFilter)EcoreUtil.create(filterClass);
        filter.eSet(filterFeature, modelElement);

        FilterPermission permission = SecurityFactory.eINSTANCE.createFilterPermission(access, filter);

        Role role = RealmUtil.findRole(items, token);
        if (role == null)
        {
          role = SecurityFactory.eINSTANCE.createRole();
          role.setId(token);
          items.add(role);
        }

        role.getPermissions().add(permission);
      }
    }
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }

  /**
   * Creates {@link AnnotationHandler} instances.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public static class Factory extends InternalSecurityManager.CommitHandler.Factory
  {
    public Factory()
    {
      super("annotation");
    }

    @Override
    public CommitHandler create(String description) throws ProductCreationException
    {
      return new AnnotationHandler();
    }
  }
}
