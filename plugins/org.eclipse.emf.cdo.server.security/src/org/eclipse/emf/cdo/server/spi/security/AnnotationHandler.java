/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.security.Check;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.security.ISecurityManager.RealmOperation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.StringTokenizer;

/**
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

  public void init(InternalSecurityManager securityManager, boolean firstTime)
  {
    if (firstTime)
    {
      CDOPackageRegistry packageRegistry = securityManager.getRepository().getPackageRegistry();
      handlePackageUnits(securityManager, packageRegistry.getPackageUnits());
    }
  }

  public void handleCommit(InternalSecurityManager securityManager, CommitContext commitContext, User user)
  {
    handlePackageUnits(securityManager, commitContext.getNewPackageUnits());
  }

  protected void handlePackageUnits(InternalSecurityManager securityManager, final CDOPackageUnit[] packageUnits)
  {
    securityManager.modify(new RealmOperation()
    {
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
    handlePackagePermission(realm, ePackage, READ_KEY, Permission.READ);
    handlePackagePermission(realm, ePackage, WRITE_KEY, Permission.WRITE);

    for (EClassifier eClassifier : ePackage.getEClassifiers())
    {
      if (eClassifier instanceof EClass)
      {
        EClass eClass = (EClass)eClassifier;
        handleClassPermission(realm, eClass, READ_KEY, Permission.READ);
        handleClassPermission(realm, eClass, WRITE_KEY, Permission.WRITE);
      }
    }
  }

  protected void handlePackagePermission(Realm realm, EPackage ePackage, String key, Permission permission)
  {
    EClass checkClass = SecurityPackage.Literals.PACKAGE_CHECK;
    EReference checkFeature = SecurityPackage.Literals.PACKAGE_CHECK__APPLICABLE_PACKAGE;
    handlePermission(realm, ePackage, key, permission, checkClass, checkFeature);
  }

  protected void handleClassPermission(Realm realm, EClass eClass, String key, Permission permission)
  {
    EClass checkClass = SecurityPackage.Literals.CLASS_CHECK;
    EReference checkFeature = SecurityPackage.Literals.CLASS_CHECK__APPLICABLE_CLASS;
    handlePermission(realm, eClass, key, permission, checkClass, checkFeature);
  }

  protected void handlePermission(Realm realm, EModelElement modelElement, String key, Permission permission,
      EClass checkClass, EReference checkFeature)
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
        Check check = (Check)EcoreUtil.create(checkClass);
        check.setPermission(permission);
        check.eSet(checkFeature, modelElement);

        Role role = RealmUtil.findRole(items, token);
        if (role == null)
        {
          role = SecurityFactory.eINSTANCE.createRole();
          role.setId(token);
          items.add(role);
        }

        role.getChecks().add(check);
      }
    }
  }
}
