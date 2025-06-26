/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOFingerPrinter;
import org.eclipse.emf.cdo.common.util.CDOFingerPrinter.FingerPrint;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.util.DigestFingerPrinter;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class FingerPrintQueryHandler implements IQueryHandler
{
  private static final String DEFAULT_TYPE = DigestFingerPrinter.Factory.TYPE;

  private static final String DEFAULT_PARAM = "SHA-512,base64";

  public FingerPrintQueryHandler()
  {
  }

  @Override
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    IView view = context.getView();

    String type = info.getParameter(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT_TYPE);
    if (StringUtil.isEmpty(type))
    {
      type = DEFAULT_TYPE;
    }

    String param = info.getParameter(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT_PARAM);
    if (StringUtil.isEmpty(param))
    {
      param = DEFAULT_PARAM;
    }

    CDOID rootID = (CDOID)info.getContext();
    if (CDOIDUtil.isNull(rootID))
    {
      rootID = view.getSession().getRepository().getRootResourceID();
    }

    IManagedContainer container = ContainerUtil.getContainer(view.getRepository());
    CDOFingerPrinter fingerPrinter = (CDOFingerPrinter)container.getElement(CDOFingerPrinter.Factory.PRODUCT_GROUP, type, param);
    FingerPrint fingerPrint = fingerPrinter.createFingerPrint(view, rootID);

    context.addResult(fingerPrint.getValue());
    context.addResult(fingerPrint.getCount());
    context.addResult(param);
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_FINGER_PRINT);
    }

    @Override
    public FingerPrintQueryHandler create(String description) throws ProductCreationException
    {
      return new FingerPrintQueryHandler();
    }
  }
}
