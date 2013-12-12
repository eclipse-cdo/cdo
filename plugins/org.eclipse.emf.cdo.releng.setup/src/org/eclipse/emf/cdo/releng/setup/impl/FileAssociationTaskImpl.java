/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.FileAssociationTask;
import org.eclipse.emf.cdo.releng.setup.FileEditor;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.Trigger;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;
import org.eclipse.ui.internal.util.PrefUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>File Association Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl#getFilePattern <em>File Pattern</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl#getDefaultEditorID <em>Default Editor ID</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.FileAssociationTaskImpl#getEditors <em>Editors</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@SuppressWarnings("restriction")
public class FileAssociationTaskImpl extends SetupTaskImpl implements FileAssociationTask
{
  /**
   * The default value of the '{@link #getFilePattern() <em>File Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFilePattern()
   * @generated
   * @ordered
   */
  protected static final String FILE_PATTERN_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFilePattern() <em>File Pattern</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFilePattern()
   * @generated
   * @ordered
   */
  protected String filePattern = FILE_PATTERN_EDEFAULT;

  /**
   * The default value of the '{@link #getDefaultEditorID() <em>Default Editor ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultEditorID()
   * @generated
   * @ordered
   */
  protected static final String DEFAULT_EDITOR_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDefaultEditorID() <em>Default Editor ID</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultEditorID()
   * @generated
   * @ordered
   */
  protected String defaultEditorID = DEFAULT_EDITOR_ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getEditors() <em>Editors</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEditors()
   * @generated
   * @ordered
   */
  protected EList<FileEditor> editors;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FileAssociationTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.FILE_ASSOCIATION_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFilePattern()
  {
    return filePattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFilePattern(String newFilePattern)
  {
    String oldFilePattern = filePattern;
    filePattern = newFilePattern;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.FILE_ASSOCIATION_TASK__FILE_PATTERN,
          oldFilePattern, filePattern));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDefaultEditorID()
  {
    return defaultEditorID;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultEditorID(String newDefaultEditorID)
  {
    String oldDefaultEditorID = defaultEditorID;
    defaultEditorID = newDefaultEditorID;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID,
          oldDefaultEditorID, defaultEditorID));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<FileEditor> getEditors()
  {
    if (editors == null)
    {
      editors = new EObjectContainmentEList.Resolving<FileEditor>(FileEditor.class, this,
          SetupPackage.FILE_ASSOCIATION_TASK__EDITORS);
    }
    return editors;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.FILE_ASSOCIATION_TASK__EDITORS:
      return ((InternalEList<?>)getEditors()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.FILE_ASSOCIATION_TASK__FILE_PATTERN:
      return getFilePattern();
    case SetupPackage.FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID:
      return getDefaultEditorID();
    case SetupPackage.FILE_ASSOCIATION_TASK__EDITORS:
      return getEditors();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.FILE_ASSOCIATION_TASK__FILE_PATTERN:
      setFilePattern((String)newValue);
      return;
    case SetupPackage.FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID:
      setDefaultEditorID((String)newValue);
      return;
    case SetupPackage.FILE_ASSOCIATION_TASK__EDITORS:
      getEditors().clear();
      getEditors().addAll((Collection<? extends FileEditor>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.FILE_ASSOCIATION_TASK__FILE_PATTERN:
      setFilePattern(FILE_PATTERN_EDEFAULT);
      return;
    case SetupPackage.FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID:
      setDefaultEditorID(DEFAULT_EDITOR_ID_EDEFAULT);
      return;
    case SetupPackage.FILE_ASSOCIATION_TASK__EDITORS:
      getEditors().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.FILE_ASSOCIATION_TASK__FILE_PATTERN:
      return FILE_PATTERN_EDEFAULT == null ? filePattern != null : !FILE_PATTERN_EDEFAULT.equals(filePattern);
    case SetupPackage.FILE_ASSOCIATION_TASK__DEFAULT_EDITOR_ID:
      return DEFAULT_EDITOR_ID_EDEFAULT == null ? defaultEditorID != null : !DEFAULT_EDITOR_ID_EDEFAULT
          .equals(defaultEditorID);
    case SetupPackage.FILE_ASSOCIATION_TASK__EDITORS:
      return editors != null && !editors.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (filePattern: ");
    result.append(filePattern);
    result.append(", defaultEditorID: ");
    result.append(defaultEditorID);
    result.append(')');
    return result.toString();
  }

  @Override
  public Set<Trigger> getValidTriggers()
  {
    return Trigger.IDE_TRIGGERS;
  }

  public boolean isNeeded(SetupTaskContext context) throws Exception
  {
    Map<String, FileEditorMapping> mappings = getMappings();
    FileEditorMapping mapping = mappings.get(getFilePattern());
    if (mapping == null)
    {
      return true;
    }

    String defaultEditorID = getDefaultEditorID();
    if (!StringUtil.isEmpty(defaultEditorID))
    {
      EditorRegistry registry = (EditorRegistry)PlatformUI.getWorkbench().getEditorRegistry();
      IEditorDescriptor defaultEditor = registry.findEditor(defaultEditorID);
      if (defaultEditor != null)
      {
        IEditorDescriptor mappingDefaultEditor = mapping.getDefaultEditor();
        String mappingDefaultEditorID = mappingDefaultEditor == null ? null : mappingDefaultEditor.getId();
        if (!ObjectUtil.equals(mappingDefaultEditorID, defaultEditorID))
        {
          return true;
        }
      }
    }

    Set<String> editorIDs = getEditorIDs(mapping);
    for (FileEditor fileEditor : getEditors())
    {
      if (!editorIDs.contains(fileEditor.getID()))
      {
        return true;
      }
    }

    return false;
  }

  public void perform(SetupTaskContext context) throws Exception
  {
    String filePattern = getFilePattern();
    final EditorRegistry registry = (EditorRegistry)PlatformUI.getWorkbench().getEditorRegistry();

    final Map<String, FileEditorMapping> mappings = getMappings();
    FileEditorMapping mapping = mappings.get(filePattern);
    if (mapping == null)
    {
      int lastDot = filePattern.lastIndexOf('.');
      String name = lastDot == -1 ? filePattern : filePattern.substring(0, lastDot);
      String extension = lastDot == -1 ? null : filePattern.substring(lastDot + 1);

      if (StringUtil.isEmpty(name))
      {
        name = "." + extension;
        extension = null;
      }

      mapping = new FileEditorMapping(name, extension);
      mappings.put(filePattern, mapping);
    }

    Set<String> editorIDs = getEditorIDs(mapping);
    for (FileEditor fileEditor : getEditors())
    {
      String editorID = fileEditor.getID();
      if (!editorIDs.contains(editorID))
      {
        IEditorDescriptor editor = registry.findEditor(editorID);
        if (editor instanceof EditorDescriptor)
        {
          mapping.addEditor((EditorDescriptor)editor);
        }
      }
    }

    String defaultEditorID = getDefaultEditorID();
    if (!StringUtil.isEmpty(defaultEditorID))
    {
      IEditorDescriptor defaultEditor = registry.findEditor(defaultEditorID);
      if (defaultEditor instanceof EditorDescriptor)
      {
        mapping.setDefaultEditor((EditorDescriptor)defaultEditor);
      }
    }

    performUI(context, new RunnableWithContext()
    {
      public void run(SetupTaskContext context) throws Exception
      {
        registry.setFileEditorMappings(mappings.values().toArray(new FileEditorMapping[mappings.size()]));
        registry.saveAssociations(); // TODO This call takes comparinlgy long. Optimize!

        PrefUtil.savePrefs();
      }
    });
  }

  private static Map<String, FileEditorMapping> getMappings()
  {
    Map<String, FileEditorMapping> result = new HashMap<String, FileEditorMapping>();
    IFileEditorMapping[] mappings = PlatformUI.getWorkbench().getEditorRegistry().getFileEditorMappings();
    for (int i = 0; i < mappings.length; i++)
    {
      IFileEditorMapping mapping = mappings[i];
      if (mapping instanceof FileEditorMapping)
      {
        String name = mapping.getName();
        String extension = mapping.getExtension();

        String pattern = name + (StringUtil.isEmpty(extension) ? "" : "." + extension);
        result.put(pattern, (FileEditorMapping)mapping);
      }
    }

    return result;
  }

  private Set<String> getEditorIDs(IFileEditorMapping mapping)
  {
    Set<String> ids = new HashSet<String>();

    IEditorDescriptor[] editors = mapping.getEditors();
    for (int i = 0; i < editors.length; i++)
    {
      IEditorDescriptor editor = editors[i];
      ids.add(editor.getId());
    }

    return ids;
  }

} // FileAssociationTaskImpl
