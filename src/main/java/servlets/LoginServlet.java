package servlets;


import dao.EstudiantewebJpaController;
import dto.Estudianteweb;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet(name="/LoginServlet", urlPatterns = "/LoginServlet")
public class LoginServlet extends HttpServlet {

    private EstudiantewebJpaController controller;

    @Override
    public void init() throws ServletException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
        controller = new EstudiantewebJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String id = (String)req.getParameter("codigo");
        EstudiantewebJpaController control = new EstudiantewebJpaController();
        Estudianteweb usu = control.findEstudiantewebByNdni(id);
        
        JSONObject obj = new JSONObject();
        
        obj.put("usenam", usu.getLogiEstd());
        obj.put("cod", usu.getPassEstd());
        resp.getWriter().print(obj.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String passwordParam = request.getParameter("password");

            String password;
        try {
            password =passwordParam;
        } catch (NumberFormatException e) {
            response.sendRedirect("login.html?error=invalid");
            return;
        }

        List<Estudianteweb> usuarios = controller.findEstudiantewebEntities();
        for (Estudianteweb u : usuarios) {
            if (u.getLogiEstd().equals(username) &&
                u.getPassEstd().equals(password) 
                ) {

                HttpSession session = request.getSession();
                session.setAttribute("usuario", u);
                response.sendRedirect("index.html");
                return;
            }
        }

        // Si no coincide ningún usuario
        response.sendRedirect("login.html?error=invalid");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
            StringBuilder jsonB = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                jsonB.append(linea);
            }
            JSONObject jsonObject = new JSONObject(jsonB.toString());
            
            String cod = jsonObject.getString("codigo");
            String contra = jsonObject.getString("nuevaContrasena");
            
            EstudiantewebJpaController control = new EstudiantewebJpaController();
            Estudianteweb usu = control.findEstudiantewebByNdni(cod);
            
            usu.setPassEstd(contra);
            
        try {
            control.edit(usu);
        } catch (Exception ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
    
}
