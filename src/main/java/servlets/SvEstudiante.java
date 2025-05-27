package servlets;

import com.google.gson.Gson;
import dao.EstudiantewebJpaController;
import dto.Estudianteweb;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SvEstudiante", urlPatterns = {"/SvEstudiante"})
public class SvEstudiante extends HttpServlet {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
    EstudiantewebJpaController estudianteController = new EstudiantewebJpaController(emf);
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Estudianteweb> estudiantes = estudianteController.findEstudiantewebEntities();
        String json = gson.toJson(estudiantes);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Estudianteweb estudiante = gson.fromJson(request.getReader(), Estudianteweb.class);
        try {
            estudianteController.create(estudiante);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Estudianteweb datos = gson.fromJson(request.getReader(), Estudianteweb.class);
        try {
            Estudianteweb estudiante = estudianteController.findEstudianteweb(datos.getCodiEstdWeb());
            if (estudiante != null) {
                estudiante.setNdniEstdWeb(datos.getNdniEstdWeb());
                estudiante.setAppaEstdWeb(datos.getAppaEstdWeb());
                estudiante.setApmaEstdWeb(datos.getApmaEstdWeb());
                estudiante.setNombEstdWeb(datos.getNombEstdWeb());
                estudiante.setFechNaciEstdWeb(datos.getFechNaciEstdWeb());
                estudiante.setLogiEstd(datos.getLogiEstd());
                estudiante.setPassEstd(datos.getPassEstd());
                estudianteController.edit(estudiante);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int cod = Integer.parseInt(request.getParameter("codiEstdWeb"));
            estudianteController.destroy(cod);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
