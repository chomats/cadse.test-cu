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
/**
 *
 */
package test.fede.workspace.domain.internal;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertEnabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.PageManager;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Messages;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.WorkspaceListener.ListenerKind;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.delta.LinkKey;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.key.SpaceKeyType;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;

/**
 * @author chomats
 *
 */
public class TestWorkingLogiqueCopy {

	// TYPE_A
	public class Create_ITEM_D_DE_A_TO_D implements ICallAction {
		Item	d;
		Link	a_to_d;

		public void notifyCreatedItem(LogicalWorkspaceTransaction wl, ItemDelta item) throws CadseException {
			// LT_A_TO_D
			d = wl.createItem(TYPE_D, null, null);
			a_to_d = item.createLink(LT_A_TO_D, d);
		}

		public void action(Call call) throws CadseException {
			if (call.getType() == CallType.notifyCreatedItem && call.getItemDelta().getType() == TYPE_A) {
				notifyCreatedItem(call.getLogicalWorkspaceTransaction(), call.getItemDelta());
			}
		}
	}

	// TYPE_A
	class Delete_ITEM_D_DE_A_TO_D implements ICallAction {

		public void beforeDeletingItem(ItemDelta item) throws CadseException {
			// LT_A_TO_D
			for (LinkDelta i2 : item.getOutgoingLinkOperations()) {
				if (i2.getLinkType() != LT_A_TO_D) {
					continue;
				}
				i2.getDestination().delete(item.getDeleteOperation().isDeleteContent());
			}
		}

		public void action(Call call) throws CadseException {
			if (call.getType() == CallType.notifyDeletedItem && call.getItemDelta().getType() == TYPE_A) {
				beforeDeletingItem(call.getItemDelta());
			}
		}
	}

	private static GeneratorName static_generator;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		static_generator		= new GeneratorName();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		static_generator = null;
	}

	private ItemType	TYPE_A;
	private ItemType	TYPE_B;
	private ItemType	TYPE_C;
	private ItemType	TYPE_D;
	private LinkType	LT_A_TO_B;
	private LinkType	LT_A_TO_D;
	private LinkType	LT_D_TO_B;
	private LinkType	LT_A_TO_C;
	private LinkType	LT_A_TO_C_NO_PART;
	private LinkType    LT_A_TO_B_ONE_MAX;
	TestSenario			senario;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		/* =============== */
		/* Initializations */
		/* =============== */

		Bundle b = Platform.getBundle("fr.imag.adele.cadse.si.workspace.view");
		b.start();
		//assertEquals(b.getState(), Bundle.ACTIVE);
		
		senario = new TestSenario();
		senario.init();

		TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);
		LT_A_TO_B_ONE_MAX = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, 1, null, TYPE_B);
		LT_A_TO_B = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
		LT_A_TO_D = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_D);
		LT_A_TO_C = senario.createLinkType(TYPE_A, LinkType.PART + LinkType.AGGREGATION, 0, -1, null, TYPE_C);
		LT_A_TO_C_NO_PART = senario.createLinkType(TYPE_A, LinkType.AGGREGATION, 0, -1, null, TYPE_C);
		LT_D_TO_B = senario.createLinkType(TYPE_D, LinkType.AGGREGATION, 0, -1, null, TYPE_B);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		senario.stop();
	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#commit(fr.imag.adele.cadse.core.Item)}.
	 *
	 * @throws CadseException
	 * @throws CoreException
	 */
	@Test
	public void testCommitItem() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
	}

	class MyListner extends WorkspaceListener {
		int	count	= 0;

		MyListner(ListenerKind kind) {
			setKind(kind);
		}

		@Override
		public void workspaceChanged(ImmutableWorkspaceDelta delta) {
			count++;
			
		}
	}
	
	class MyListner2 extends WorkspaceListener {
		int	count	= 0;

		MyListner2(ListenerKind kind) {
			setKind(kind);
		}

		@Override
		public void workspaceChanged(ImmutableWorkspaceDelta delta) {
			count++;
			System.out.println("My listener 2 : "+count);
			System.out.println(delta);
		}
	}

	class MyItemDeltaListner extends WorkspaceListener {
		int	count	= 0;
Item currentItem;
		MyItemDeltaListner(ListenerKind kind) {
			setKind(kind);
		}

		@Override
		public void workspaceChanged(ImmutableWorkspaceDelta delta) {
			if (delta.getItem(currentItem) == null) return;
			count++;

		}
		public void setCurrentItem(Item currentItem) {
			this.currentItem = currentItem;
		}

	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.CompactUUID, java.lang.String, java.lang.String)}.
	 *
	 * @throws CadseException
	 */
	@Test
	public void testCreateItemItemTypeItemLinkTypeCompactUUIDStringString() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();

		Item a = copy.createItem(TYPE_A, null, null);
		Item a2 = copy.getItem(a.getId());
		assertNotNull(a2);
		assertTrue(a == a2);
		a.delete(false);
		a2 = copy.getItem(a.getId());
		assertNull(a2);

	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.CompactUUID, java.lang.String, java.lang.String)}.
	 *
	 * @throws CadseException
	 * @throws CoreException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 */
	@Test
	public void testWorkspaceListener() throws CadseException, CoreException, InterruptedException, TimeoutException {
		System.out.println("[testWorkspaceListener] begin...");
		
		// attendre que les evenements précedent sont passé.
		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		
		MyListner2 myListner = new MyListner2(ListenerKind.UI);
		senario.addListener(myListner, 0x7F);
		Item a = copy.createItem(TYPE_A, null, null);
		a.setName(senario.newName());
		copy.commit();

		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		System.out.println("[testWorkspaceListener] end...");

		
		Assert.assertTrue("Listener not called", myListner.count == 1);

	}

	/**
	 * Test method for
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.CompactUUID, java.lang.String, java.lang.String)}.
	 *
	 * @throws CadseException
	 * @throws CoreException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 */
	@Test
	public void testItemListener() throws CadseException, CoreException, InterruptedException, TimeoutException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		a.setName(senario.newName());
		copy.commit();
		MyItemDeltaListner myItemDeltaListner = new MyItemDeltaListner(ListenerKind.UI);
		a = senario.getLogicalWorkspace().getItem(a.getId());
		myItemDeltaListner.setCurrentItem(a);
		a.addListener(myItemDeltaListner, ChangeID.toFilter(ChangeID.CREATE_OUTGOING_LINK));

		copy = senario.getLogicalWorkspace().createTransaction();
		Item a2 = copy.createItem(TYPE_A, null, null);
		a2.setName(senario.newName());
		copy.commit();
		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		Assert.assertTrue("Listener called", myItemDeltaListner.count == 1);

		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getItem(a.getId()).setAttribute("As", "test");
		Item b = copy.createItem(TYPE_B, null, null);
		b.setName(senario.newName());
		copy.commit();
		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		Assert.assertTrue("Listener called", myItemDeltaListner.count == 1);

		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getItem(a.getId()).createLink(LT_A_TO_B, senario.getLogicalWorkspace().getItem(b.getId()));
		copy.commit();
		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		Assert.assertTrue("Listener called", myItemDeltaListner.count == 2);

	}

	@Test
	public void testCreateItemItemTypeItemLinkTypeCompactUUIDStringString2() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		Item a2 = copy.getItem(a.getId());
		assertNotNull(a2);
		assertTrue(a == a2);
		a.delete(false);

	}

	@Test
	public void testCreateItemPart() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		a = senario.getLogicalWorkspace().getItem(a.getId());
		copy = senario.getLogicalWorkspace().createTransaction();
		Item c = copy.createItem(TYPE_C, a, LT_A_TO_C);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNotNull(senario.getLogicalWorkspace().getItem(c.getId()));
		assertNotNull(a.getOutgoingLink(LT_A_TO_C, c.getId()));
	}

	/**
	 * Create an A implies create an D
	 * 
	 * Create A and commit 
	 * Test 'A' and 'D' is created
	 * Test link A to D is created
	 * 
	 * @throws CadseException
	 * @throws CoreException
	 */
	@Test
	public void testCreateAImpliesCreateD() throws CadseException, CoreException {
		Create_ITEM_D_DE_A_TO_D create_ITEM_D_DE_A_TO_D = new Create_ITEM_D_DE_A_TO_D();
		senario.callActions.add(create_ITEM_D_DE_A_TO_D);
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		Item a = copy.createItem(TYPE_A, null, null);
		copy.commit();
		a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(a);
		Item d_0 = create_ITEM_D_DE_A_TO_D.d;
		assertNotNull(d_0);
		assertNotNull(senario.getLogicalWorkspace().getItem(d_0.getId()));
		assertNotNull(create_ITEM_D_DE_A_TO_D.a_to_d);
		assertNotNull(a.getOutgoingLink(LT_A_TO_D, d_0.getId()));
		assertOutgoingLinksItem(a);
	}

	@Test
	public void testDeletAImpliesDeleteD() throws CadseException, CoreException {
		Create_ITEM_D_DE_A_TO_D create_ITEM_D_DE_A_TO_D = new Create_ITEM_D_DE_A_TO_D();
		senario.callActions.add(create_ITEM_D_DE_A_TO_D);
		senario.callActions.add(new Delete_ITEM_D_DE_A_TO_D());
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		Item a = copy.createItem(TYPE_A, null, null);
		copy.commit();
		a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(a);
		Item d_0 = create_ITEM_D_DE_A_TO_D.d;
		assertNotNull(d_0);
		assertNotNull(senario.getLogicalWorkspace().getItem(d_0.getId()));
		assertNotNull(create_ITEM_D_DE_A_TO_D.a_to_d);
		assertNotNull(a.getOutgoingLink(LT_A_TO_D, d_0.getId()));
		assertOutgoingLinksItem(a);

		copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		copy.getItem(a.getId()).delete(true);
		copy.commit();
		assertNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNull(senario.getLogicalWorkspace().getItem(d_0.getId()));
	}

	@Test
	public void testCreateItem3i2l() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		Item a = copy.createItem(TYPE_A, null, null);
		Item b = copy.createItem(TYPE_B, null, null);
		Item d = copy.createItem(TYPE_D, null, null);
		copy.commit();

		// senario.assertCall(new Call(CallType.beforeCreatingItem, a, null,
		// null), new Call(CallType.beforeCreatingItem,
		// b, null, null), new Call(CallType.beforeCreatingItem, d, null, null),
		// new Call(CallType.createdItem,
		// senario.getLogicalWorkspace().getItem(a.getId()), null, null), new
		// Call(CallType.createdItem, senario
		// .getLogicalWorkspace().getItem(d.getId()), null, null), new
		// Call(CallType.createdItem, senario
		// .getLogicalWorkspace().getItem(b.getId()), null, null));
		a = senario.getLogicalWorkspace().getItem(a.getId());
		b = senario.getLogicalWorkspace().getItem(b.getId());
		d = senario.getLogicalWorkspace().getItem(d.getId());
		assertNotNull(a);
		assertNotNull(b);
		assertNotNull(d);

		senario.clearCalls();
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		ItemDelta a2 = copy.getItem(a.getId());
		ItemDelta d2 = copy.getItem(d.getId());
		LinkDelta l1 = a2.createLink(LT_A_TO_D, d);
		LinkDelta l2 = d2.createLink(LT_D_TO_B, b);
		copy.commit();

		Link l2_1 = a.getOutgoingLink(LT_A_TO_D, d.getId());
		assertNotNull(l2_1);
		Link l2_2 = d.getOutgoingLink(LT_D_TO_B, b.getId());
		assertNotNull(l2_2);
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(d.getId()));
		senario.assertCall(new Call(copy, CallType.notifyCreatedLink, l1), new Call(copy, CallType.notifyCreatedLink,
				l2));

		senario.clearCalls();
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.addLogicalWorkspaceTransactionListener(senario.getLogicalWorkspaceTransactionListener());
		Item d3 = copy.getItem(d.getId());
		Item a3 = copy.getItem(a.getId());
		Link l3_1 = a3.getOutgoingLink(LT_A_TO_D, d.getId());
		Link l3_2 = d3.getOutgoingLink(LT_D_TO_B, b.getId());
		d3.delete(true);

		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNotNull(senario.getLogicalWorkspace().getItem(b.getId()));
		assertNull(senario.getLogicalWorkspace().getItem(d.getId()));

		// senario.assertCall(new Call(CallType.beforeDeletingLink, null, l3_1,
		// null), new Call(
		// CallType.beforeDeletingLink, null, l3_2, null), new
		// Call(CallType.beforeDeletingDestinationItem, null,
		// l3_1, d3), new Call(CallType.beforeDeletingItem, d3, null, null), new
		// Call(CallType.deletedLink, null,
		// l2_1, null), new Call(CallType.deletedLink, null, l2_2, null), new
		// Call(
		// CallType.deletedDestinationItem, null, null, d), new
		// Call(CallType.deletedItem, d, null, null));

	}

	@Test
	public void testCreateItemPart_fail() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		a = senario.getLogicalWorkspace().getItem(a.getId());
		copy = senario.getLogicalWorkspace().createTransaction();
		try {
			Item c = copy.createItem(TYPE_C, a, LT_A_TO_C_NO_PART);
		} catch (CadseException e) {
			assertMelusineError(e, Messages.error_parent_link_type_not_part, LT_A_TO_C_NO_PART);
			return;
		}
		fail("exception not raised");

	}

	private void assertMelusineError(CadseException e, String msg, Object... args) throws CadseException {
		if (e.getMsg().equals(msg) && Arrays.equals(e.getArgs(), args)) {
			return;
		}
		throw e;
	}
	
	private void assertMelusineError(CadseIllegalArgumentException e, String msg, Object... args) throws CadseException {
		if (e.getMsg().equals(msg) && Arrays.equals(e.getArgs(), args)) {
			return;
		}
		throw e;
	}

	@Test
	public void testDeleteItemPart() throws CadseException, CoreException {
		senario.clearCalls();
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		Item c = copy.createItem(TYPE_C, a, LT_A_TO_C);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNotNull(senario.getLogicalWorkspace().getItem(c.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		// senario.assertCall(new Call(CallType.beforeCreatingLink, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()), null),
		// new Call(CallType.beforeCreatingDestinationItem, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()), c),
		// new Call(CallType.beforeCreatingItem, a, null, null), new
		// Call(CallType.beforeCreatingItem, c, null,
		// null), new Call(CallType.createdLink, null,
		// senario.getLogicalWorkspace().getItem(a.getId())
		// .getOutgoingLink(LT_A_TO_C, c.getId()), null), new
		// Call(CallType.createdDestinationItem, null,
		// null, senario.getLogicalWorkspace().getItem(c.getId())), new
		// Call(CallType.createdItem, senario
		// .getLogicalWorkspace().getItem(a.getId()), null, null), new
		// Call(CallType.createdItem, senario
		// .getLogicalWorkspace().getItem(c.getId()), null, null)
		//
		// );
		a = senario.getLogicalWorkspace().getItem(a.getId());
		c = senario.getLogicalWorkspace().getItem(c.getId());
		Link a_to_c = a.getOutgoingLink(LT_A_TO_C, c.getId());
		assertNotNull(a_to_c);
		copy = senario.getLogicalWorkspace().createTransaction();
		Item a2 = copy.getItem(a.getId());
		Item c2 = copy.getItem(c.getId());
		Link a2_to_c2 = a2.getOutgoingLink(LT_A_TO_C, c.getId());
		a2.delete(true);

		senario.clearCalls();
		copy.commit();

		// senario.assertCall(new Call(CallType.beforeDeletingLink, null,
		// a2_to_c2, null), new Call(
		// CallType.beforeDeletingDestinationItem, null, a2_to_c2, c2), new
		// Call(CallType.beforeDeletingItem, a2,
		// null, null), new Call(CallType.beforeDeletingItem, c2, null, null),
		// new Call(CallType.deletedLink,
		// null, a_to_c, null), new Call(CallType.deletedDestinationItem, null,
		// null, c), new Call(
		// CallType.deletedItem, a, null, null), new Call(CallType.deletedItem,
		// c, null, null)
		//
		// );
		assertNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNull(senario.getLogicalWorkspace().getItem(c.getId()));

	}

	@Test
	public void testDeleteItemPart_2() throws CadseException, CoreException {
		senario.clearCalls();
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		Item c = copy.createItem(TYPE_C, a, LT_A_TO_C);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNotNull(senario.getLogicalWorkspace().getItem(c.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));

		// senario
		// .assertCall(new Call(CallType.beforeCreatingLink, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()), null),
		// new Call(CallType.beforeCreatingDestinationItem, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()),
		// c), new Call(CallType.beforeCreatingItem, a, null, null),
		// // new Call(CallType.createdSourceItem,
		// // senario.getLogicalWorkspace().getItem(a.getId()),
		// // null, null),
		// new Call(CallType.createdDestinationItem, null, null,
		// senario.getLogicalWorkspace().getItem(
		// c.getId())), new Call(CallType.createdLink, null,
		// senario.getLogicalWorkspace()
		// .getItem(a.getId()).getOutgoingLink(LT_A_TO_C, c.getId()), null), new
		// Call(
		// CallType.createdItem,
		// senario.getLogicalWorkspace().getItem(a.getId()), null, null));
		a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(a.getOutgoingLink(LT_A_TO_C, c.getId()));
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getItem(a.getId()).delete(true);
		copy.commit();
		assertNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNull(senario.getLogicalWorkspace().getItem(c.getId()));

	}

	@Test
	public void testDelete_b() throws CadseException, CoreException {
		senario.clearCalls();
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta a = copy.createItem(TYPE_A, null, null);
		ItemDelta c = copy.createItem(TYPE_C, a, LT_A_TO_C);
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNotNull(senario.getLogicalWorkspace().getItem(c.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));

		// senario
		// .assertCall(new Call(CallType.notifyCreatedLink, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()), null),
		// new Call(CallType.beforeCreatingDestinationItem, null,
		// a.getOutgoingLink(LT_A_TO_C, c.getId()),
		// c), new Call(copy, CallType.notifyCreatedItem, a),
		// // new Call(CallType.createdSourceItem,
		// // senario.getLogicalWorkspace().getItem(a.getId()),
		// // null, null),
		// new Call(CallType.createdDestinationItem, null, null,
		// senario.getLogicalWorkspace().getItem(
		// c.getId())), new Call(CallType.createdLink, null,
		// senario.getLogicalWorkspace()
		// .getItem(a.getId()).getOutgoingLink(LT_A_TO_C, c.getId()), null), new
		// Call(
		// CallType.createdItem,
		// senario.getLogicalWorkspace().getItem(a.getId()), null, null));
		Item abis = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(abis.getOutgoingLink(LT_A_TO_C, c.getId()));
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getItem(abis.getId()).delete(true);
		copy.commit();
		assertNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertNull(senario.getLogicalWorkspace().getItem(c.getId()));

	}

	private void assertOutgoingLinksItem(Item a) {
		List<Link> links = a.getOutgoingLinks();
		Map<LinkKey, Link> uniquelinks = new HashMap<LinkKey, Link>();
		for (Link l : links) {
			LinkKey key = new LinkKey(l);

			Link oldL = uniquelinks.get(key);
			if (oldL != null) {
				fail("Duplicate link " + oldL);
			}
			uniquelinks.put(key, l);
		}
	}

	@Test
	public void testDeleteItemItemTypeItemLinkTypeCompactUUIDStringString2() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		Item a2 = copy.getItem(a.getId());
		assertNotNull(a2);
		Item wl_a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNull(wl_a);
		assertTrue(a == a2);
		copy.commit();
		wl_a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(wl_a);

		copy = senario.getLogicalWorkspace().createTransaction();
		Item c1_a = copy.getItem(a.getId());
		c1_a.delete(true);
		wl_a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNotNull(wl_a);
		copy.commit();
		wl_a = senario.getLogicalWorkspace().getItem(a.getId());
		assertNull(wl_a);
	}

	@Test
	public void testCreateLink() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item a = senario.getLogicalWorkspace().getItem(newa.getId());
		Item b = senario.getLogicalWorkspace().getItem(newb.getId());
		assertNotNull(a);
		assertNotNull(b);

		copy = senario.getLogicalWorkspace().createTransaction();
		Item copya = copy.getItem(a.getId());
		copya.createLink(LT_A_TO_B, b);
		Link l_a_to_b = a.getOutgoingLink(LT_A_TO_B, b.getId());
		assertNull(l_a_to_b);
		copy.commit();

		l_a_to_b = a.getOutgoingLink(LT_A_TO_B, b.getId());
		assertNotNull(l_a_to_b);
	}

	@Test
	public void testDeleteLink() throws CadseException, CoreException {
		// create two item a and b
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item a = senario.getLogicalWorkspace().getItem(newa.getId());
		Item b = senario.getLogicalWorkspace().getItem(newb.getId());
		assertNotNull(a);
		assertNotNull(b);

		// create a link a to b
		copy = senario.getLogicalWorkspace().createTransaction();
		Item copya = copy.getItem(a.getId());
		copya.createLink(LT_A_TO_B, b);
		assertNull(a.getOutgoingLink(LT_A_TO_B, b.getId()));
		copy.commit();
		assertNotNull(a.getOutgoingLink(LT_A_TO_B, b.getId()));

		// delete the link a to b
		copy = senario.getLogicalWorkspace().createTransaction();
		Item copya2 = copy.getItem(a.getId());
		Link l = copya2.getOutgoingLink(LT_A_TO_B, b.getId());
		l.delete();
		// the link exist toujours pas de commit
		assertNotNull(a.getOutgoingLink(LT_A_TO_B, b.getId()));
		copy.commit();
		// the link is deleted
		assertNull(a.getOutgoingLink(LT_A_TO_B, b.getId()));
		a = senario.getLogicalWorkspace().getItem(newa.getId());
		b = senario.getLogicalWorkspace().getItem(newb.getId());
		// a and b exit.
		assertNotNull(a);
		assertNotNull(b);

	}

	@Test
	public void testDeleteUnresolvedLink() {

	}

	@Test
	public void testDeleteUnresolvedPartLink() {

	}

	@Test
	public void testDeletePartLink() {

	}
	
	@Test
	public void testCreateLinkBadDest() throws CadseException {
		/*TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);*/
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newd = copy.createItem(TYPE_D, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newdC = newd.getBaseItem();
		
		try {
			copy = senario.getLogicalWorkspace().createTransaction();
			copy.getOrCreateItemOperation(newaC).createLink(LT_A_TO_B, newdC);
		} catch(CadseException e) {
			assertMelusineError(e, Messages.cannot_create_link_bad_link_type,
					newaC.getId(), newaC.getQualifiedName(), newdC.getId(), newdC.getQualifiedName(), LT_A_TO_B.getName(), LT_A_TO_B
							.getDestination().getName(), newdC.getType().getName());
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testCreateLinkDeletedDest() throws CadseException {
		/*TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);*/
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		
		try {
			copy = senario.getLogicalWorkspace().createTransaction();
			copy.getOrCreateItemOperation(newbC).delete(true);
			copy.getOrCreateItemOperation(newaC).createLink(LT_A_TO_B, newbC);
		} catch(CadseException e) {
			assertMelusineError(e, Messages.cannot_create_link_to_deleted_destination,
					newaC, newbC);
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testgetOutgoingLinkFailLinkTypeNull() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		
		try {
			newaC.getOutgoingLink(null);
		} catch(CadseIllegalArgumentException e) {
			assertMelusineError(e, Messages.error_linktype_is_null);
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testgetOutgoingLinkMax1() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		newa.createLink(LT_A_TO_B_ONE_MAX, newb);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		assertNotNull(newaC);
		assertNotNull(newbC);
		
		Link a_to_b = newaC.getOutgoingLink(LT_A_TO_B_ONE_MAX);
		assertLink(LT_A_TO_B_ONE_MAX, newaC, newbC, a_to_b);
		
		
		Link a_to_b2 = newbC.getIncomingLink(LT_A_TO_B_ONE_MAX, newaC.getId());
		assertEquals(a_to_b, a_to_b2);
		
		List<Link> links = newbC.getIncomingLinks(LT_A_TO_B_ONE_MAX);
		assertEquals(1, links.size());
		assertEquals(a_to_b, links.get(0));
		
	}
	
	@Test
	public void testgetItemDeleted() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		assertNotNull(newaC);
		copy = senario.getLogicalWorkspace().createTransaction();
		Item a1 = copy.getItem(newaC.getId(), false);
		assertNotNull(a1);
		assertEquals(a1, copy.getItem(newaC.getId()));
		assertEquals(a1, copy.getItem(newaC));
		a1.delete(true);
		Item a2 = copy.getItem(newaC.getId(), false);
		assertNull(a2);
		assertNull(copy.getItem(newaC.getId()));
		assertNull(copy.getItem(newaC));
		assertEquals(a1, copy.getItem(newaC.getId(), true));		
	}
	
	@Test
	public void testKey() throws CadseException {
		SpaceKeyType spaceKeytype = new SpaceKeyType(TYPE_A, null);
		TYPE_A.setSpaceKeyType(spaceKeytype);
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		assertNotNull(newa);
		String k1 = senario.generator.newName();
		newa.setName(k1);
		assertTrue(copy.containsSpaceKey(newa.getKey()));
		assertTrue(copy.containsSpaceKey(spaceKeytype.computeKey(k1, null)));
		assertFalse(copy.existsItem(newa));
		assertTrue(newa.exists());
		assertEquals(newa, copy.getItem(newa.getKey()));	
		assertNull(senario.getLogicalWorkspace().getItem(newa.getKey()));
		copy.commit();
		
		Item newaC = newa.getBaseItem();
		assertNotNull(newaC);
		assertEquals(newaC, senario.getLogicalWorkspace().getItem(newa.getKey()));	
		
		// delete
		copy = senario.getLogicalWorkspace().createTransaction();
		Item a1 = copy.getItem(newaC.getId(), false);
		assertNotNull(a1);
		a1.delete(true);
		assertNotNull(senario.getLogicalWorkspace().getItem(newa.getKey()));
		assertTrue(!copy.containsSpaceKey(newa.getKey()));
		assertTrue(!copy.containsSpaceKey(spaceKeytype.computeKey(k1, null)));
		assertNull(copy.getItem(newa.getKey()));	
		copy.commit();
		assertNull(senario.getLogicalWorkspace().getItem(newa.getKey()));
		
		
		//recreate a
		copy = senario.getLogicalWorkspace().createTransaction();
		newa = copy.createItem(TYPE_A, null, null);
		assertNotNull(newa);
		k1 = senario.generator.newName();
		newa.setName(k1);
		assertTrue(copy.containsSpaceKey(newa.getKey()));
		assertTrue(copy.containsSpaceKey(spaceKeytype.computeKey(k1, null)));
		assertEquals(newa, copy.getItem(newa.getKey()));	
		assertNull(senario.getLogicalWorkspace().getItem(newa.getKey()));
		copy.commit();
		newaC = newa.getBaseItem();

		//rename
		copy = senario.getLogicalWorkspace().createTransaction();
		a1 = copy.getItem(newaC.getId(), false);
		assertNotNull(a1);
		String k2 = senario.generator.newName();
		a1.setName(k2);
		assertNull(senario.getLogicalWorkspace().getItem(a1.getKey()));
		assertTrue(!copy.containsSpaceKey(newa.getKey()));
		assertTrue(!copy.containsSpaceKey(spaceKeytype.computeKey(k1, null)));
		assertNull(copy.getItem(newa.getKey()));	
		
		assertTrue(copy.containsSpaceKey(a1.getKey()));
		assertTrue(copy.containsSpaceKey(spaceKeytype.computeKey(k2, null)));
		assertNotNull(copy.getItem(a1.getKey()));	
		assertNotNull(copy.getItem(spaceKeytype.computeKey(k2, null)));	
		
		copy.commit();
		assertNull(senario.getLogicalWorkspace().getItem(newa.getKey()));
		assertEquals(newaC, senario.getLogicalWorkspace().getItem(a1.getKey()));	
		
		// create a cadse def with item-type, link-type, page, attribute (bool, string, integer)
		String nameCadse = static_generator.newName();
		LogicalWorkspace lw = senario.getLogicalWorkspace();
		
		assertNull(lw.getItem(CadseGCST.CADSE_DEFINITION.getSpaceKeyType().computeKey(nameCadse, null)));
		Item cadseDefCommited = createCadseDefinition(nameCadse);
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+nameCadse, cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemTypeAndCheck(dm);
		checkType(lw, cadseDefCommited, TypeACommited);
		
		Item TypeBCommited = createItemType(dm);
		checkType(lw, cadseDefCommited, TypeBCommited);
		ItemDelta LinkA_TypeA = createLinkType(TypeACommited, TypeBCommited);
		
		checkIncomingLinks(TypeACommited);
		checkIncomingLinks(TypeBCommited);
		checkIncomingLinks(LinkA_TypeA.getBaseItem());
		checkIncomingLinks(dm);
		
		Item attrBool1 = createAttributeBool(TypeACommited);
		Item attrInt1 = createAttributeInt(TypeACommited);
		Item attrString1= createAttributeString(TypeACommited);
		Item attrLong1 = createAttributeLong(TypeACommited);
		
		Item et = createEnumType(dm);
		Item attrEnum1 = createAttributeEnum(TypeACommited, et);
		
		assertEquals(cadseDefCommited, lw.getItem(cadseDefCommited.getKey()));
		assertEquals(cadseDefCommited, lw.getItem(CadseGCST.CADSE_DEFINITION.getSpaceKeyType().computeKey(cadseDefCommited.getName(), null)));
		assertEquals(dm, lw.getItem(dm.getKey()));
		assertEquals(dm, lw.getItem(CadseGCST.DATA_MODEL.getSpaceKeyType().computeKey(dm.getName(), cadseDefCommited)));
		
		assertEquals(TypeACommited, lw.getItem(TypeACommited.getKey()));
		assertEquals(TypeACommited, lw.getItem(CadseGCST.ITEM_TYPE.getSpaceKeyType().computeKey(TypeACommited.getName(), cadseDefCommited)));

		assertEquals(TypeBCommited, lw.getItem(TypeBCommited.getKey()));
		assertEquals(TypeBCommited, lw.getItem(CadseGCST.ITEM_TYPE.getSpaceKeyType().computeKey(TypeBCommited.getName(), cadseDefCommited)));
		
		assertEquals(attrBool1, lw.getItem(attrBool1.getKey()));
		assertEquals(attrBool1, lw.getItem(CadseGCST.ATTRIBUTE.getSpaceKeyType().computeKey(attrBool1.getName(), TypeACommited)));
		
		assertEquals(attrInt1, lw.getItem(attrInt1.getKey()));
		assertEquals(attrInt1, lw.getItem(CadseGCST.ATTRIBUTE.getSpaceKeyType().computeKey(attrInt1.getName(), TypeACommited)));
		
		assertEquals(attrString1, lw.getItem(attrString1.getKey()));
		assertEquals(attrString1, lw.getItem(CadseGCST.ATTRIBUTE.getSpaceKeyType().computeKey(attrString1.getName(), TypeACommited)));
		
		assertEquals(attrLong1, lw.getItem(attrLong1.getKey()));
		assertEquals(attrLong1, lw.getItem(CadseGCST.ATTRIBUTE.getSpaceKeyType().computeKey(attrLong1.getName(), TypeACommited)));
	
		ItemType it = (ItemType) TypeACommited;
		
		assertEquals(attrBool1, it.getAttributeType(attrBool1.getName()));
		assertEquals(attrInt1, it.getAttributeType(attrInt1.getName()));
		assertEquals(attrString1, it.getAttributeType(attrString1.getName()));
		
		assertNotNull(it.getFirstCreatedPage());
		assertNotNull(it.getFirstModificationPage());
		
		List<Link> pages = it.getOutgoingLinks(CadseGCST.ITEM_TYPE_lt_CREATION_PAGES);
		assertEquals(1, pages.size());
		assertEquals(it.getFirstCreatedPage(), pages.get(0).getDestination());
		
		pages = it.getOutgoingLinks(CadseGCST.ITEM_TYPE_lt_MODIFICATION_PAGES);
		assertEquals(1, pages.size());
		assertEquals(it.getFirstModificationPage(), pages.get(0).getDestination());
		
		
		Item cd  = it.getOutgoingItem(CadseGCST.ABSTRACT_ITEM_TYPE_lt_CREATION_DIALOG, true);
		Item md  = it.getOutgoingItem(CadseGCST.ABSTRACT_ITEM_TYPE_lt_MODIFICATION_DIALOG, true);
		
		assertNotNull(cd);
		assertNotNull(md);
		
	}

	private void checkType(LogicalWorkspace lw, Item cadseDefCommited,
			Item TypeACommited) {
		assertEquals(true, TypeACommited.getAttribute(CadseGCST.ITEM_TYPE_at_IS_ROOT_ELEMENT_));
		assertEquals(true, TypeACommited.getAttribute(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_));
		assertEquals(false, TypeACommited.getAttribute(CadseGCST.ITEM_TYPE_at_IS_ABSTRACT_));
		assertEquals(cadseDefCommited, TypeACommited.getOutgoingItem(CadseGCST.ITEM_TYPE_lt_CADSE_RUNTIME, true));
		
		Item managerTypeA = TypeACommited.getIncomingItem(CadseGCST.MANAGER_lt_ITEM_TYPE);
		assertNotNull(managerTypeA);
		assertEquals("${#parent.qualified-name}{.}${#name}", managerTypeA.getAttribute(CadseGCST.MANAGER_at_LONG_NAME_TEMPLATE_));
		assertEquals("${#name}", managerTypeA.getAttribute(CadseGCST.MANAGER_at_DISPLAY_NAME_TEMPLATE_));
		assertEquals(TypeACommited.getName()+"-manager", managerTypeA.getName());
		checkIncomingLinks(managerTypeA);
		assertEquals(managerTypeA, lw.getItem(managerTypeA.getQualifiedName()));
	}
	
	

	@Test
	public void testgetOutgoingLinkMax1_notfound() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		assertNotNull(newaC);
		assertNotNull(newbC);
		
		Link a_to_b = newaC.getOutgoingLink(LT_A_TO_B_ONE_MAX);
		assertNull(a_to_b);
	}

	private void assertLink(LinkType lt, Item srcItem, Item dstItem, Link link) {
		assertNotNull(link);
		assertEquals(srcItem, link.getSource());
		assertEquals(dstItem, link.getDestination());
		assertEquals(lt, link.getLinkType());
	}
	
	@Test
	public void testgetOutgoingLinkFailMax1() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		newa.createLink(LT_A_TO_B, newb);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		assertNotNull(newaC);
		assertNotNull(newbC);
		
		try {
			newaC.getOutgoingLink(LT_A_TO_B);
		} catch(CadseIllegalArgumentException e) {
			assertMelusineError(e, Messages.error_maximum_cardinality_must_be_one, LT_A_TO_B.getName());
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testgetIncomingLinksFail() throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		
		try {
			newaC.getIncomingLinks(null);
		} catch(CadseIllegalArgumentException e) {
			assertMelusineError(e, Messages.error_linktype_is_null);
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testMoveLinks_2() throws CadseException {
		testMoveLinks(2);
		
	}
	
	@Test
	public void testMoveLinks_3() throws CadseException {
		testMoveLinks(3);
	}
	
	@Test
	public void testMoveLinks_4() throws CadseException {
		testMoveLinks(4);
	}
	
	@Test
	public void testMoveLinks_5() throws CadseException {
		testMoveLinks(5);
	}
	
	@Test
	public void testMoveLinks_6() throws CadseException {
		testMoveLinks(6);
	}
	
	
	public void testMoveLinks(int nb) throws CadseException {
		Item[] dest= new Item[nb];
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		for (int i = 0; i < dest.length; i++) {
			dest[i] = copy.createItem(TYPE_B, null, null);
			dest[i].setName("b"+i);
			assertNotNull(dest[i]);
		}
		for (int i = 0; i < dest.length; i++) {
			newa.createLink(LT_A_TO_B, dest[i]);
		}
		copy.commit();
		Item newaC = newa.getBaseItem();
		
		for (int i = 0; i < dest.length; i++) {
			dest[i] = dest[i].getBaseItem();
			assertNotNull(dest[i]);
			checkIncomingLinks(dest[i]);
		}
		assertNotNull(newaC);
		checkIncomingLinks(newaC);
		
		// move link_1 before link_0 (1 -> 0)
		assertMove(newaC, LT_A_TO_B, dest);
		
	}
	
	private void assertMove(Item srcItem, LinkType lt, Item ...dest) throws CadseException {
		
		assertlink(srcItem, lt, dest);
		int nb = dest.length;
		int nbcomp = nb*nb - nb;
		int[] i_comp =  new int[nbcomp];
		int[] j_comp =  new int[nbcomp];
		int k = 0;
		for (int i = 0; i < nb; i++) {
			for (int j = 0; j < nb; j++) {
				if (i == j) continue;
				i_comp[k] = i;
				j_comp[k] = j;
				k++;
			}
		}
		
		for (int z = 0; z < nbcomp; z++) {
			k = senario.generator.getint(nbcomp);
			int i = i_comp[k];
			int j = j_comp[k];
			List<Link> links = srcItem.getOutgoingLinks(lt);
			if (i > j) {
				LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
				copy.getLink(links.get(i)).moveBefore(links.get(j));
				copy.commit();
				Item temp = dest[i];
				System.arraycopy(dest, j, dest, j+1, i-j);
				dest[j] = temp;				
			} else {
				LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
				copy.getLink(links.get(i)).moveAfter(links.get(j));
				copy.commit();
				Item temp = dest[i];
				System.arraycopy(dest, i+1, dest, i, j-i);
				dest[j] = temp;			
			}
			assertlink(srcItem, lt, dest);
			for (int t = 0; t < dest.length; t++) {
				checkIncomingLinks(dest[t]);
			}
			checkIncomingLinks(srcItem);
		}		
	}
	// key
	// exist in trans
	
	private void assertlink(Item srcItem, LinkType lt, Item ...dest) {
		List<Link> links = srcItem.getOutgoingLinks(lt);
		assertEquals(dest.length, links.size());
		for (int i = 0; i < dest.length; i++) {
			assertLink(lt, srcItem, dest[i], links.get(i));
		}
	}
	

	@Test
	public void testCreateLinkDeletedSource() throws CadseException {
		/*TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);*/
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		
		try {
			copy = senario.getLogicalWorkspace().createTransaction();
			copy.getOrCreateItemOperation(newaC).delete(true);
			copy.getOrCreateItemOperation(newaC).createLink(LT_A_TO_B, newbC);
		} catch(CadseException e) {
			assertMelusineError(e, Messages.cannot_create_link_from_deleted_source,
					newaC, newbC);
			return;
		}
		fail("exception not raised!");
	}
	
	
	@Test
	public void testCreateLinkUnexistingSource() throws CadseException {
		/*TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);*/
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getOrCreateItemOperation(newaC).delete(true);
		copy.commit();
		try {
			copy = senario.getLogicalWorkspace().createTransaction();
			copy.getOrCreateItemOperation(newaC).createLink(LT_A_TO_B, newbC);
		} catch(CadseException e) {
			assertMelusineError(e, Messages.cannot_create_link_from_unexisting_source,
					newaC, newbC);
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testCreateLinkUnexistingDestination() throws CadseException {
		/*TYPE_A = senario.createItemType(null, false, false);
		TYPE_B = senario.createItemType(null, false, false);
		TYPE_C = senario.createItemType(null, false, false);
		TYPE_D = senario.createItemType(null, false, false);*/
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item newa = copy.createItem(TYPE_A, null, null);
		Item newb = copy.createItem(TYPE_B, null, null);
		copy.commit();
		Item newaC = newa.getBaseItem();
		Item newbC = newb.getBaseItem();
		
		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getOrCreateItemOperation(newbC).delete(true);
		copy.commit();
		try {
			copy = senario.getLogicalWorkspace().createTransaction();
			copy.getOrCreateItemOperation(newaC).createLink(LT_A_TO_B, newbC);
		} catch(CadseException e) {
			assertMelusineError(e, Messages.cannot_create_link_to_unexisting_destination,
					newaC, newbC);
			return;
		}
		fail("exception not raised!");
	}
	
	@Test
	public void testCreateCadsegLink() throws CadseException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		Item TypeBCommited = createItemType(dm);
		ItemDelta LinkA_TypeA = createLinkType(TypeACommited, TypeBCommited);
		
		checkIncomingLinks(TypeACommited);
		checkIncomingLinks(TypeBCommited);
		checkIncomingLinks(LinkA_TypeA.getBaseItem());
		checkIncomingLinks(dm);
	}
	
	@Test
	public void testFailBadParent() throws CadseException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		try {
			copy.createItem(CadseGCST.ENUM_TYPE, dm, CadseGCST.DATA_MODEL_lt_ENUMS);
		} catch (CadseException e) {
			assertMelusineError(e, Messages.error_cannot_create_an_item_bad_destination, dm.getName(), CadseGCST.DATA_MODEL_lt_ENUMS
					.getName(), CadseGCST.DATA_MODEL_lt_ENUMS.getDestination().getName(),
					CadseGCST.DATA_MODEL_lt_ENUMS.getDestination().getId(), CadseGCST.ENUM_TYPE.getName(), 
					CadseGCST.ENUM_TYPE.getId());
			return;
		}	
			
	}
	@Test
	public void testRenameCadseg() throws CadseException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		Item TypeBCommited = createItemType(dm);
		ItemDelta LinkA_TypeA = createLinkType(TypeACommited, TypeBCommited);
		
		String cadseName = cadseDefCommited.getName();
		IProject p = cadseDefCommited.getMainMappingContent(IProject.class);
		assertNotNull(p);
		assertEquals(cadseDefCommited.getQualifiedName(), p.getName());
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseName, cadseDefCommited.getQualifiedName());
		
		ContentItem ci = cadseDefCommited.getContentItem();
		assertNotNull(ci);
		assertEquals(ci, cadseDefCommited.getOutgoingItem(CadseGCST.ITEM_lt_CONTENTS, true));
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		String newName = senario.generator.newName();
		ItemDelta renameCadseDefItem = copy.getItem(cadseDefCommited);
		renameCadseDefItem.setName(newName);
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, renameCadseDefItem.getQualifiedName());
		copy.commit();
		assertEquals(newName, cadseDefCommited.getName());
		p = cadseDefCommited.getMainMappingContent(IProject.class);
		assertNotNull(p);
		assertEquals(cadseDefCommited.getQualifiedName(), p.getName());
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, cadseDefCommited.getQualifiedName());
		
	}

	private ItemDelta createLinkType(Item TypeACommited, Item TypeBCommited)
			throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta LinkA_TypeA = copy.createItem(CadseGCST.LINK, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		LinkA_TypeA.setName(static_generator.newName());
		LinkA_TypeA.createLink(CadseGCST.LINK_lt_DESTINATION, TypeBCommited);
		copy.commit();
		return LinkA_TypeA;
	}
	
	private Item createAttributeBool(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.BOOLEAN, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		copy.commit();
		return attrBool;
	}
	
	private Item createAttributeInt(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.INTEGER, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		copy.commit();
		return attrBool;
	}
	
	private Item createAttributeString(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.STRING, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		copy.commit();
		return attrBool;
	}
	
	private Item createAttributeLong(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrLong = copy.createItem(CadseGCST.LONG, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrLong.setName(static_generator.newName());
		copy.commit();
		return attrLong;
	}
	
	private Item createAttributeEnum(Item TypeACommited, Item et) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrLong = copy.createItem(CadseGCST.ENUM, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrLong.setName(static_generator.newName());
		attrLong.setOutgoingItem(CadseGCST.ENUM_lt_ENUM_TYPE, et);
		List<String> v = et.getAttribute(CadseGCST.ENUM_TYPE_at_VALUES_);
		attrLong.setAttribute(CadseGCST.ATTRIBUTE_at_DEFAULT_VALUE_, v.get(0));
		copy.commit();
		return attrLong;
	}

	private Item createItemType(Item dm) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta TypeA = copy.createItem(CadseGCST.ITEM_TYPE, dm, CadseGCST.DATA_MODEL_lt_TYPES);
		TypeA.setName(static_generator.newName());
		
		copy.commit();
		Item TypeACommited = senario.getLogicalWorkspace().getItem(TypeA.getId());
		
		
		return TypeACommited;
	}
	
	private Item createEnumType(Item dm) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta TypeA = copy.createItem(CadseGCST.ENUM_TYPE, dm, CadseGCST.DATA_MODEL_lt_ENUMS);
		TypeA.setName(static_generator.newName());
		ArrayList<String> values = new ArrayList<String>();
		values.add(static_generator.newLowerName(5));
		values.add(static_generator.newLowerName(6));
		values.add(static_generator.newName(null, null, 
				GeneratorName.F_FIRST_LOWER|GeneratorName.F_NEXT_LOWER|GeneratorName.F_NEXT_UPPER, 4, 8));
		TypeA.setAttribute(CadseGCST.ENUM_at_VALUES_, values);
		copy.commit();
		Item TypeACommited = senario.getLogicalWorkspace().getItem(TypeA.getId());
		
		
		return TypeACommited;
	}
	
	private Item createItemTypeAndCheck(Item dm) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta deltaTypeA = copy.createItem(CadseGCST.ITEM_TYPE, dm, CadseGCST.DATA_MODEL_lt_TYPES);
		deltaTypeA.setName(static_generator.newName());
		
		assertEquals(true, deltaTypeA.getAttribute(CadseGCST.ITEM_TYPE_at_IS_ROOT_ELEMENT_));
		assertEquals(true, deltaTypeA.getAttribute(CadseGCST.ITEM_TYPE_at_HAS_CONTENT_));
		assertEquals(false, deltaTypeA.getAttribute(CadseGCST.ITEM_TYPE_at_IS_ABSTRACT_));
		assertEquals(dm.getPartParent(), deltaTypeA.getOutgoingItem(CadseGCST.ITEM_TYPE_lt_CADSE_RUNTIME, true));
		
		Item managerTypeA = deltaTypeA.getIncomingItem(CadseGCST.MANAGER_lt_ITEM_TYPE);
		assertNotNull(managerTypeA);
		assertEquals("${#parent.qualified-name}{.}${#name}", managerTypeA.getAttribute(CadseGCST.MANAGER_at_LONG_NAME_TEMPLATE_));
		assertEquals("${#name}", managerTypeA.getAttribute(CadseGCST.MANAGER_at_DISPLAY_NAME_TEMPLATE_));
		assertEquals(deltaTypeA.getName()+"-manager", managerTypeA.getName());
		checkIncomingLinks(managerTypeA);
		assertEquals(managerTypeA, copy.getItem(managerTypeA.getQualifiedName()));
	
		copy.commit();
		Item ret = senario.getLogicalWorkspace().getItem(deltaTypeA.getId());
		
		return ret;
	}

	private Item createCadseDefinition(String name)
			throws CadseException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta cadseDef = copy.createItem(CadseGCST.CADSE_DEFINITION, null, null);
		assertNotNull(cadseDef);
		cadseDef.setName(name);
		cadseDef.setAttribute(CadseGCST.CADSE_DEFINITION_at_PACKAGENAME_, static_generator.newPackageName(3));
		cadseDef.setAttribute(CadseGCST.CADSE_DEFINITION_at_COMMENTARY_, static_generator.newName());
		cadseDef.setAttribute(CadseGCST.CADSE_DEFINITION_at_VENDOR_NAME_, static_generator.newName());
		cadseDef.setAttribute(CadseGCST.CADSE_DEFINITION_at_CADSE_NAME_, static_generator.newName());
		cadseDef.setAttribute(CadseGCST.CADSE_RUNTIME_at_DESCRIPTION_, static_generator.newName());
		copy.commit();
		
		Item cadseDefCommited = senario.getLogicalWorkspace().getItem(cadseDef.getId());
		return cadseDefCommited;
	}

	private void checkIncomingLinks(Item itemA) {
		for (Link l : itemA.getIncomingLinks()) {
			Link lout = l.getSource().getOutgoingLink(l.getLinkType(), l.getDestinationId());
			assertNotNull(lout);
			assertEquals(l, lout);
		}
		
		for (Link l : itemA.getOutgoingLinks()) {
			Link lin = l.getDestination().getIncomingLink(l.getLinkType(), l.getSourceId());
			if (lin == null)
				assertNotNull("not found incoming link : "+l, lin);
			assertEquals(l, lin);
		}
	}
	
		

}
