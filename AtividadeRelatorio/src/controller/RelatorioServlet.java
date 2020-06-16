package controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;
import persistence.GenericDao;

@WebServlet("/relatorio")
public class RelatorioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RelatorioServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		geraRelatorio(request, response);
	}
	private void geraRelatorio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String erro = "";
		String cliente = request.getParameter("cliente");
		String jasper = "WEB-INF/report/AtividadeRelatorio.jasper";
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("CLIENTE", cliente);
		
		byte[] bytes = null;
		
		ServletContext contexto = getServletContext();
		
		try {
			JasperReport relatorio = (JasperReport) JRLoader.loadObjectFromFile(contexto.getRealPath(jasper));
			bytes = JasperRunManager.runReportToPdf(relatorio, param, new GenericDao().getConnection());
		} catch (JRException e) {
			erro = e.getMessage();
		} finally {
			if(bytes != null) {
				response.setContentType("application/pdf");
				response.setContentLength(bytes.length);
				ServletOutputStream sos = response.getOutputStream();
				sos.write(bytes);
				sos.flush();
				sos.close();
			} else {
				RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
				request.setAttribute("erro", erro);
				rd.forward(request, response);
			}
		}
		
		
	}

}
