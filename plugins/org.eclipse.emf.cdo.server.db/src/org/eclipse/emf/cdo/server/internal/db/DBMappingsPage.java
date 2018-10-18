/*
 * Copyright (c) 2010-2013, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IClassMapping;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy3;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class DBMappingsPage extends AbstractPage
{
  public DBMappingsPage()
  {
    super("mappings", "DB Mappings");
  }

  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IDBStore;
  }

  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    boolean mapAll = "all".equals(browser.getParam("map"));

    IDBStore store = (IDBStore)repository.getStore();
    IMappingStrategy mappingStrategy = store.getMappingStrategy();
    Map<EClass, IClassMapping> classMappings = mappingStrategy.getClassMappings(mapAll);

    if (mappingStrategy instanceof IMappingStrategy3)
    {
      CDOPackageRegistry packageRegistry = store.getRepository().getPackageRegistry();
      CDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits();

      IMappingStrategy3 mappingStrategy3 = (IMappingStrategy3)mappingStrategy;
      List<EClass> mappedClasses = mappingStrategy3.getMappedClasses(packageUnits);

      int countMapped = classMappings.size();
      int countMappable = mappedClasses.size();

      out.print("Mappable classes: " + countMappable + "<br>\r\n");
      out.print("Mapped classes: " + countMapped + "<br>\r\n");
      out.print("Unmapped classes: " + (countMappable - countMapped));

      if (countMapped < countMappable)
      {
        out.print("&nbsp;&nbsp;&nbsp;" + browser.href("(map all)", getName(), "map", "all"));
      }

      out.print("<br>\r\n");
      out.print("<br>\r\n");
    }

    List<String> qualifiedNames = new ArrayList<String>();
    Map<String, IClassMapping> byQualifiedName = new LinkedHashMap<String, IClassMapping>();
    for (Map.Entry<EClass, IClassMapping> entry : classMappings.entrySet())
    {
      EClass eClass = entry.getKey();
      IClassMapping classMapping = entry.getValue();

      String qualifiedName = EMFUtil.getQualifiedName(eClass, ".");
      qualifiedNames.add(qualifiedName);
      byQualifiedName.put(qualifiedName, classMapping);
    }

    Collections.sort(qualifiedNames);

    out.print("<table border=1>\r\n");
    out.print("<tr><th>Class</th><th>Table</th></tr/>\r\n");

    for (String qualifiedName : qualifiedNames)
    {
      IClassMapping classMapping = byQualifiedName.get(qualifiedName);
      List<IDBTable> tables = classMapping.getDBTables();

      boolean mapped = tables.get(0) != null;
      String fontPrefix = mapped ? "<b>" : "<font color=\"gray\">";
      String fontSuffix = mapped ? "</b>" : "</font>";

      out.print("<tr><td valign=\"top\">");
      out.print(fontPrefix);
      out.print(qualifiedName);
      out.print(fontSuffix);
      out.print("</td><td>");

      boolean first = true;
      for (IDBTable table : tables)
      {
        if (table != null)
        {
          if (first)
          {
            first = false;
          }
          else
          {
            out.print("<br/>");
          }

          out.print(DBTablesPage.tableHRef(browser, table));
        }
      }

      out.print("</td></tr/>\r\n");
    }

    out.print("</table>\r\n");
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "dbmappings";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public DBMappingsPage create(String description) throws ProductCreationException
    {
      return new DBMappingsPage();
    }
  }
}
