/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db;

import org.eclipse.net4j.internal.db.Field;

/**
 * @author Eike Stepper
 */
public interface ITable
{
  public ISchema getSchema();

  public String getName();

  public IField addField(String name, IField.Type type);

  public Field addField(String name, IField.Type type, boolean notNull);

  public Field addField(String name, IField.Type type, int precision);

  public Field addField(String name, IField.Type type, int precision, boolean notNull);

  public Field addField(String name, IField.Type type, int precision, int scale);

  public Field addField(String name, IField.Type type, int precision, int scale, boolean notNull);

  public IField getField(String name);

  public IField getField(int index);

  public int getFieldCount();

  public IField[] getFields();

  public IIndex addIndex(IIndex.Type type, IField field);

  public IIndex addIndex(IIndex.Type type, IField[] fields);

  public int getIndexCount();

  public IIndex[] getIndices();

  public IIndex getPrimaryKeyIndex();
}
