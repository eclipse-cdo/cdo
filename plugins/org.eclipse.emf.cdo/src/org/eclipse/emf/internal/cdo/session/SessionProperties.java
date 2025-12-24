/*
 * Copyright (c) 2011-2013, 2015, 2016, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418452
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.AbstractRepositoryProperties;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public class SessionProperties extends AbstractRepositoryProperties<CDOSession>
{
  public static final IProperties<CDOSession> INSTANCE = new SessionProperties();

  private static final String CATEGORY_SESSION = "Session"; //$NON-NLS-1$

  private SessionProperties()
  {
    super(CDOSession.class);

  }

  @Override
  protected CDOCommonRepository getRepository(CDOSession session)
  {
    return session.getRepositoryInfo();
  }

  @Override
  protected void initProperties()
  {
    add(new Property<CDOSession>("open", Messages.getString("SessionPropertyTester_34"), //
        Messages.getString("SessionPropertyTester_35"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return !session.isClosed();
      }
    });

    add(new Property<CDOSession>("sessionID", Messages.getString("SessionPropertyTester_0"), //
        Messages.getString("SessionPropertyTester_3"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getSessionID();
      }
    });

    add(new Property<CDOSession>("userID", Messages.getString("SessionPropertyTester_4"), //
        Messages.getString("SessionPropertyTester_5"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getUserID();
      }
    });

    add(new Property<CDOSession>("passiveUpdateEnabled", Messages.getString("SessionPropertyTester_6"), //
        Messages.getString("SessionPropertyTester_7"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.options().isPassiveUpdateEnabled();
      }
    });

    add(new Property<CDOSession>("lastUpdateTime", Messages.getString("SessionPropertyTester_36"), //
        Messages.getString("SessionPropertyTester_37"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return CDOCommonUtil.formatTimeStamp(session.getLastUpdateTime());
      }
    });

    add(new Property<CDOSession>("passiveUpdateMode", Messages.getString("SessionPropertyTester_8"), //
        Messages.getString("SessionPropertyTester_9"), //$NON-NLS-1$
        CATEGORY_SESSION)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.options().getPassiveUpdateMode();
      }
    });

    super.initProperties();

    add(new Property<CDOSession>("userAuthenticated")
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return !StringUtil.isEmpty(session.getUserID());
      }
    });
  }

  public static void main(String[] args)
  {
    new Tester().dumpContributionMarkup();
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tester extends DefaultPropertyTester<CDOSession>
  {
    public static final String NAMESPACE = "org.eclipse.emf.cdo.session";

    public Tester()
    {
      super(NAMESPACE, INSTANCE);
    }
  }
}
