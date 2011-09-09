/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article;

import com.sun.javadoc.PackageDoc;


/**
 * @author Eike Stepper
 */
public class Category extends CategoryElement
{
  private final String name;

  private PackageDoc packageDoc;

  Category(CategoryElement parent, String name)
  {
    super(parent);
    this.name = name;
  }

  public final String getName()
  {
    return name;
  }

  public final PackageDoc getPackageDoc()
  {
    return packageDoc;
  }

  @Override
  public String getPath()
  {
    Category parent = getParent();
    if (parent == null)
    {
      return name;
    }

    return parent.getPath() + "/" + name;
  }

  public void accept(Visitor visitor) throws Exception
  {
    visitor.visit(this);
  }

  @Override
  public String toString()
  {
    return name;
  }

  final void setPackageDoc(PackageDoc packageDoc)
  {
    this.packageDoc = packageDoc;
  }
}
