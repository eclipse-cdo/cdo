/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.logicalstructure.util;

import org.eclipse.emf.cdo.ecore.logicalstructure.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.eval.IAstEvaluationEngine;
import org.eclipse.jdt.debug.eval.ICompiledExpression;
import org.eclipse.jdt.debug.eval.IEvaluationListener;
import org.eclipse.jdt.debug.eval.IEvaluationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public class Scope
{
  private static final int INFO_EVALUATION_STACK_FRAME = 111;

  private static final IStatus NEED_STACK_FRAME = new Status(IStatus.INFO, Activator.PLUGIN_ID,
      INFO_EVALUATION_STACK_FRAME, "Provides thread context for an evaluation", null);

  private static final Map<IDebugTarget, Map<String, ICompiledExpression>> CACHE = new WeakHashMap<IDebugTarget, Map<String, ICompiledExpression>>();

  private static IStatusHandler stackFrameProvider;

  private final IJavaObject value;

  private final IJavaReferenceType type;

  private final IJavaThread thread;

  private final IAstEvaluationEngine evaluationEngine;

  public Scope(IJavaObject value, IJavaReferenceType type, IJavaThread thread, IAstEvaluationEngine evaluationEngine)
  {
    this.value = value;
    this.type = type;
    this.thread = thread;
    this.evaluationEngine = evaluationEngine;
  }

  public String getModelIdentifier()
  {
    return value.getModelIdentifier();
  }

  public IDebugTarget getDebugTarget()
  {
    return value.getDebugTarget();
  }

  public ILaunch getLaunch()
  {
    return value.getLaunch();
  }

  public IJavaObject getValue()
  {
    return value;
  }

  public IJavaReferenceType getType()
  {
    return type;
  }

  public IJavaValue evaluate(String expression) throws DebugException
  {
    ICompiledExpression compiledExpression = getCompiledExpression(expression);

    ResultListener listener = new ResultListener();
    evaluationEngine.evaluateExpression(compiledExpression, value, thread, listener, DebugEvent.EVALUATION_IMPLICIT,
        false);

    IEvaluationResult result = listener.getResult();
    if (result == null)
    {
      return null;
    }

    if (result.hasErrors())
    {
      StringBuilder message = new StringBuilder("Error when evaluating expression ");
      message.append(expression);
      message.append(": ");

      String[] errorMessages = result.getErrorMessages();
      for (int i = 0; i < errorMessages.length; i++)
      {
        message.append(errorMessages[i]);
        message.append("\n");
      }

      throw new DebugException(
          new Status(IStatus.ERROR, Activator.PLUGIN_ID, message.toString(), result.getException()));
    }

    return result.getValue();
  }

  private ICompiledExpression getCompiledExpression(String expression) throws DebugException
  {
    IDebugTarget debugTarget = value.getDebugTarget();
    Map<String, ICompiledExpression> compiledExpressions = CACHE.get(debugTarget);
    if (compiledExpressions == null)
    {
      compiledExpressions = new HashMap<String, ICompiledExpression>();
      CACHE.put(debugTarget, compiledExpressions);
    }

    ICompiledExpression compiledExpression = compiledExpressions.get(expression);
    if (compiledExpression == null)
    {
      compiledExpression = compile(expression);
      compiledExpressions.put(expression, compiledExpression);
    }

    return compiledExpression;
  }

  private ICompiledExpression compile(String expression) throws DebugException
  {
    ICompiledExpression compiledExpression = evaluationEngine.getCompiledExpression(expression, type);
    if (compiledExpression.hasErrors())
    {
      StringBuilder message = new StringBuilder("Error when compiling expression ");
      message.append(expression);
      message.append(": ");

      String[] errorMessages = compiledExpression.getErrorMessages();
      for (int i = 0; i < errorMessages.length; i++)
      {
        message.append(errorMessages[i]);
        message.append("\n");
      }

      throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message.toString()));
    }

    return compiledExpression;
  }

  public static Scope create(IJavaObject value) throws CoreException
  {
    Scope scope = null;

    IJavaType type = value.getJavaType();
    if (type instanceof IJavaClassType)
    {
      IJavaStackFrame stackFrame = getStackFrame(value);
      if (stackFrame != null)
      {
        // find the project the snippets will be compiled in.
        ISourceLocator locator = value.getLaunch().getSourceLocator();
        Object sourceElement = null;
        if (locator instanceof ISourceLookupDirector)
        {
          String[] sourcePaths = ((IJavaClassType)type).getSourcePaths(null);
          if (sourcePaths != null && sourcePaths.length > 0)
          {
            sourceElement = ((ISourceLookupDirector)locator).getSourceElement(sourcePaths[0]);
          }

          if (!(sourceElement instanceof IJavaElement) && sourceElement instanceof IAdaptable)
          {
            sourceElement = ((IAdaptable)sourceElement).getAdapter(IJavaElement.class);
          }
        }

        if (sourceElement == null)
        {
          sourceElement = locator.getSourceElement(stackFrame);
          if (!(sourceElement instanceof IJavaElement) && sourceElement instanceof IAdaptable)
          {
            Object newSourceElement = ((IAdaptable)sourceElement).getAdapter(IJavaElement.class);
            // if the source is a drl during the execution of the rule
            if (newSourceElement != null)
            {
              sourceElement = newSourceElement;
            }
          }
        }

        IJavaProject project = null;
        if (sourceElement instanceof IJavaElement)
        {
          project = ((IJavaElement)sourceElement).getJavaProject();
        }
        else if (sourceElement instanceof IResource)
        {
          IJavaProject resourceProject = JavaCore.create(((IResource)sourceElement).getProject());
          if (resourceProject.exists())
          {
            project = resourceProject;
          }
        }

        if (project != null)
        {
          IJavaDebugTarget debugTarget = (IJavaDebugTarget)stackFrame.getDebugTarget();
          IAstEvaluationEngine evaluationEngine = getEvaluationEngine(project, debugTarget);

          scope = new Scope(value, (IJavaReferenceType)type, (IJavaThread)stackFrame.getThread(), evaluationEngine);
        }
      }
    }

    return scope;
  }

  @SuppressWarnings("restriction")
  private static IAstEvaluationEngine getEvaluationEngine(IJavaProject project, IJavaDebugTarget debugTarget)
  {
    return org.eclipse.jdt.internal.debug.core.JDIDebugPlugin.getDefault().getEvaluationEngine(project, debugTarget);
  }

  /**
   * Return the current stack frame context, or a valid stack frame for the
   * given value.
   */
  private static IJavaStackFrame getStackFrame(IValue value) throws CoreException
  {
    IStatusHandler handler = getStackFrameProvider();
    if (handler != null)
    {
      IJavaStackFrame stackFrame = (IJavaStackFrame)handler.handleStatus(NEED_STACK_FRAME, value);
      if (stackFrame != null)
      {
        return stackFrame;
      }
    }

    IDebugTarget target = value.getDebugTarget();
    IJavaDebugTarget javaTarget = target.getAdapter(IJavaDebugTarget.class);
    if (javaTarget != null)
    {
      IThread[] threads = javaTarget.getThreads();
      for (int i = 0; i < threads.length; i++)
      {
        IThread thread = threads[i];
        if (thread.isSuspended())
        {
          return (IJavaStackFrame)thread.getTopStackFrame();
        }
      }
    }

    return null;
  }

  private static IStatusHandler getStackFrameProvider()
  {
    if (stackFrameProvider == null)
    {
      stackFrameProvider = DebugPlugin.getDefault().getStatusHandler(NEED_STACK_FRAME);
    }

    return stackFrameProvider;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ResultListener implements IEvaluationListener
  {
    private IEvaluationResult result;

    @Override
    public synchronized void evaluationComplete(IEvaluationResult result)
    {
      this.result = result;
      notifyAll();
    }

    public synchronized IEvaluationResult getResult()
    {
      while (result == null)
      {
        try
        {
          wait();
        }
        catch (InterruptedException ex)
        {
          break;
        }
      }

      return result;
    }
  }
}
