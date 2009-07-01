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
 */
package test.fede.workspace.domain.internal;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemDescription;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.delta.revision.ITeamRevisionService;
import fr.imag.adele.cadse.core.delta.revision.WorkspaceLogiqueRevisionDelta;
import fr.imag.adele.cadse.core.impl.CadseCore;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.fede.workspace.si.persistence.Persistence;

public class TestPersistence {

	/**
	 * @throws java.lang.Exception
	 */
	private ItemType	TYPE_A;
	private ItemType	TYPE_B;
	private ItemType	TYPE_C;
	private ItemType	TYPE_D;
	private LinkType	LT_A_TO_B;
	private LinkType	LT_A_TO_D;
	private LinkType	LT_D_TO_B;
	private LinkType	LT_A_TO_C;
	private LinkType	LT_A_TO_C_NO_PART;
	TestSenario			senario;
	Persistence			persistence;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		senario = new TestSenario();
		senario.init();

		TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		LT_A_TO_D = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_D);
		LT_A_TO_C = senario.createLinkType(TYPE_A, LinkType.PART + LinkType.AGGREGATION, 0, -1, null, TYPE_C);
		LT_A_TO_C_NO_PART = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_C);
		LT_D_TO_B = senario.createLinkType(TYPE_D, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
	}

	@Test
	public void testItemToByteArrayItemDescription() throws IOException, CadseException, CoreException,
			NoSuchAlgorithmException, ClassNotFoundException {

		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		a.setAttribute("A1", "sqtqu");
		a.setAttribute("A2", "A5");
		Item b = copy.createItem(TYPE_B, null, null);
		a.createLink(LT_A_TO_B, b);
		copy.commit();
		a = senario.getItem(a.getId());

		byte[] a_byte = Persistence.itemToByteArray(a);

	
		ItemDescription a_desc = Persistence.readFromByteArray(senario.getLogicalWorkspace(), null, a_byte);
		Assert.assertEquals(a_desc, new ItemDescription(a));
	}

}
