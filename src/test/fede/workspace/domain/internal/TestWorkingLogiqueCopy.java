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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.Messages;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.WorkspaceListener.ListenerKind;
import fr.imag.adele.cadse.core.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.delta.ItemDelta;
import fr.imag.adele.cadse.core.delta.LinkKey;
import fr.imag.adele.cadse.core.delta.LinkDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
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

		public void beforeCreatingItem(LogicalWorkspaceTransaction wl, ItemDelta item) throws CadseException {
			// LT_A_TO_D
			d = wl.createItem(TYPE_D, null, null);
			a_to_d = item.createLink(LT_A_TO_D, d);
		}

		public void action(Call call) throws CadseException {
			if (call.getType() == CallType.notifyCreatedItem && call.getOperItem().getType() == TYPE_A) {
				beforeCreatingItem(call.getLogicalWorkspaceTransaction(), call.getOperItem());
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
			if (call.getType() == CallType.notifyDeletedItem && call.getOperItem().getType() == TYPE_A) {
				beforeDeletingItem(call.getOperItem());
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
		a.setShortName(senario.newName());
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
		a.setShortName(senario.newName());
		copy.commit();
		MyItemDeltaListner myItemDeltaListner = new MyItemDeltaListner(ListenerKind.UI);
		a = senario.getLogicalWorkspace().getItem(a.getId());
		myItemDeltaListner.setCurrentItem(a);
		a.addListener(myItemDeltaListner, ChangeID.toFilter(ChangeID.CREATE_OUTGOING_LINK));

		copy = senario.getLogicalWorkspace().createTransaction();
		Item a2 = copy.createItem(TYPE_A, null, null);
		a2.setShortName(senario.newName());
		copy.commit();
		senario.getLogicalWorkspace().getCadseDomain().waitEndAsyncEvents(10000);
		Assert.assertTrue("Listener called", myItemDeltaListner.count == 1);

		copy = senario.getLogicalWorkspace().createTransaction();
		copy.getItem(a.getId()).setAttribute("As", "test");
		Item b = copy.createItem(TYPE_B, null, null);
		b.setShortName(senario.newName());
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

}
