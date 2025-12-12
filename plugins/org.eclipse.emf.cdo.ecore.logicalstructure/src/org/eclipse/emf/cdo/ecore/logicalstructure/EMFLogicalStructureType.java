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
package org.eclipse.emf.cdo.ecore.logicalstructure;

import org.eclipse.emf.cdo.ecore.logicalstructure.util.ContainerValue;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.DebugUtil;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.DelegatingObject;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.DelegatingValue;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.Scope;
import org.eclipse.emf.cdo.ecore.logicalstructure.util.Variable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILogicalStructureType;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class EMFLogicalStructureType implements ILogicalStructureType
{
  public static final EMFLogicalStructureType[] ARRAY = { new EMFLogicalStructureType() };

  private static final String INFOS_EXPR = "    org.eclipse.emf.common.util.EList features = eClass().getEAllStructuralFeatures();" //
      + "Object[][] infos = new Object[features.size()][];" //
      + "for (int i = 0; i < features.size(); i++)" //
      + "{" //
      + "  org.eclipse.emf.ecore.EStructuralFeature feature = (org.eclipse.emf.ecore.EStructuralFeature)features.get(i);" //
      + "  Class instanceClass = feature.getEType().getInstanceClass();" //
      + "  boolean primitive = instanceClass != null && instanceClass.isPrimitive();" //
      + "  infos[i] = new Object[] { feature.getName(), primitive ? \"1\" : \"0\" };" //
      + "}" //
      + "return infos;";

  private static final String VALUES_EXPR = "    org.eclipse.emf.common.util.EList features = eClass().getEAllStructuralFeatures();" //
      + "Object[] values = new Object[features.size()];" //
      + "for (int i = 0; i < features.size(); i++)" //
      + "{" //
      + "  org.eclipse.emf.ecore.EStructuralFeature feature = (org.eclipse.emf.ecore.EStructuralFeature)features.get(i);" //
      + "  Object value = eGet(feature, false);" //
      + "  if (value instanceof org.eclipse.emf.ecore.util.InternalEList)" //
      + "  {" //
      + "    value = ((org.eclipse.emf.ecore.util.InternalEList)value).basicToArray();" //
      + "  }" //
      + "  else if (value instanceof java.util.List)" //
      + "  {" //
      + "    value = ((java.util.List)value).toArray();" //
      + "  }" //
      + "  values[i] = value;" //
      + "}" //
      + "return values;";

  private final Map<IDebugTarget, Map<String, IJavaValue[]>> cache = new WeakHashMap<>();

  private String infosExpr = "return org.eclipse.emf.ecore.util.EcoreUtil.getAllStructuralFeatureInfos(eClass());";

  private String valuesExpr = "return org.eclipse.emf.ecore.util.EcoreUtil.getAllValues(this);";

  private EMFLogicalStructureType()
  {
  }

  @Override
  public String getId()
  {
    return EMFLogicalStructureType.class.getName();
  }

  @Override
  public String getDescription()
  {
    return "EObjects";
  }

  @Override
  public String getDescription(IValue value)
  {
    return "EObject";
  }

  @Override
  public boolean providesLogicalStructure(IValue value)
  {
    return value instanceof IJavaObject;
  }

  @Override
  public IValue getLogicalStructure(IValue value) throws CoreException
  {
    IJavaObject javaValue = (IJavaObject)value;
    IVariable[] variables = getLogicalVariables(javaValue);
    return new DelegatingObject(javaValue, variables);
  }

  private IVariable[] getLogicalVariables(IJavaObject eObject) throws CoreException
  {
    List<IVariable> result = new ArrayList<>();

    Scope scope = Scope.create(eObject);
    if (scope != null)
    {
      ContainerValue container = new EMFContainerValue(scope);
      result.add(new Variable.Private("[emf]", container));

      addContributions(eObject, scope, result);

      IJavaValue[] infos = getInfos(scope);
      IJavaValue[] values = getValues(scope, infos);

      for (int i = 0; i < values.length; i++)
      {
        IJavaValue[] info = ((IJavaArray)infos[i]).getValues();
        IJavaValue value = values[i];

        String featureName = info[0].getValueString();
        boolean primitive = info[1].getValueString().charAt(0) == '1';
        if (primitive)
        {
          value = unbox(value);
        }

        result.add(new Variable(featureName, value));
      }
    }

    return result.toArray(new IVariable[result.size()]);
  }

  private void addContributions(IJavaObject eObject, Scope scope, List<IVariable> result) throws DebugException
  {
    IConfigurationElement[] containerValues = Activator.getContainerValues();
    for (IConfigurationElement containerValue : containerValues)
    {
      try
      {
        String typeConstraint = containerValue.getAttribute("typeConstraint");
        if (DebugUtil.isInstanceOf(eObject, typeConstraint))
        {
          String name = containerValue.getAttribute("name");
          ContainerValue container = new ContributionContainer(scope, containerValue);

          result.add(new Variable.Private("[" + name + "]", container));
        }
      }
      catch (Exception ex)
      {
        Activator.error(ex);
      }
    }
  }

  private IJavaValue[] getInfos(Scope scope) throws DebugException
  {
    IDebugTarget debugTarget = scope.getDebugTarget();
    Map<String, IJavaValue[]> map = cache.get(debugTarget);
    if (map == null)
    {
      map = new HashMap<>();
      cache.put(debugTarget, map);
    }

    IJavaObject value = scope.getValue();
    IJavaType javaType = value.getJavaType();
    String type = javaType.getName();

    IJavaValue[] infos = map.get(type);
    if (infos == null)
    {
      try
      {
        IJavaArray result = (IJavaArray)scope.evaluate(infosExpr);
        infos = result.getValues();
      }
      catch (DebugException ex)
      {
        String message = ex.getMessage();
        if (message != null && message.contains("The method getAllStructuralFeatureInfos(EClass) is undefined for the type EcoreUtil"))
        {
          infosExpr = INFOS_EXPR;
          valuesExpr = VALUES_EXPR;

          IJavaArray result = (IJavaArray)scope.evaluate(infosExpr);
          infos = result.getValues();
        }
        else
        {
          Activator.error(ex);
        }
      }

      map.put(type, infos);
    }

    return infos;
  }

  private IJavaValue[] getValues(Scope scope, IJavaValue[] infos) throws DebugException
  {
    try
    {
      IJavaArray result = (IJavaArray)scope.evaluate(valuesExpr);
      if (result != null)
      {
        return result.getValues();
      }
    }
    catch (Exception ex)
    {
      return getValuesOneByOne(scope, infos);
    }

    return new IJavaValue[0];
  }

  private IJavaValue[] getValuesOneByOne(Scope scope, IJavaValue[] infos) throws DebugException
  {
    IJavaValue[] result = new IJavaValue[infos.length];
    for (int i = 0; i < infos.length; i++)
    {
      try
      {
        String expr = "return eGet(((org.eclipse.emf.ecore.impl.EClassImpl)eClass()).getEAllStructuralFeaturesData()[" + i + "]);";
        result[i] = scope.evaluate(expr);
      }
      catch (Exception ex)
      {
        Activator.error(ex);

        String featureName = ((IJavaArray)infos[i]).getValue(0).getValueString();
        result[i] = new ErrorValue(scope.getValue(), featureName, ex);
      }
    }

    return result;
  }

  private IJavaValue unbox(IJavaValue value) throws DebugException
  {
    IVariable[] variables = value.getVariables();
    for (int i = 0; i < variables.length; i++)
    {
      IVariable variable = variables[i];
      if ("value".equals(variable.getName()))
      {
        return (IJavaValue)variable.getValue();
      }
    }

    return value;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ContributionContainer extends ContainerValue
  {
    private final IConfigurationElement containerValue;

    private ContributionContainer(Scope scope, IConfigurationElement containerValue)
    {
      super(scope);
      this.containerValue = containerValue;
    }

    @Override
    protected void initVariables(Variables variables) throws DebugException
    {
      for (IConfigurationElement variable : containerValue.getChildren())
      {
        String name = variable.getAttribute("name");
        String expression = variable.getAttribute("expression");
        variables.add(name, expression);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ErrorValue extends DelegatingValue
  {
    private final String featureName;

    private final Exception exception;

    private ErrorValue(IJavaValue delegate, String featureName, Exception exception)
    {
      super(delegate);
      this.featureName = featureName;
      this.exception = exception;
    }

    @Override
    public String getReferenceTypeName() throws DebugException
    {
      return exception.getClass().getName();
    }

    @Override
    public String getValueString() throws DebugException
    {
      return "Error when evaluating value of feature " + featureName;
    }

    @Override
    public boolean hasVariables() throws DebugException
    {
      return false;
    }
  }
}
