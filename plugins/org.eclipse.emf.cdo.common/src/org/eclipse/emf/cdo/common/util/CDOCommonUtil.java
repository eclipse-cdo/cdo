/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.spi.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionUnchunker;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various static methods that may help with I/O and time stamps.
 *
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOCommonUtil
{
  /**
   * @since 4.5
   */
  public static final String UNSPECIFIED_DATE_STRING = "*";

  /**
   * @since 4.0
   */
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.'SSS");

  /**
   * @since 4.4
   */
  public static final Pattern DATE_PATTERN = Pattern.compile("(\\d+)[ -/](\\d+)[ -/](\\d+)( +(\\d+):?(\\d*):?(\\d*)\\.?(\\d*))?");

  /**
   * @since 4.2
   */
  public static final String SYSTEM_USER_ID = "CDO_SYSTEM"; //$NON-NLS-1$

  /**
   * @since 4.4
   */
  public static final Comparator<CDONameProvider> NAME_COMPARATOR = new Comparator<CDONameProvider>()
  {
    @Override
    public int compare(CDONameProvider o1, CDONameProvider o2)
    {
      String n1 = StringUtil.safe(o1.getName()).toLowerCase();
      String n2 = StringUtil.safe(o2.getName()).toLowerCase();
      return n1.compareTo(n2);
    }
  };

  /**
   * @since 4.4
   */
  public static final Comparator<CDOTimeProvider> TIME_COMPARATOR = new Comparator<CDOTimeProvider>()
  {
    @Override
    public int compare(CDOTimeProvider o1, CDOTimeProvider o2)
    {
      return compareTimeStamps(o1.getTimeStamp(), o2.getTimeStamp());
    }
  };

  private CDOCommonUtil()
  {
  }

  /**
   * @since 4.0
   */
  public static CDODataInput createCDODataInput(ExtendedDataInputStream inputStream, final CDOPackageRegistry packageRegistry,
      final CDOBranchManager branchManager, final CDOCommitInfoManager commitManager, final CDORevisionFactory revisionFactory,
      final CDOListFactory listFactory, final CDOLobStore lobStore) throws IOException
  {
    return new CDODataInputImpl(inputStream)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return branchManager;
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return commitManager;
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return revisionFactory;
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return listFactory;
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return lobStore;
      }
    };
  }

  /**
   * @since 4.0
   * @deprecated As of 4.3 use {@link #createCDODataOutput(ExtendedDataOutput, CDOPackageRegistry, CDOIDProvider, CDORevisionUnchunker)}.
   */
  @Deprecated
  public static CDODataOutput createCDODataOutput(ExtendedDataOutput extendedDataOutputStream, final CDOPackageRegistry packageRegistry,
      final CDOIDProvider idProvider)
  {
    return createCDODataOutput(extendedDataOutputStream, packageRegistry, idProvider, null);
  }

  /**
   * @since 4.3
   */
  public static CDODataOutput createCDODataOutput(ExtendedDataOutput extendedDataOutputStream, final CDOPackageRegistry packageRegistry,
      final CDOIDProvider idProvider, final CDORevisionUnchunker unchunker)
  {
    return new CDODataOutputImpl(extendedDataOutputStream)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return idProvider;
      }

      @Override
      public CDORevisionUnchunker getRevisionUnchunker()
      {
        return unchunker;
      }
    };
  }

  /**
   * A well formed URI string must have a non-zero length, and must encode any special characters such as the space character.
   * As such, creating a {@link URI#createURI(String, boolean) URI}, ignoring the properly encoded characters, and converting
   * that to a {@link URI#toString() string}, must yield this URI string itself.
   *
   * @param uri the URI string in question.
   * @return whether the URI is well formed.
   * @since 4.21
   */
  public static boolean isWellFormedURI(String uri)
  {
    try
    {
      return !StringUtil.isEmpty(uri) && uri.equals(URI.createURI(uri, true).toString());
    }
    catch (Throwable exception)
    {
      return false;
    }
  }

  public static boolean isValidTimeStamp(long timeStamp, long startTime, long endTime)
  {
    if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return endTime == CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return (endTime == CDOBranchPoint.UNSPECIFIED_DATE || endTime >= timeStamp) && timeStamp >= startTime;
  }

  public static int compareTimeStamps(long t1, long t2)
  {
    if (t1 == CDORevision.UNSPECIFIED_DATE)
    {
      t1 = Long.MAX_VALUE;
    }

    if (t2 == CDORevision.UNSPECIFIED_DATE)
    {
      t2 = Long.MAX_VALUE;
    }

    if (t1 < t2)
    {
      return -1;
    }

    if (t1 > t2)
    {
      return 1;
    }

    return 0;
  }

  public static String formatTimeStamp()
  {
    return formatTimeStamp(System.currentTimeMillis());
  }

  public static String formatTimeStamp(long timeStamp)
  {
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return UNSPECIFIED_DATE_STRING;
    }

    if (timeStamp == CDORevision.INVALID_DATE)
    {
      return "INVALID";
    }

    synchronized (DATE_FORMAT)
    {
      return DATE_FORMAT.format(new Date(timeStamp));
    }
  }

  /**
   * @since 4.0
   */
  public static long parseTimeStamp(String timeStamp) throws ParseException
  {
    String trimmed = timeStamp.trim();

    if (UNSPECIFIED_DATE_STRING.equals(trimmed))
    {
      return CDORevision.UNSPECIFIED_DATE;
    }

    Matcher matcher = DATE_PATTERN.matcher(trimmed);
    if (!matcher.matches())
    {
      throw new ParseException("Not a valid date: " + trimmed + " --> pattern = " + DATE_PATTERN, 0);
    }

    timeStamp = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3) + " " + safe(matcher.group(5)) + ":" + safe(matcher.group(6)) + ":"
        + safe(matcher.group(7)) + "." + safe(matcher.group(8));

    Date date = DATE_FORMAT.parse(timeStamp);
    return date.getTime();
  }

  private static String safe(String value)
  {
    return StringUtil.isEmpty(value) ? "0" : value;
  }

  /**
   * @since 4.2
   */
  public static boolean waitWhileInitial(CDOCommonRepository repository, INotifier notifier, IProgressMonitor monitor)
  {
    double rest = 10000000;
    if (monitor != null)
    {
      monitor.beginTask("Waiting for repository " + repository.getName() + " to become initialized...", (int)rest);
    }

    IListener listener = null;

    try
    {
      while (repository.getState() == State.INITIAL)
      {
        if (monitor.isCanceled())
        {
          return false;
        }

        if (listener == null)
        {
          listener = new IListener()
          {
            @Override
            public void notifyEvent(IEvent event)
            {
              synchronized (this)
              {
                notifyAll();
              }
            }
          };

          notifier.addListener(listener);
        }

        synchronized (listener)
        {
          listener.wait(10L);
        }

        double work = rest / 1000;
        if (work >= 0)
        {
          monitor.worked((int)work);
          rest -= work;
        }
      }

      return true;
    }
    catch (InterruptedException ex)
    {
      return false;
    }
    finally
    {
      if (listener != null)
      {
        notifier.removeListener(listener);
      }

      if (monitor != null)
      {
        monitor.done();
      }
    }
  }
}
