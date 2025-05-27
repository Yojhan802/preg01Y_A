/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servlets;

import dao.EstudiantewebJpaController;
import dto.Estudianteweb;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author yojha
 */
@WebServlet("/BuscarEstudianteServlet")
public class BuscarEstudianteServlet extends HttpServlet {
    private EstudiantewebJpaController estudianteController;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("NombreDeTuUnidadDePersistencia"); // cambia por tu PU
            getServletContext().setAttribute("emf", emf);
        }
        estudianteController = new EstudiantewebJpaController(emf);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Estudianteweb user = (Estudianteweb) session.getAttribute("usuario");

        if (user == null) {
            response.sendRedirect("login.jsp"); // Usuario no logueado
            return;
        }

        String claveActual = request.getParameter("claveActual");
        String nuevaClave = request.getParameter("nuevaClave");
        String confirmarClave = request.getParameter("confirmarClave");

        if (claveActual == null || nuevaClave == null || confirmarClave == null ||
            claveActual.isEmpty() || nuevaClave.isEmpty() || confirmarClave.isEmpty()) {
            response.getWriter().println("Todos los campos son obligatorios.");
            return;
        }

        if (!user.getPassEstd().equals(claveActual)) {
            response.getWriter().println("La clave actual no es correcta.");
            return;
        }

        if (!nuevaClave.equals(confirmarClave)) {
            response.getWriter().println("La nueva clave no coincide con la confirmación.");
            return;
        }

        try {
            user.setPassEstd(nuevaClave);
            estudianteController.edit(user);
            response.getWriter().println("La clave se actualizó correctamente.");
        } catch (Exception e) {
            response.getWriter().println("Error al actualizar la clave: " + e.getMessage());
        }
    }
}

