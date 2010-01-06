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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CCadse;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CExtensionItemType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CItem;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CLink;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.CValuesType;
import fr.imag.adele.fede.workspace.as.initmodel.jaxb.ObjectFactory;
import fr.imag.adele.cadse.cadseg.contents.CadseDefinitionContent;
import fr.imag.adele.cadse.cadseg.generate.GenerateCadseDefinitionModel;
import fr.imag.adele.cadse.cadseg.managers.CadseDefinitionManager;
import fr.imag.adele.cadse.cadseg.managers.attributes.LinkManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.CreationDialogManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.ItemTypeManager;
import fr.imag.adele.cadse.cadseg.managers.dataModel.PageManager;
import fr.imag.adele.cadse.cadseg.managers.ui.FieldManager;
import fr.imag.adele.cadse.cadseg.operation.ImportCadseUtil;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.ChangeID;
import fr.imag.adele.cadse.core.content.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.Messages;
import fr.imag.adele.cadse.core.WorkspaceListener;
import fr.imag.adele.cadse.core.WorkspaceListener.ListenerKind;
import fr.imag.adele.cadse.core.attribute.BooleanAttributeType;
import fr.imag.adele.cadse.core.attribute.CheckStatus;
import fr.imag.adele.cadse.core.attribute.IAttributeType;
import fr.imag.adele.cadse.core.transaction.delta.ImmutableWorkspaceDelta;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;
import fr.imag.adele.cadse.core.transaction.delta.LinkKey;
import fr.imag.adele.cadse.core.transaction.delta.LinkDelta;
import fr.imag.adele.cadse.core.impl.CadseIllegalArgumentException;
import fr.imag.adele.cadse.core.key.Key;
import fr.imag.adele.cadse.core.key.SpaceKeyType;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.ui.IPageFactory;
import fr.imag.adele.cadse.core.ui.Pages;
import fr.imag.adele.cadse.core.var.ContextVariable;

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
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String)}.
	 *
	 * @throws CadseException
	 */
	@Test
	public void testCreateItemItemTypeItemLinkTypeUUIDStringString() throws CadseException {
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
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String)}.
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
	 * {@link fr.imag.adele.cadse.core.impl.internal.WorkingLogiqueCopy#createItem(fr.imag.adele.cadse.core.ItemType, fr.imag.adele.cadse.core.Item, fr.imag.adele.cadse.core.LinkType, fr.imag.adele.cadse.core.UUID, java.lang.String, java.lang.String)}.
	 *
	 * @throws CadseException
	 * @throws CoreException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 */
	// TODO not work @Test
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
	public void testCreateItemItemTypeItemLinkTypeUUIDStringString2() throws CadseException {
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
	 * Create a --> c (link of type part)
	 * try to delete parent --> fail
	 * try to set parent to null  --> fail
	 * try to change parent --> sucess
	 * 
	 * 	@Test
	 * @throws CadseException
	 * @throws CoreException
	 */
	@Test
	public void testCreateAndDeleteItemPart() throws CadseException, CoreException {
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		Item a = copy.createItem(TYPE_A, null, null);
		a.setName("a");
		copy.commit();
		
		assertNotNull(senario.getLogicalWorkspace().getItem(a.getId()));
		assertOutgoingLinksItem(senario.getLogicalWorkspace().getItem(a.getId()));
		
		a = a.getBaseItem();
		
		copy = senario.getLogicalWorkspace().createTransaction();
		Item c = copy.createItem(TYPE_C, a, LT_A_TO_C);
		c.setName("c");
		assertEquals(a, c.getPartParent());
		copy.commit();
		assertNotNull(senario.getLogicalWorkspace().getItem(c.getId()));
		assertNotNull(a.getOutgoingLink(LT_A_TO_C, c.getId()));
		c = c.getBaseItem();
		
		
		assertEquals(a, c.getOutgoingItem(CadseGCST.ITEM_lt_PARENT, true));
		assertEquals(a, c.getPartParent());
		
		
		ItemDelta cDelta;
		while(true) {
			try {
				copy = senario.getLogicalWorkspace().createTransaction();
				cDelta = copy.getItem(c);
				cDelta.getOutgoingLink(CadseGCST.ITEM_lt_PARENT).delete();
				copy.commit();
				break;
			} catch (CadseException e) {
				if (e.getMsg().equals(Messages.parent_must_be_set))
					break;
			}
			fail("delete link parent not fail !!!");
		}
		
		assertEquals(a, c.getOutgoingItem(CadseGCST.ITEM_lt_PARENT, true));
		assertEquals(a, c.getPartParent());
		assertNotNull(a.getOutgoingLink(LT_A_TO_C, c.getId()));
		
		copy = senario.getLogicalWorkspace().createTransaction();
		Item a2 = copy.createItem(TYPE_A, null, null);
		a2.setName("a2");
		copy.commit();
		
		a2 = a2.getBaseItem();
		
		copy = senario.getLogicalWorkspace().createTransaction();
		cDelta = copy.getItem(c);
		cDelta.setParent(a2, LT_A_TO_C);
		assertEquals(a2, cDelta.getPartParent());
		copy.commit();
		
		
		assertEquals(a2, c.getOutgoingItem(CadseGCST.ITEM_lt_PARENT, true));
		assertEquals(a2, c.getPartParent());
		assertNull(a.getOutgoingLink(LT_A_TO_C, c.getId()));		
		assertNotNull(a2.getOutgoingLink(LT_A_TO_C, c.getId()));
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
	public void testDeleteItemItemTypeItemLinkTypeUUIDStringString2() throws CadseException, CoreException {
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
		Key key = newaC.getKey();
		copy = senario.getLogicalWorkspace().createTransaction();
		a1 = copy.getItem(newaC.getId(), false);
		assertNotNull(a1);
		String k2 = senario.generator.newName();
		a1.setName(k2);
		assertNotSame(key, a1.getKey());
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
		
		Item et = createEnumType(dm,3);
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
	
	@Test
	public void testCheckAttribute() throws CadseException {

		// create a cadse def with item-type, link-type, page, attribute (bool, string, integer)
		String nameCadse = static_generator.newName();
		LogicalWorkspace lw = senario.getLogicalWorkspace();
		
		assertNull(lw.getItem(CadseGCST.CADSE_DEFINITION.getSpaceKeyType().computeKey(nameCadse, null)));
		Item cadseDefCommited = createCadseDefinition(nameCadse);
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);
		assertNotNull(dm);
		
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
		
		Item attrBool_s = createAttributeBool(TypeACommited);
		assertTrue(attrBool_s instanceof BooleanAttributeType);
		CheckStatus ret = ((BooleanAttributeType) attrBool_s).check(null, null);
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, "true");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, "false");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, "true");
		assertEquals(false, ((BooleanAttributeType) attrBool_s).getDefaultValue());
		ret = ((BooleanAttributeType) attrBool_s).check(null, "trufe");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, 120.0);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.must_be_a_boolean);
		
		Item attrBool_s_dv_true = createAttributeBool(TypeACommited, "true", null);
		assertTrue(attrBool_s_dv_true instanceof BooleanAttributeType);
		ret = ((BooleanAttributeType) attrBool_s_dv_true).check(null, null);
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_true).check(null, "true");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_true).check(null, "false");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_true).check(null, "true");
		assertEquals(true, ((BooleanAttributeType) attrBool_s_dv_true).getDefaultValue());
		ret = ((BooleanAttributeType) attrBool_s_dv_true).check(null, "trufe");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, 120.0);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.must_be_a_boolean);
		
		Item attrBool_s_dv_false = createAttributeBool(TypeACommited, "false", null);
		assertTrue(attrBool_s_dv_false instanceof BooleanAttributeType);
		ret = ((BooleanAttributeType) attrBool_s_dv_false).check(null, null);
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_false).check(null, "true");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_false).check(null, "false");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_dv_false).check(null, "true");
		assertEquals(false, ((BooleanAttributeType) attrBool_s_dv_false).getDefaultValue());
		ret = ((BooleanAttributeType) attrBool_s_dv_false).check(null, "trufe");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, 120.0);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.must_be_a_boolean);
		
		Item attrBool_s_cundef = createAttributeBool(TypeACommited, null, true);
		assertTrue(attrBool_s_cundef instanceof BooleanAttributeType);
		ret = ((BooleanAttributeType) attrBool_s_cundef).check(null, null);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.cannot_be_undefined);
		
		ret = ((BooleanAttributeType) attrBool_s_cundef).check(null, "true");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_cundef).check(null, "false");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_cundef).check(null, "true");
		assertEquals(false, ((BooleanAttributeType) attrBool_s_cundef).getDefaultValue());
		ret = ((BooleanAttributeType) attrBool_s_cundef).check(null, "trufe");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, 120.0);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.must_be_a_boolean);
		
		Item attrBool_s_notcundef = createAttributeBool(TypeACommited, null, false);
		assertTrue(attrBool_s_notcundef instanceof BooleanAttributeType);
		ret = ((BooleanAttributeType) attrBool_s_notcundef).check(null, null);
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_notcundef).check(null, "true");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_notcundef).check(null, "false");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s_notcundef).check(null, "true");
		assertEquals(false, ((BooleanAttributeType) attrBool_s_notcundef).getDefaultValue());
		ret = ((BooleanAttributeType) attrBool_s_notcundef).check(null, "trufe");
		assertNull(ret);
		ret = ((BooleanAttributeType) attrBool_s).check(null, 120.0);
		assertNotNull(ret);
		assertEquals(ret.getMessage(), fr.imag.adele.cadse.core.impl.attribute.Messages.must_be_a_boolean);
		
		Item attrInt1 = createAttributeInt(TypeACommited);
		Item attrString1= createAttributeString(TypeACommited);
		Item attrLong1 = createAttributeLong(TypeACommited);
		
		Item et = createEnumType(dm, 3);
		Item attrEnum1 = createAttributeEnum(TypeACommited, et);
		
		
		
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
	public void testCreateCadsegName() throws CadseException {
		
		String newName = static_generator.newName();
		Item cadseDefCommited = createCadseDefinition(newName);
		assertEquals(newName, cadseDefCommited.getName());
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, cadseDefCommited.getQualifiedName());
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, cadseDefCommited.getQualifiedName(true));
		
		IProject p = cadseDefCommited.getMainMappingContent(IProject.class);
		assertNotNull(p);
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, p.getName());
		IProject pbad = p.getWorkspace().getRoot().getProject(newName);
		assertFalse("Project "+pbad+" exists !!!",pbad.exists());
		
		IJavaProject jp = org.eclipse.jdt.core.JavaCore.create(p);
		assertNotNull(jp);
		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+newName, jp.getElementName());
		
		newName = static_generator.newPackageName(5);
		cadseDefCommited = createCadseDefinition(newName);
		assertEquals(newName, cadseDefCommited.getName());
		assertEquals(newName, cadseDefCommited.getQualifiedName());
		assertEquals(newName, cadseDefCommited.getQualifiedName(true));
		
		p = cadseDefCommited.getMainMappingContent(IProject.class);
		assertNotNull(p);
		assertEquals(newName, p.getName());
		pbad = p.getWorkspace().getRoot().getProject(CadseRuntime.CADSE_NAME_SUFFIX+newName);
		assertFalse("Project "+pbad+" exists !!!",pbad.exists());
		
		jp = org.eclipse.jdt.core.JavaCore.create(p);
		assertNotNull(jp);
		assertEquals(newName, jp.getElementName());
	}
	
	@Test
	public void testFailBadParent() throws CadseException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);
		
		LogicalWorkspaceTransaction copy = senario.getLogicalWorkspace().createTransaction();
		try {
			copy.createItem(CadseGCST.ENUM_TYPE, dm, CadseGCST.DATA_MODEL_lt_TYPES);
		} catch (CadseException e) {
			assertEquals(Messages.cannot_create_link_bad_link_type, e.getMsg());
			return;
		}
		fail("exception not raised");
			
	}
	
	@Test
	public void testFailDuplicateItemType() throws CadseException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);
		
		
		String n = static_generator.newName();
		createItemType(dm, n, null);
		try {
			createItemType(dm, n, null);
		} catch (CadseException e) {
			if (e.getMsg().equals(Messages.error_invalid_assignement_key_allready_exists))
				return;
		}	
		fail("exception not raised");
			
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
	
	@Test
	public void testGenerateShortNameCadseg() throws CadseException, CoreException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		ItemType TypeBCommited = createItemType(dm);
		TypeBCommited.setAttribute(CadseGCST.ITEM_TYPE_at_IS_ABSTRACT_, true);
		ItemDelta LinkA_TypeA = createLinkType(TypeACommited, TypeBCommited, LinkType.PART);
		Item TypeBbisCommited = createItemType(dm, null, TypeBCommited);
		Item creationDialog = ItemTypeManager.getCreationDialog(TypeBbisCommited);
		creationDialog.setAttribute(CadseGCST.CREATION_DIALOG_at_AUTOMATIC_SHORT_NAME_, true);
		creationDialog.setAttribute(CadseGCST.CREATION_DIALOG_at_GENERATE_AUTOMATIC_SHORT_NAME_, true);
		creationDialog.setAttribute(CadseGCST.CREATION_DIALOG_at_DEFAULT_SHORT_NAME_, "${id}");
		IJavaProject jp = cadseDefCommited.getMainMappingContent(IJavaProject.class);
		
		checkError(jp, new NullProgressMonitor());
		
		
	}
	
	@Test
	public void testHeritageContentCadseg() throws CadseException, CoreException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		Item TypeBCommited = createItemType(dm, null, (ItemType) TypeACommited);
		
		// create content
		
		createFileContent(TypeACommited, true, "a","a");
		createFileContent(TypeBCommited, true, "a","a");
		
		checkError(cadseDefCommited.getMainMappingContent(IJavaProject.class), null);
		
	}
	
	@Test
	public void testHeritagePageFieldNameCadseg() throws CadseException, CoreException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		Item cd = ItemTypeManager.getCreationDialog(TypeACommited);
		assertNotNull(cd);
		Collection<Item> pages = CreationDialogManager.getPages(cd);
		assertEquals(1, pages.size());
		Item p = pages.iterator().next();
		assertNotNull(p);
		Collection<Item> fields = PageManager.getFieldsAll(p);
		assertEquals(1, fields.size());
		Item f = fields.iterator().next();
		assertNotNull(f);
		Item a = FieldManager.getAttribute(f);
		assertNotNull(a);
		assertEquals(CadseGCST.ITEM_at_NAME_, a);
		
		Item TypeBCommited = createItemType(dm, null, (ItemType) TypeACommited);
		cd = ItemTypeManager.getCreationDialog(TypeBCommited);
		assertNotNull(cd);
		pages = CreationDialogManager.getPages(cd);
		assertEquals(1, pages.size());
		p = pages.iterator().next();
		assertNotNull(p);
		fields = PageManager.getFieldsAll(p);
		assertEquals(0, fields.size());
		
		checkError(cadseDefCommited.getMainMappingContent(IJavaProject.class), null);
		
	}
	
	/** test modification for extension */
	@Test
	public void testModificationPageForExtension() throws CadseException, CoreException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item TypeACommited = createItemType(dm);
		Item extTypeA = createExtItemType(dm, null, (ItemType) TypeACommited);
		
		Item md = extTypeA.getOutgoingItem(CadseGCST.ABSTRACT_ITEM_TYPE_lt_MODIFICATION_DIALOG, true);
		assertNotNull(md);
		Collection<Item> modPages = md.getOutgoingItems(CadseGCST.MODIFICATION_DIALOG_lt_PAGES, true);
		assertEquals(1, modPages.size());
		Item modPage1 = modPages.iterator().next();
		assertNotNull(modPage1);
		assertTrue(PageManager.isModificationPage(modPage1));
		
		modPages = extTypeA.getOutgoingItems(CadseGCST.ITEM_TYPE_lt_MODIFICATION_PAGES, true);
		assertEquals(1, modPages.size());
		assertEquals(modPage1, modPages.iterator().next());
		
		modPages = extTypeA.getOutgoingItems(CadseGCST.ITEM_TYPE_lt_CREATION_PAGES, true);
		assertEquals(1, modPages.size());
		
		CCadse cadsexml = GenerateCadseDefinitionModel.generateCADSE(cadseDefCommited);
		List<CExtensionItemType> extTypes = cadsexml.getExtItemType();
		assertEquals(1, extTypes.size());
		CExtensionItemType extTypaAxml = extTypes.get(0);
		assertNotNull(extTypaAxml);
		assertEquals(1, extTypaAxml.getCreationPages().getPage().size());
		assertEquals(1, extTypaAxml.getModificationPages().getPage().size());
		assertNotNull(extTypaAxml.getModificationPages().getPage().get(0));
		assertEquals(IPageFactory.PAGE_PROPERTY_ITEM, extTypaAxml.getModificationPages().getPage().get(0).getCas());
		
		checkError(cadseDefCommited.getMainMappingContent(IJavaProject.class), null);
		
	}
	
	/** test enum  */
	@Test
	public void testEnum() throws CadseException, CoreException {
		
		Item cadseDefCommited = createCadseDefinition(static_generator.newName());
		
		Item dm = cadseDefCommited.getOutgoingItem(CadseGCST.CADSE_DEFINITION_lt_DATA_MODEL, true);

		assertEquals(CadseRuntime.CADSE_NAME_SUFFIX+cadseDefCommited.getName(), cadseDefCommited.getQualifiedName());
		
		Item enumType = createEnumType(dm, 15);
		Item TypeACommited = createItemType(dm);
		Item attrEnum1 = createAttributeEnum(TypeACommited, enumType);
		
		checkError(cadseDefCommited.getMainMappingContent(IJavaProject.class), null);
		
	}

	public void checkError(IJavaProject jp, IProgressMonitor monitor) throws CoreException {
		jp.getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		if (!jp.hasBuildState())
			return ;
		
		IMarker[] markers = jp.getProject().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		if (markers == null) return ;
		StringBuilder errors = new StringBuilder();
		for (IMarker iMarker : markers) {
			Integer severity = (Integer) iMarker.getAttribute(IMarker.SEVERITY);
			if (severity != null && severity == IMarker.SEVERITY_ERROR) {
				errors.append(iMarker.getAttribute(IMarker.MESSAGE)).append("\n");
			}
		}
		if (errors.length() != 0) {
			fail("compilation error on "+jp.getElementName()+"\n"+errors);
		}
	}
	private Item createFileContent(Item typeACommited, boolean ext,
			String fileName, String filePath) throws CadseException {
		Item managerA = ItemTypeManager.getManager(typeACommited);
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta fc = copy.createItem(CadseGCST.FILE_CONTENT_MODEL, managerA, CadseGCST.MANAGER_lt_CONTENT_MODEL);
		fc.setAttribute(CadseGCST.FILE_CONTENT_MODEL_at_FILE_NAME_, fileName);
		fc.setAttribute(CadseGCST.FILE_CONTENT_MODEL_at_FILE_PATH_, filePath);
		fc.setAttribute(CadseGCST.CONTENT_ITEM_TYPE_at_EXTENDS_CLASS_, ext);
		copy.commit();
		return fc.getBaseItem();
	}
	
	private ItemDelta createLinkType(Item TypeACommited, Item TypeBCommited)
	throws CadseException {
		return createLinkType(TypeACommited, TypeBCommited, 0);
	}
	
	private ItemDelta createLinkType(Item TypeACommited, Item TypeBCommited, int kind)
			throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta LinkA_TypeA = copy.createItem(CadseGCST.LINK, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		if ((kind & LinkType.PART) != 0) {
			LinkManager.setPartAttribute(LinkA_TypeA, true);
		}
		if ((kind & LinkType.AGGREGATION) != 0) {
			LinkManager.setAggregationAttribute(LinkA_TypeA, true);
		}
		if ((kind & LinkType.REQUIRE) != 0) {
			LinkManager.setRequireAttribute(LinkA_TypeA, true);
		}
		LinkA_TypeA.setName(static_generator.newName());
		LinkA_TypeA.createLink(CadseGCST.LINK_lt_DESTINATION, TypeBCommited);
		copy.commit();
		return LinkA_TypeA;
	}
	
	private Item createAttributeBool(Item TypeACommited) throws CadseException {
		return createAttributeBool(TypeACommited, null, null);
	}
	
	private Item createAttributeBool(Item TypeACommited, String dv, Boolean cannotbeundefined) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.BOOLEAN, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		if (cannotbeundefined != null)
			attrBool.setAttribute(CadseGCST.ATTRIBUTE_at_CANNOT_BE_UNDEFINED_, cannotbeundefined);
		if (dv != null)
			attrBool.setAttribute(CadseGCST.ATTRIBUTE_at_DEFAULT_VALUE_, dv);
		
		copy.commit();
		return attrBool.getBaseItem();
	}
	
	private Item createAttributeInt(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.INTEGER, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		copy.commit();
		return attrBool.getBaseItem();
	}
	
	private Item createAttributeString(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrBool = copy.createItem(CadseGCST.STRING, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrBool.setName(static_generator.newName());
		copy.commit();
		return attrBool.getBaseItem();
	}
	
	private Item createAttributeLong(Item TypeACommited) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta attrLong = copy.createItem(CadseGCST.LONG, TypeACommited, CadseGCST.ABSTRACT_ITEM_TYPE_lt_ATTRIBUTES);
		attrLong.setName(static_generator.newName());
		copy.commit();
		return attrLong.getBaseItem();
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
		return attrLong.getBaseItem();
	}

	private ItemType createItemType(Item dm) throws CadseException {
		return createItemType(dm, null, null);
	}
	
	
	
	private ItemType createItemType(Item dm, String name, ItemType superType) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta newType = copy.createItem(CadseGCST.ITEM_TYPE, dm, CadseGCST.DATA_MODEL_lt_TYPES);
		if (name == null)
			name = static_generator.newName();
		newType.setName(name);
		if (superType != null)
			newType.setOutgoingItem(CadseGCST.ITEM_TYPE_lt_SUPER_TYPE, superType);
		copy.commit();
		
		return (ItemType) newType.getBaseItem();
	}
	
	private Item createEnumType(Item dm, int nbValues) throws CadseException {
		LogicalWorkspaceTransaction copy;
		String[] values = new String[nbValues];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = static_generator.newName(null, null, 
					GeneratorName.F_FIRST_LOWER|GeneratorName.F_NEXT_LOWER|GeneratorName.F_NEXT_UPPER, 4, 8);
		}
		return createEnumType(dm, static_generator.newName(), 
				values);
	}
	
	private Item createEnumType(Item dm, String name, String... values) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta enumType = copy.createItem(CadseGCST.ENUM_TYPE, dm, CadseGCST.DATA_MODEL_lt_ENUMS);
		if (name == null)
			name = static_generator.newName();
		enumType.setName(name);
		enumType.setAttribute(CadseGCST.ENUM_TYPE_at_VALUES_, new ArrayList<String>(Arrays.asList(values)));
		copy.commit();		
		return enumType.getBaseItem();
	}
	
	private Item createExtItemType(Item dm, String name, ItemType refType) throws CadseException {
		LogicalWorkspaceTransaction copy;
		copy = senario.getLogicalWorkspace().createTransaction();
		ItemDelta extType = copy.createItem(CadseGCST.EXT_ITEM_TYPE, dm, CadseGCST.DATA_MODEL_lt_TYPES);
		if (name == null)
			name = static_generator.newName();
		extType.setName(name);
		extType.setOutgoingItem(CadseGCST.EXT_ITEM_TYPE_lt_REF_TYPE, refType);
		copy.commit();
		return extType.getBaseItem();
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
	

	@Test
	public void testImportCadseg() throws CadseException, IOException, JAXBException {
		URL importCadseZip = TestActivator.findResource("ImportCadse.zip");
		Item cadse = ImportCadseUtil.importCadse(null, importCadseZip.openStream());
		assertNotNull(cadse);
		
		generateAssert(cadse, "cadseg");
	}
	
	@Test
	public void testImportMind() throws CadseException, IOException, JAXBException {
		URL importCadseZip = TestActivator.findResource("Model.Workspace.mind-src.zip");
		Item cadse = ImportCadseUtil.importCadse(null, importCadseZip.openStream());
		assertNotNull(cadse);
		
		generateAssert(cadse, "mind");
	}
	

	private void generateAssert(Item cadse, String name) throws JAXBException {
		ObjectFactory factory = new ObjectFactory();
		CCadse cadseJaxb = factory.createCCadse();
		cadseJaxb.setName(cadse.getQualifiedName());
		StringWriter structWriter = new StringWriter();

		
		gen(cadseJaxb, cadse,factory, structWriter, "");
		
		FileUtils.write(structWriter.toString(), new File("/home/chomats/ws/cadse-g/TEST.CU.Workspace.Workspace/struct."+name+".txt"));		
		StringWriter writer = new StringWriter();

		JAXBContext jc = JAXBContext.newInstance("fr.imag.adele.fede.workspace.as.initmodel.jaxb");
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(cadseJaxb, writer);
		
		FileUtils.write(writer.toString(), new File("/home/chomats/ws/cadse-g/TEST.CU.Workspace.Workspace/cadse."+name+".xml"));		
		
		
	}

	private void gen(CCadse cadseJaxb, Item anItem, ObjectFactory factory, StringWriter structWriter, String tab) {
		if (structWriter.toString().contains("#invert_part_"))
			fail("contains ref #invert_part_");
		
		CItem itemJaxb = factory.createCItem();
		cadseJaxb.getItem().add(itemJaxb);
		itemJaxb.setId(anItem.getId().toString());
		itemJaxb.setIsHidden(anItem.isHidden());
		itemJaxb.setIsReadonly(anItem.isReadOnly());
		itemJaxb.setShortName(anItem.getName());
		
		if (anItem.getName().contains("#invert_part_"))
			fail("contains ref #invert_part_");
		
		structWriter.append(tab).append(anItem.getType().getName()).append(" ").append(anItem.getName()).append("<");
		
		IAttributeType<?>[] attTypes = anItem.getType().getAllAttributeTypes();
		for (int i = 0; i < attTypes.length; i++) {
			if (attTypes[i] instanceof LinkType) continue;
			
			Object v = anItem.getAttribute(attTypes[i]);
			if (v == null) continue;
			
			CValuesType cv = factory.createCValuesType();
			cv.setKey(attTypes[i].getName());
			cv.setId(attTypes[i].getId().toString());
			cv.setValue(v.toString());
			
			itemJaxb.getAttributesValue().add(cv);
			
			structWriter.append(attTypes[i].getName()).append("='");
			structWriter.append(v.toString()).append("' ");
		}
		structWriter.append(">\n");
		
		if (anItem.getQualifiedName() != null)
			itemJaxb.setUniqueName(anItem.getQualifiedName());
		
		for (Link l :  anItem.getOutgoingLinks()) {
			if (l.getLinkType().getName().contains("#invert_part_"))
				fail("contains ref #invert_part_");
			if (l.getDestination().getName().contains("#invert_part_"))
				fail("contains ref #invert_part_");
			CLink linkJaxb=factory.createCLink();
			Item dest = l.getDestination();
			linkJaxb.setDestinationId(dest.getId().toString());
			linkJaxb.setType(l.getLinkType().getName());
			
			itemJaxb.getLink().add(linkJaxb );
			if (l.getLinkType().isPart() && l.isLinkResolved()) {
				gen(cadseJaxb, l.getDestination(), factory, structWriter, tab+"  ");
			} else {
				structWriter.append(tab).append("  --> ").append(dest.getType().getName()).append(" ").append(dest.getName()).append("\n");
			}
		}
		
		
		
	}

}
