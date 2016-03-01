/*
 * Copyright (c) 2011-2013, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418452
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.properties.DefaultPropertyTester;
import org.eclipse.net4j.util.properties.IProperties;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class SessionProperties extends Properties<CDOSession> implements CDOCommonRepository
{
  public static final IProperties<CDOSession> INSTANCE = new SessionProperties();

  private static final String CATEGORY_SESSION = "Session"; //$NON-NLS-1$

  private static final String CATEGORY_REPOSITORY = "Repository"; //$NON-NLS-1$

  private SessionProperties()
  {
    super(CDOSession.class);

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

    add(new Property<CDOSession>("repositoryName", Messages.getString("SessionPropertyTester_10"), //
        Messages.getString("SessionPropertyTester_11"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getName();
      }
    });

    add(new Property<CDOSession>("repositoryUUID", Messages.getString("SessionPropertyTester_12"), //
        Messages.getString("SessionPropertyTester_13"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getUUID();
      }
    });

    add(new Property<CDOSession>("repositoryType", Messages.getString("SessionPropertyTester_14"), //
        Messages.getString("SessionPropertyTester_15"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getType();
      }
    });

    add(new Property<CDOSession>("repositoryState", Messages.getString("SessionPropertyTester_16"), //
        Messages.getString("SessionPropertyTester_17"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getState();
      }
    });

    add(new Property<CDOSession>("repositoryCreationTime", Messages.getString("SessionPropertyTester_18"), //
        Messages.getString("SessionPropertyTester_19"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return CDOCommonUtil.formatTimeStamp(session.getRepositoryInfo().getCreationTime());
      }
    });

    add(new Property<CDOSession>("authenticating", Messages.getString("SessionPropertyTester_40"), //
        Messages.getString("SessionPropertyTester_41"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isAuthenticating();
      }
    });

    add(new Property<CDOSession>("supportingAudits", Messages.getString("SessionPropertyTester_20"), //
        Messages.getString("SessionPropertyTester_21"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isSupportingAudits();
      }
    });

    add(new Property<CDOSession>("supportingBranches", Messages.getString("SessionPropertyTester_22"), //
        Messages.getString("SessionPropertyTester_23"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isSupportingBranches();
      }
    });

    add(new Property<CDOSession>("supportingUnits", Messages.getString("SessionPropertyTester_22"), //
        Messages.getString("SessionPropertyTester_23"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isSupportingBranches();
      }
    });

    add(new Property<CDOSession>("serializeCommits", Messages.getString("SessionPropertyTester_38"), //
        Messages.getString("SessionPropertyTester_39"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isSerializingCommits();
      }
    });

    add(new Property<CDOSession>("ensureReferentialIntegrity", Messages.getString("SessionPropertyTester_30"), //
        Messages.getString("SessionPropertyTester_31"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().isEnsuringReferentialIntegrity();
      }
    });

    add(new Property<CDOSession>("idGenerationLocation", Messages.getString("SessionPropertyTester_32"), //
        Messages.getString("SessionPropertyTester_33"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getIDGenerationLocation();
      }
    });

    add(new Property<CDOSession>("storeType", Messages.getString("SessionPropertyTester_24"), //
        Messages.getString("SessionPropertyTester_25"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return session.getRepositoryInfo().getStoreType();
      }
    });

    add(new Property<CDOSession>("objectIDTypes", Messages.getString("SessionPropertyTester_26"), //
        Messages.getString("SessionPropertyTester_27"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(CDOSession session)
      {
        StringBuilder builder = new StringBuilder();
        for (CDOID.ObjectType objectIDType : session.getRepositoryInfo().getObjectIDTypes())
        {
          if (builder.length() != 0)
          {
            builder.append(", "); //$NON-NLS-1$
          }

          builder.append(objectIDType);
        }

        return builder;
      }
    });

    add(new Property<CDOSession>("userAuthenticated")
    {
      @Override
      protected Object eval(CDOSession session)
      {
        return !StringUtil.isEmpty(session.getUserID());
      }
    });
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public long getTimeStamp()
  {
    throw new UnsupportedOperationException();
  }

  public String getName()
  {
    throw new UnsupportedOperationException();
  }

  public String getUUID()
  {
    throw new UnsupportedOperationException();
  }

  public Type getType()
  {
    throw new UnsupportedOperationException();
  }

  public State getState()
  {
    throw new UnsupportedOperationException();
  }

  public long getCreationTime()
  {
    throw new UnsupportedOperationException();
  }

  public String getStoreType()
  {
    throw new UnsupportedOperationException();
  }

  public Set<ObjectType> getObjectIDTypes()
  {
    throw new UnsupportedOperationException();
  }

  public IDGenerationLocation getIDGenerationLocation()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID getRootResourceID()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isAuthenticating()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isSupportingAudits()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isSupportingBranches()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isSupportingUnits()
  {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  public boolean isSupportingEcore()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isSerializingCommits()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEnsuringReferentialIntegrity()
  {
    throw new UnsupportedOperationException();
  }

  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    throw new UnsupportedOperationException();
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
