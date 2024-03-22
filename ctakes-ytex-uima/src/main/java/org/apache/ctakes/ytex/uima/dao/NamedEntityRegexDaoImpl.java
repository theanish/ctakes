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
import org.hibernate.SessionFactory;

import java.util.List;


public class NamedEntityRegexDaoImpl implements NamedEntityRegexDao {
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	private SessionFactory sessionFactory;
	/* (non-Javadoc)
	 * @see gov.va.vacs.esld.dao.NamedEntityRegexDao#getNamedEntityRegexs()
	 */
	@SuppressWarnings("unchecked")
	public List<NamedEntityRegex> getNamedEntityRegexs() {
		return (List<NamedEntityRegex>)sessionFactory.getCurrentSession().createQuery("from NamedEntityRegex").list();
	}

}
