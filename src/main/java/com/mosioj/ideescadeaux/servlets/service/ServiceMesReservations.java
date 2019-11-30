package com.mosioj.ideescadeaux.servlets.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.ideescadeaux.model.Idee;
import com.mosioj.ideescadeaux.servlets.securitypolicy.generic.AllAccessToPostAndGet;
import com.mosioj.ideescadeaux.servlets.service.response.ServiceResponse;

@WebServlet("/protected/service/mes_reservations")
public class ServiceMesReservations extends AbstractServiceGet<AllAccessToPostAndGet> {

	private static final long serialVersionUID = 2763424501732173771L;

	/**
	 * Class constructor.
	 */
	public ServiceMesReservations() {
		super(new AllAccessToPostAndGet());
	}

	@Override
	public void ideesKDoGET(HttpServletRequest request,
							HttpServletResponse response) throws ServletException, SQLException, IOException {
		List<Idee> idees = model.idees.getIdeasWhereIDoParticipateIn(thisOne);
		response.getOutputStream().print(new ServiceResponse(true, idees, true, isAdmin(request)).asJSon(response));
	}

}
