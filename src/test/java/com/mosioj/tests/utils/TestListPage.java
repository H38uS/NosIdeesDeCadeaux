package com.mosioj.tests.utils;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.mosioj.servlets.controllers.AbstractListes;
import com.mosioj.servlets.controllers.idees.MesListes;
import com.mosioj.servlets.controllers.relations.Page;
import com.mosioj.servlets.securitypolicy.SecurityPolicy;
import com.mosioj.utils.NotLoggedInException;

public class TestListPage {
	
	@Test
	public void testAbstractList() {
		TestList maListe = new TestList();
		System.out.println(maListe.getClass().getName() + " " + maListe.getMaxNumberOfResults());
		assertEquals(2, maListe.getPages(32).size());
		assertEquals(1, maListe.getPages(19).size());
		assertEquals(1, maListe.getPages(20).size());
		assertEquals(2, maListe.getPages(21).size());
		assertEquals(8, maListe.getPages(144).size());
	}

	@Test
	public void testMesListesList() {
		TestMesListes maListe = new TestMesListes();
		int maxNumberOfResults = maListe.getMaxNumberOfResults();
		System.out.println(maListe.getClass().getName() + " " + maxNumberOfResults);
		assertEquals(6, maListe.getPages(maxNumberOfResults*5 +1).size());
		assertEquals(1, maListe.getPages(maxNumberOfResults-1).size());
		assertEquals(1, maListe.getPages(maxNumberOfResults).size());
		assertEquals(2, maListe.getPages(maxNumberOfResults + 3).size());
		assertEquals(15, maListe.getPages(maxNumberOfResults*14+2).size());
	}
	
	private class TestMesListes extends MesListes {
		
		private static final long serialVersionUID = 1L;
		
		public int getMaxNumberOfResults() {
			return maxNumberOfResults;
		}
		
		@Override
		protected List<Page> getPages(int total) {
			return super.getPages(total);
		}
		
		@Override
		protected String getViewPageURL() {
			return null;
		}
		
		@Override
		protected String getCallingURL() {
			return null;
		}
		
		@Override
		protected String getSpecificParameters(HttpServletRequest req) {
			return null;
		}
		
		@Override
		protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException, NotLoggedInException {
			return 0;
		}
		
	}

	private class TestList extends AbstractListes<Object, SecurityPolicy> {
		
		private static final long serialVersionUID = 1L;

		public int getMaxNumberOfResults() {
			return maxNumberOfResults;
		}
		
		public TestList() {
			super(null);
		}

		@Override
		protected List<Page> getPages(int total) {
			return super.getPages(total);
		}

		@Override
		protected String getViewPageURL() {
			return null;
		}

		@Override
		protected String getCallingURL() {
			return null;
		}

		@Override
		protected String getSpecificParameters(HttpServletRequest req) {
			return null;
		}

		@Override
		protected int getTotalNumberOfRecords(HttpServletRequest req) throws SQLException, NotLoggedInException {
			return 0;
		}

		@Override
		protected List<Object> getDisplayedEntities(int firstRow, HttpServletRequest req) throws SQLException, NotLoggedInException {
			return null;
		}

		@Override
		public void ideesKDoPOST(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException {
		}

	}

}
