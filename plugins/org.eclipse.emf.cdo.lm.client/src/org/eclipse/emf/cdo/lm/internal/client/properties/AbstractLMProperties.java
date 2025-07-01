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
package org.eclipse.emf.cdo.lm.internal.client.properties;

import org.eclipse.emf.cdo.common.util.CDOFingerPrinter.FingerPrint;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.internal.client.messages.Messages;
import org.eclipse.emf.cdo.lm.util.LMFingerPrintAnnotation;

import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLMProperties<RECEIVER> extends Properties<RECEIVER>
{
  public static final String CATEGORY = "Lifecycle Management"; //$NON-NLS-1$

  protected AbstractLMProperties(Class<RECEIVER> receiverType)
  {
    super(receiverType);
    initProperties();
  }

  protected abstract IAssemblyDescriptor getAssemblyDescriptor(RECEIVER receiver);

  protected void initProperties()
  {
    add(new Property<RECEIVER>("moduleName", Messages.getString("AbstractLMProperties_00"), //
        Messages.getString("AbstractLMProperties_01"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getModule(receiver).getName();
      }
    });

    add(new Property<RECEIVER>("moduleType", Messages.getString("AbstractLMProperties_12"), //
        Messages.getString("AbstractLMProperties_13"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getModule(receiver).getType().getName();
      }
    });

    add(new Property<RECEIVER>("systemName", Messages.getString("AbstractLMProperties_02"), //
        Messages.getString("AbstractLMProperties_03"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).getSystem().getName();
      }
    });

    add(new Property<RECEIVER>("baselineName", Messages.getString("AbstractLMProperties_04"), //
        Messages.getString("AbstractLMProperties_05"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).getName();
      }
    });

    add(new Property<RECEIVER>("baselineTypeName", Messages.getString("AbstractLMProperties_10"), //
        Messages.getString("AbstractLMProperties_11"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).getTypeName();
      }
    });

    add(new Property<RECEIVER>("baselineTypeAndName")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).getTypeAndName();
      }
    });

    add(new Property<RECEIVER>("baselineType")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).eClass().getName();
      }
    });

    add(new Property<RECEIVER>("baselineFloating")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getBaseline(receiver).isFloating();
      }
    });

    add(new Property<RECEIVER>("baselineHasFingerPrint")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        FingerPrint fingerPrint = getFingerPrint(receiver);
        return fingerPrint != null;
      }
    });

    add(new Property<RECEIVER>("baselineFingerPrint")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        FingerPrint fingerPrint = getFingerPrint(receiver);
        if (fingerPrint != null)
        {
          return fingerPrint.getValue();
        }

        return null;
      }
    });

    add(new Property<RECEIVER>("streamName", Messages.getString("AbstractLMProperties_06"), //
        Messages.getString("AbstractLMProperties_07"), //$NON-NLS-1$
        CATEGORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getStream(receiver).getName();
      }
    });

    add(new Property<RECEIVER>("streamCodeName")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getStream(receiver).getCodeName();
      }
    });

    add(new Property<RECEIVER>("streamModeName")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getStream(receiver).getMode();
      }
    });

    add(new Property<RECEIVER>("streamVersion")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        Stream stream = getStream(receiver);
        return stream.getMajorVersion() + '.' + stream.getMinorVersion();
      }
    });

    add(new Property<RECEIVER>("updatesAvailable")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getAssemblyDescriptor(receiver).hasUpdatesAvailable();
      }
    });
  }

  private Module getModule(RECEIVER receiver)
  {
    return getBaseline(receiver).getModule();
  }

  private Stream getStream(RECEIVER receiver)
  {
    return getBaseline(receiver).getStream();
  }

  private Baseline getBaseline(RECEIVER receiver)
  {
    return getAssemblyDescriptor(receiver).getBaseline();
  }

  private FingerPrint getFingerPrint(RECEIVER receiver)
  {
    Baseline baseline = getBaseline(receiver);
    if (baseline instanceof FixedBaseline)
    {
      FixedBaseline fixedBaseline = (FixedBaseline)baseline;
      return LMFingerPrintAnnotation.getFingerPrint(fixedBaseline);
    }

    return null;
  }

  public static void main(String[] args)
  {
    new LMCheckoutProperties.Tester().dumpContributionMarkup();
    new LMModuleCheckoutProperties.Tester().dumpContributionMarkup();
  }
}
