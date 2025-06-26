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
package org.eclipse.emf.cdo.internal.common.util;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.ContainmentProxyStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.FeatureStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.MessageDigestHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.util.CDOFingerPrinter;

import org.eclipse.net4j.util.StringConverter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainer.ContainerAware;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Eike Stepper
 */
public class DigestFingerPrinter implements CDOFingerPrinter
{
  public static final String DEFAULT_ALGORITHM = "SHA-512";

  public static final String DEFAULT_ENCODER = "base64";

  private static final String[] NO_PARAMETERS = {};

  private static final String LOCAL_IDS = "localIDs";

  private static final String SEPARATOR = ",";

  private final String param;

  private final String algorithm;

  private final StringConverter encoder;

  private final boolean localIDs;

  public DigestFingerPrinter(String param, IManagedContainer container)
  {
    String[] tokens = param == null ? NO_PARAMETERS : param.split(SEPARATOR);
    algorithm = tokens.length < 1 ? DEFAULT_ALGORITHM : tokens[0];
    createMessageDigest(); // Check existence early.

    String encoderName = tokens.length < 2 ? DEFAULT_ENCODER : tokens[1];
    StringConverter stringConverter = container.getElementOrNull(StringConverter.PRODUCT_GROUP, encoderName);
    if (stringConverter != null)
    {
      encoder = stringConverter;
    }
    else
    {
      encoder = StringConverter.BASE64;
      encoderName = DEFAULT_ENCODER;
    }

    localIDs = tokens.length < 3 ? false : LOCAL_IDS.equalsIgnoreCase(tokens[2]);

    this.param = algorithm + SEPARATOR + encoderName + (localIDs ? SEPARATOR + LOCAL_IDS : "");
  }

  @Override
  public final String getParam()
  {
    return param;
  }

  @Override
  public FingerPrint createFingerPrint(CDORevisionProvider revisionProvider, CDOID rootID)
  {
    CDORevision rootResource = revisionProvider.getRevision(rootID);

    MessageDigest digest = createMessageDigest();
    MessageDigestHandler handler = new MessageDigestHandler(digest, localIDs);

    CDORevisionCrawler crawler = new CDORevisionCrawler(handler);
    crawler.setFeatureStrategy(FeatureStrategy.TREE);
    crawler.setContainmentProxyStrategy(ContainmentProxyStrategy.Physical);
    crawler.setRevisionProvider(revisionProvider);
    crawler.addRevision(rootResource);

    if (localIDs)
    {
      handler.finishLocalIDs();
    }

    byte[] bytes = digest.digest();
    String value = encoder.apply(bytes);
    long count = crawler.getRevisionCount();
    return new FingerPrint(value, count, param);
  }

  private MessageDigest createMessageDigest()
  {
    try
    {
      return MessageDigest.getInstance(algorithm);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends CDOFingerPrinter.Factory implements ContainerAware
  {
    public static final String TYPE = "digest"; //$NON-NLS-1$

    private IManagedContainer container = IPluginContainer.INSTANCE;

    public Factory()
    {
      this(TYPE);
    }

    protected Factory(String type)
    {
      super(type);
    }

    @Override
    public void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }

    @Override
    public CDOFingerPrinter create(String param) throws ProductCreationException
    {
      return new DigestFingerPrinter(param, container);
    }
  }
}
