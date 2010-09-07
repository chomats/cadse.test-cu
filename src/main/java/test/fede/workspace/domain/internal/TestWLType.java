/*
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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package test.fede.workspace.domain.internal;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;

public class TestWLType {
	private static TestSenario	senario;
	private ItemType			TYPE_A;
	private ItemType			TYPE_B;
	private LinkType			LT_A_TO_B;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	static public void setUp() throws Exception {
		senario = new TestSenario();
		senario.init();

	}

	@Test
	public void testCreateType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
	}

	@Test
	public void deleteItemType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_A.delete(true);
		senario.remove(TYPE_A);
		Assert.assertNull(senario.getItem(TYPE_A.getId()));
		Assert.assertEquals(senario.getItem(CadseGCST.ITEM_at_NAME_.getId()), CadseGCST.ITEM_at_NAME_);
		Assert.assertEquals(senario.getItem(CadseGCST.ITEM_TYPE.getId()), CadseGCST.ITEM_TYPE);
	}

	@Test
	public void createLinkType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_B = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(TYPE_A.getOutgoingLink(CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES, LT_A_TO_B
				.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));

		Assert.assertNotNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getName()));
		Assert.assertNotNull(LT_A_TO_B.getIncomingLink(CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES, TYPE_A
				.getId()));
	}

	@Test
	public void deleteLinkType() throws Exception {
		TYPE_A = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		TYPE_B = senario.createItemType(null, false, false);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(TYPE_A.getOutgoingLink(CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES, LT_A_TO_B
				.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));

		Assert.assertNotNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getName()));
		Assert.assertNotNull(LT_A_TO_B.getIncomingLink(CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES, TYPE_A
				.getId()));

		Assert.assertNotNull(TYPE_A);
		Assert.assertNotNull(TYPE_B);
		Assert.assertNotNull(LT_A_TO_B);
		Assert.assertNotNull(senario.getItem(TYPE_A.getId()));
		Assert.assertNotNull(senario.getItem(TYPE_B.getId()));
		Assert.assertNotNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getName()));
		Assert.assertNotNull(senario.getItem(LT_A_TO_B.getId()));
		LT_A_TO_B.delete();
		Assert.assertNull(TYPE_A.getOutgoingLinkType(TYPE_B, LT_A_TO_B.getName()));
		Assert.assertNull(TYPE_A.getOutgoingLink(CadseGCST.TYPE_DEFINITION_lt_ATTRIBUTES, LT_A_TO_B
				.getId()));
		Assert.assertNull(senario.getItem(LT_A_TO_B.getId()));
		Assert.assertNull(TYPE_B.getIncomingLinkType(LT_A_TO_B.getName()));
	}
}
