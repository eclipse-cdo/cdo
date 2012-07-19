/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.codegen.util;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;

import org.eclipse.swt.widgets.Canvas;

import java.util.List;

/**
 * @author Martin Fluegge
 */
public class Utils
{
  public static String packagePath = "";

  public static String packageName = "";

  public static String diagramPluginID = "";

  public static String diagramPackage = "";

  public static String timestamp()
  {
    return String.valueOf(System.currentTimeMillis());
  }

  public static String getPackagePath()
  {
    return packagePath;
  }

  public static String getPackageName()
  {
    return packageName;
  }

  public static String getDiagramPluginID()
  {
    return diagramPluginID;
  }

  public static String setGlobals(Canvas e)
  {
    // packageName=e.getName();
    // packagePath=e.getName().replace(".", "/");
    return "";
  }

  public static String setPackage(String pName)
  {
    diagramPluginID = pName + ".diagram";
    packageName = pName.replace("class", "clazz");
    packagePath = pName.replace(".", "/");
    return "";
  }

  public static String setDiagramPackage(String pName)
  {
    diagramPackage = pName;
    return "";
  }

  public static String getDiagramPackage()
  {
    return diagramPackage;
  }

  public static String getRootPackage()
  {
    String ret = diagramPackage.replace(".diagram", "");
    return ret;
  }

  public static String getDiagramPackagePath()
  {
    return diagramPackage.replace(".", "/");
  }

  public static String getUniqueIdentifierName(String uniqueIdentifier)
  {
    String ret = uniqueIdentifier.substring(uniqueIdentifier.lastIndexOf(".") + 1, uniqueIdentifier.length());
    return ret;
  }

  public static String toModelName(String uniqueIdentifier)
  {
    String ret = uniqueIdentifier.substring(uniqueIdentifier.lastIndexOf(".") + 1, uniqueIdentifier.length());
    ret = ret.substring(0, ret.lastIndexOf("_"));
    return ret;
  }

  public static String toModelNameFromGetterName(String uniqueIdentifier)
  {
    String ret = uniqueIdentifier.replace("getFigure", "");
    return ret;
  }

  /**
   * @since 1.0
   */
  public static String getEMFFileName(List<GenPackage> genPackages)
  {
    return genPackages.get(0).getFileExtension();
  }
}
