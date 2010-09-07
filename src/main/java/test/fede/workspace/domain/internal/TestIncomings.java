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

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

import fr.imag.adele.cadse.core.CadseGCST;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.cadse.core.transaction.AbstractLogicalWorkspaceTransactionListener;
import fr.imag.adele.cadse.core.transaction.LogicalWorkspaceTransaction;
import fr.imag.adele.cadse.core.transaction.delta.ItemDelta;

public class TestIncomings {
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
		senario.getLogicalWorkspace().addLogicalWorkspaceTransactionListener(new AbstractLogicalWorkspaceTransactionListener() {
			
			@Override
			public void notifyLoadedItem(
					LogicalWorkspaceTransaction workspaceLogiqueWorkingCopy,
					List<ItemDelta> loadedItems) {
				for (ItemDelta itemDelta : loadedItems) {
					if (itemDelta.getId().equals(UUID.fromString("25ffd055-8316-4436-850a-65de41805145"))) {
						System.out.println(itemDelta);;
					}
				}
			}
		});
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		senario.stop();
	}
	
	@Test
	public void testD() throws InterruptedException {
		
		while (true) {
			Item c = senario.getLogicalWorkspace().getItem(UUID.fromString("25ffd055-8316-4436-850a-65de41805145"));
			if (c != null) break;
			Thread.sleep(100);
		}
		senario.getLogicalWorkspace().getCadseDomain().beginOperation("test");
		try {
			Item c = senario.getLogicalWorkspace().getItem(UUID.fromString("25ffd055-8316-4436-850a-65de41805145"));
			for(Link l : c.getIncomingLinks(CadseGCST.ITEM_TYPE_lt_LINK_TYPE)) {
				Link lout = l.getSource().getOutgoingLink(CadseGCST.ITEM_TYPE_lt_LINK_TYPE, l.getDestinationId());
				assertNotNull(lout);
			}
		} finally {
			senario.getLogicalWorkspace().getCadseDomain().endOperation();
			
		}
		
	}

	
	//
}
