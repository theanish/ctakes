/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.ytex.uima.dao;


import org.apache.ctakes.ytex.uima.model.NamedEntityRegex;

import java.util.List;

/**
 * Dao to access NamedEntity Regular Expressions used by the NamedEntityRegexAnnotator
 * @author vijay
 *
 */
public interface NamedEntityRegexDao {

	public abstract List<NamedEntityRegex> getNamedEntityRegexs();

}