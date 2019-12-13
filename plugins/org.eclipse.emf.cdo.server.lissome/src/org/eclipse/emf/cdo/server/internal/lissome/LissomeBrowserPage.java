/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.lissome.ILissomeStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.PrintStream;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class LissomeBrowserPage extends AbstractPage
{
  public LissomeBrowserPage()
  {
    super("ltables", "LissomeDB Tables");
  }

  @Override
  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof ILissomeStore;
  }

  @Override
  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    out.print("<table border=\"0\">\r\n");
    out.print("<tr>\r\n");

    out.print("<td valign=\"top\">\r\n");
    // String table = showTables(browser, out, connection, repository.getName());
    out.print("</td>\r\n");

    // if (table != null)
    {
      out.print("<td valign=\"top\">\r\n");
      // showTable(browser, out, connection, table);
      out.print("</td>\r\n");
    }

    out.print("</tr>\r\n");
    out.print("</table>\r\n");
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "lissome";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    @Override
    public LissomeBrowserPage create(String description) throws ProductCreationException
    {
      return new LissomeBrowserPage();
    }
  }
}
